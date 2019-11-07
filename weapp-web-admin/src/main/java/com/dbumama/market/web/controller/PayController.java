package com.dbumama.market.web.controller;

import com.dbumama.market.alipay.AlipayConfig;
import com.dbumama.market.alipay.AlipayNotify;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.dbumama.weixin.api.CompTemplateMsgApi;
import com.dbumama.weixin.api.TemplateData;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.JFinal;
import com.jfinal.core.NotAction;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.utils.RequestUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

@RequestMapping(value="pay")
public class PayController extends BaseAuthUserController{

	//正在处理的用户订单
	public static Set<String> userTradeSet = new HashSet<String>();
	
	//返回成功的xml给微信
	static final String TO_RES_WEIXIN = "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
			+ "<return_msg><![CDATA[OK]]></return_msg></xml>";
	
	@RPCInject
	private PurchaseService purchaseService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private AuthUserAppService userAppService;
	@RPCInject
	private SelleruserRechargeRcdService selleruserRechargeRcdService;
	@RPCInject
	private AppService appService;
	@RPCInject
	private SellerUserService sellerUserService;
	@RPCInject
	private PayService payService;
	@RPCInject
	private AppOrderService appOrderService;
	@RPCInject
	private AuthUserAppService authUserAppService;
	@RPCInject
	private AuthCertService authCertService;
	
	public void index(){
		setAttr("purchases", purchaseService.find());
		PurchaseOrder porder = purchaseService.getUnpayPurchaseOrderByModule(getSellerId(), getAuthUserId());
		setAttr("porder", porder);
		if(porder != null){
			Purchase purch = purchaseService.findById(porder.getPurchaseId());
			setAttr("purch", purch);
		}
		render("pay_index.html");
	}
	
	//检查微信支付是否支付成功
	@Before(POST.class)
	public void checkPay(String tradeNo){
		AppOrder appOrder = appOrderService.findByTradeNo(tradeNo);
		
		if(appOrder == null){
			renderSuccess();			
		}else{
			renderSuccess(appOrder.getId());
		}
	}
	
	public void qrcode(Long pid, Long appId, String expirTime){
		App app = appService.findById(appId);
		String tradeNo = getTradeNo(appId, expirTime);
		setAttr("tradeNo", tradeNo);
		Purchase purchase = purchaseService.findById(pid);
		BigDecimal payFee = new BigDecimal(purchase.getExpiresIn()).divide(new BigDecimal(30)).multiply(new BigDecimal(purchase.getPayFee())).setScale(2, BigDecimal.ROUND_HALF_UP);
		String url = payService.prepareToPay4pc(tradeNo, payFee, "购买"+app.getAppName()+"应用" + purchase.getName(), RequestUtil.getIpAddress(getRequest()));
		renderJson(Ret.ok().set("url", url).set("tradeNo", tradeNo));
	}
	
	@NotAction
	private synchronized String getTradeNo(Long appId, String expirTime){
		String scenceId = "f";
		String scenceIdStr = String.valueOf(System.currentTimeMillis());
		//规则        
		// 随机时间戳_公众号数据id_购买的插件应用id_购买时长
		// 比如 y1234567890_117_125_30 即公众号id为117的购买应用id为125的应用30天
		scenceId = scenceId + scenceIdStr.substring(0, 10) + "_" + getAuthUserId()+"_"+appId+"_"+expirTime;
		return scenceId;
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "appId", message = "应用id不能为空"),
	})
	public void test(Long appId){
		AuthUserApp authUserApp = authUserAppService.findByApp(getAuthUserId(), appId);
		if(authUserApp != null){
			renderFail("已经使用过该应用");
			return;
		}
		
		authUserApp = new AuthUserApp();
		try {
			authUserApp
			.setAuthUserId(getAuthUserId())
			.setAppId(appId).setStartDate(new Date())
			.setEndDate(DateTimeUtil.FORMAT_YYYY_MM_DD.parse(DateTimeUtil.getNextDateStringAddDay(3)))
			.setActive(true)
			.setCreated(new Date())
			.setUpdated(new Date());
			authUserAppService.save(authUserApp);
			renderSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			renderFail(e.getMessage());
		}
	}
	
	@NotAction
	private String buildRequest(String action) {
        //待请求参数数组
        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form id=\"wxpaysubmit\" name=\"wxpaysubmit\" action=\"" + JFinal.me().getContextPath() + action
                      + "_input_charset=" + AlipayConfig.input_charset + "\" method=\"POST"
                      + "\">");

        sbHtml.append("<script>document.forms['wxpaysubmit'].submit();</script>");

        return sbHtml.toString();
    }
	
	@Clear
	@EnableLimit(rate=1, fallback="renderAliDefault")
	public void alipayCallBack(){
		Map<String,String> params = new HashMap<String,String>();
		HttpServletRequest request = getRequest();
		Map<String, String []> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号
		String out_trade_no = getPara("out_trade_no");
		//支付宝交易号
		String trade_no = getPara("trade_no");
		//交易状态
		String trade_status = getPara("trade_status");
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		
		//计算得出通知验证结果
		boolean verify_result = AlipayNotify.verify(params);
		
		if(!verify_result){//验证成功
			rendFailedJson("签名错误");
			return;
		}
		
		synchronized (userTradeSet) {
			if(userTradeSet.contains(out_trade_no)){
				//正在处理的订单数据，本次不处理
				renderText("success");
				return;
			}
			userTradeSet.add(out_trade_no);
		}
		if(trade_status.equals("TRADE_FINISHED")){
			//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序
				
			//注意：
			//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
			
		} else if (trade_status.equals("TRADE_SUCCESS")){
			//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序
			purchaseService.callBack(out_trade_no, trade_no);
			
			//注意：
			//付款完成后，支付宝系统发送该交易状态通知
		}
		userTradeSet.remove(out_trade_no);
		renderText("success");
	}
	
	@Clear
	@EnableLimit(rate=1, fallback="renderWechatDefault")
	public void wxpayNotify(){
		log.info("===================pc微信扫码支付成功回调");
		String resultXml = getRawData();
		if(StrKit.isBlank(resultXml)){
			renderNull();
			return;
		}
		SAXReader saxReader = new SAXReader();
		try {
			saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		} catch (SAXException e) {
			e.printStackTrace();
			renderNull();
			return;
		}
		
		InputSource source = new InputSource(new StringReader(resultXml));
		source.setEncoding(JFinal.me().getConstants().getEncoding());

		Document doc = null;
		try {
			doc = saxReader.read(source);
		} catch (DocumentException e) {
			renderNull();
			return;
		}
		Element root = doc.getRootElement();
		//校验签名
		TreeMap<String, Object> params = new TreeMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<Element> elements = root.elements();
		for(Element e : elements){
			params.put(e.getName(), e.getTextTrim());
		}

		final String authAppId = (String) params.get("appid"); 
		AuthUser authUser = authUserService.getAuthUserByAppId(authAppId);
		if(authUser == null){
			log.error("[" + authAppId + "]授权公众号/小程序不存在");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		final String tenpaySign = root.elementText("sign").toUpperCase();
		if(payService.checkSign(params, tenpaySign)){
			log.error("微信支付异步通知签名错误");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		log.info("成功回调，签名正确");
		
		//根据out_trade_no 判断订单支付来源
		final String tradeNo = (String) params.get("out_trade_no");
		final String transactionId = (String) params.get("transaction_id");
		
		synchronized (userTradeSet) {
			if(userTradeSet.contains(tradeNo)){
				//正在处理的订单数据，本次不处理
				renderText("success");
				return;
			}
			userTradeSet.add(tradeNo);
		}
		
		try {
			if(tradeNo.startsWith("e")){
				//用户充值
				String id = tradeNo.split("_")[1];
				String payFee = (String)params.get("total_fee");
				SelleruserRechargeRcd selleruserRechargeRcd = selleruserRechargeRcdService.findByTradeNo(tradeNo);
				if(selleruserRechargeRcd == null){
					selleruserRechargeRcd = new SelleruserRechargeRcd();
					selleruserRechargeRcd.setSellerId(Long.valueOf(id))
					.setBalance(new BigDecimal(payFee).divide(new BigDecimal(100)).setScale(2))
					.setTradeNo(tradeNo).setStatus(1).setTransactionId(transactionId)
					.setActive(true).setCreated(new Date()).setUpdated(new Date());
					selleruserRechargeRcdService.save(selleruserRechargeRcd);
					
					//更新用户账户余额
					SellerUser seller = sellerUserService.findById(id);
					if(seller != null){
						BigDecimal oldBalance = seller.getBalance() == null ? new BigDecimal(0) : seller.getBalance();
						BigDecimal newBalance = oldBalance.add(new BigDecimal(payFee).divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
						seller.setBalance(newBalance);
						sellerUserService.update(seller);
					}
				}
			}else if(tradeNo.startsWith("f")){
				
				String tradnoArr [] = tradeNo.split("_");
				String authUserId = tradnoArr[1];
				String appId = tradnoArr[2];
				String expirTime = tradnoArr[3];
				
				AuthUser orderAppUser = authUserService.findById(authUserId);
				
				//购买插件
				// 比如 y1234567890_117_125_30 即公众号id为117的购买应用id为125的应用30天
				AppOrder appOrder = appOrderService.findByTradeNo(tradeNo);
				App app = appService.findById(Long.valueOf(appId));
				if(appOrder == null){
					String payFee = (String)params.get("total_fee");//支付金额
					appOrder = new AppOrder();
					appOrder.setAuthUserId(Long.valueOf(authUserId))
					.setAppId(Long.valueOf(appId))
					.setTradeNo(tradeNo)
					.setTransactionId(transactionId)
					.setPayFee(payFee)
					.setExpirTime(expirTime)
					.setActive(true)
					.setCreated(new Date())
					.setUpdated(new Date());
					//插入购买记录
					appOrderService.save(appOrder);
					
					//插入app
					AuthUserApp authUserApp = authUserAppService.findByApp(Long.valueOf(authUserId), Long.valueOf(appId));
					
					if(authUserApp == null){
						authUserApp = new AuthUserApp();
						authUserApp.setAuthUserId(Long.valueOf(authUserId))
						.setAppId(Long.valueOf(appId))
						.setStartDate(new Date())
						.setEndDate(DateTimeUtil.FORMAT_YYYY_MM_DD.parse(DateTimeUtil.getNextDateStringAddDay(Integer.valueOf(expirTime))))
						.setActive(true).setCreated(new Date()).setUpdated(new Date());
						authUserAppService.save(authUserApp);
					}else{
						
						Date endDate = authUserApp.getEndDate();
						if(endDate.before(new Date())){
							//过期
							String endDateStr = DateTimeUtil.getNextDateStringAddDay(Integer.valueOf(expirTime));
							endDate = DateTimeUtil.FORMAT_YYYY_MM_DD.parse(endDateStr);
						}else{
							//未过期
							endDate = DateTimeUtil.FORMAT_YYYY_MM_DD.parse(DateTimeUtil.getNextDateStringAddDay(DateTimeUtil.FORMAT_YYYY_MM_DD.format(authUserApp.getEndDate()), Integer.valueOf(expirTime)));
						}
						
						authUserApp.setEndDate(endDate).setUpdated(new Date());
						authUserAppService.update(authUserApp);
					}
					
					//发送购买插件通知
					//点步软件公众号
					AuthUser dbumamaAuthUser = authUserService.getAuthUserByAppId(WeappConstants.WECHAT_LOGIN_APPID);
					
					TemplateData templateData = TemplateData.New().setTemplate_id("H5Rbymr27uY_NHXYk5zr3QhmEQRknbzPyYJ6Dbaapts")
							.setTouser("oj9Un06knAHNgDti45BpAbBfn00g")
							.setUrl("http://www.dbumama.com")
							.add("first", orderAppUser.getNickName() + "开通" + app.getAppName() + expirTime + "天", "#173177")
							.add("keyword1", DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.format(appOrder.getUpdated()), "#173177")
							.add("keyword2", "微信支付", "#173177")
							.add("keyword3", appOrder.getTransactionId())
							.add("remark", "支付金额" + new BigDecimal(appOrder.getPayFee()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "元", "#173177");
					CompTemplateMsgApi.send(authUserService.getAccessToken(dbumamaAuthUser), templateData.build());
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			userTradeSet.remove(tradeNo);
		}
		renderText(TO_RES_WEIXIN, "text/xml");
		
	}
	
	public void renderWechatDefault(){
		renderText(TO_RES_WEIXIN, "text/xml");
	}
	
	@Clear
	@EnableLimit(rate=1, fallback="renderAliDefault")
	public void alipayNotify(){
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		HttpServletRequest request = getRequest();
		Map<String, String []> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号
		String out_trade_no = getPara("out_trade_no");
		//支付宝交易号
		String trade_no = getPara("trade_no");
		//交易状态
		String trade_status = getPara("trade_status");
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		if(!AlipayNotify.verify(params)){//验证成功
			System.out.println("签名失败");
			return;
		}
		synchronized (userTradeSet) {
			if(userTradeSet.contains(out_trade_no)){
				//正在处理的订单数据，本次不处理
				renderText("success");
				return;
			}
			userTradeSet.add(out_trade_no);
		}
		if(trade_status.equals("TRADE_FINISHED")){
			//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序
				
			//注意：
			//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
			
		} else if (trade_status.equals("TRADE_SUCCESS")){
			//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序
			purchaseService.callBack(out_trade_no, trade_no);
			//注意：
			//付款完成后，支付宝系统发送该交易状态通知
		}
		userTradeSet.remove(out_trade_no);
		renderText("success");
	}
	
	public void renderAliDefault(){
		renderText("success");
	}
	
}
