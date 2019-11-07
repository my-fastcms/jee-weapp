package com.dbumama.market.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.utils.SignKit;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.dbumama.market.web.core.utils.IpKit;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


@RequestMapping(value = "pay")
public class PayController extends BaseApiController{
	//返回成功的xml给微信
	static final String TO_RES_WEIXIN = "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
			+ "<return_msg><![CDATA[OK]]></return_msg></xml>";
	
	//正在处理的用户订单
	public static Set<String> userTradeSet = new HashSet<String>();
	
	@RPCInject
	private OrderService orderService;
	@RPCInject
	private OrderGroupService orderGroupService;
	@RPCInject
	private AreaService areaService;
	@RPCInject
	private BuyerReceiverService receiverService;
	@RPCInject
	private PayService payService;
	@RPCInject
	private BuyerUserService buyerUserService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private AuthCertService authCertService;
	@RPCInject
	private ProductService productService;
	
	/**
	 * 订单结算
	 */
	public void balance (){
		JSONObject result = new JSONObject();
		final String items = getJSONPara("items");
		final Long receiverId=getJSONParaToLong("receiverId");
		result.put("img_domain", getImageDomain());
		BuyerReceiver buyerReceiver=null;
		
		try {
			OrderResultDto orderDto = orderService.balance(getBuyerId(), buyerReceiver == null ? null : buyerReceiver.getId(), items);
			result.put("order", orderDto);
			
			List<OrderItemResultDto> orderItems = orderDto.getOrderItems();
			if(orderItems != null && orderItems.size() == 1){
				Product pro = productService.findById(orderItems.get(0).getProductId());
				//自提商品直接返回数据
				if(pro.getIsPickUp() != null && pro.getIsPickUp()){
					result.put("shop", "true");
					rendSuccessJson(result);
					return;
				}
				if(pro.getIsCityDis() != null && pro.getIsCityDis()){
					result.put("city", "true");
				}
			}
			
			if(receiverId == null){
				buyerReceiver = receiverService.getDefaultReceiver(getBuyerId());
			}else{
				buyerReceiver = receiverService.findById(receiverId);
			}
			
			if(buyerReceiver != null) {
				Area area = areaService.findById(buyerReceiver.getAreaId());
				buyerReceiver.setAddress(area.getFullName()+buyerReceiver.getAddress());
				result.put("receiver", buyerReceiver);
			}else { //最近一个作为	
				List<BuyerReceiver> rs=receiverService.getBuyerReceiver(getBuyerId());
				if(rs!=null && rs.size()>0){
					result.put("receiver", rs.get(0));
				}
			}
			
		} catch (OrderException e) {
			result.put("error", e.getMessage());
		}
		rendSuccessJson(result);
	}
	
	/**
	 * 结算拼团订单
	 */
	@SuppressWarnings("unchecked")
	public void gbalance(){
		final String items = getJSONPara("items");
		final Long groupUserId = getJSONParaToLong("gHeaderId");

		Ret ret = Ret.create(); 
		ret.put("items", items);
		ret.put("groupUserId", groupUserId);
		ret.put("img_domain", getImageDomain());
		
		try {
			OrderResultDto orderDto = orderGroupService.gbalance(getBuyerId(), null, items);
			ret.put("order", orderDto);
			
			List<OrderItemResultDto> orderItems = orderDto.getOrderItems();
			Product pro = productService.findById(orderItems.get(0).getProductId());
			//自提商品直接返回数据
			if(pro.getIsPickUp() != null && pro.getIsPickUp()){
				ret.put("shop", "true");
				rendSuccessJson(ret);
				return;
			}
			//查询默认收货地址
			BuyerReceiver buyerReceiver=receiverService.getDefaultReceiver(getBuyerId());
			ret.put("receiver", buyerReceiver);
			rendSuccessJson(ret);
		} catch (OrderException e) {
			ret.put("error", e.getMessage());
			rendFailedJsonObj(ret);
		}
	}
	
	/**
	 * 小程序统一下单
	 */
	@Before(ApiSessionInterceptor.class)
	public void wxAppPrepareToPay(){
		try {
			TreeMap<String, Object> params = payService.wxAppPrepareToPay(getJSONParaToLong("orderId"), IpKit.getRealIp(getRequest()));
			rendSuccessJson(params);
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 小程序支付成功后，回调
	 * 【接收到的notify通知】:
		<xml><appid><![CDATA[wx0dd16298bc16ed63]]></appid>
			<bank_type><![CDATA[CFT]]></bank_type>
			<cash_fee><![CDATA[5]]></cash_fee>
			<device_info><![CDATA[WEB]]></device_info>
			<fee_type><![CDATA[CNY]]></fee_type>
			<is_subscribe><![CDATA[Y]]></is_subscribe>
			<mch_id><![CDATA[1281049301]]></mch_id>
			<nonce_str><![CDATA[92hce70z47sbpgv92kgrf6b2sznkn8y4]]></nonce_str>
			<openid><![CDATA[oQ774wnoZjqJt4UdAXusjT9WBvgI]]></openid>
			<out_trade_no><![CDATA[v2bbu7gb2j68r4c6978hrmpp0mls7wvx]]></out_trade_no>
			<result_code><![CDATA[SUCCESS]]></result_code>
			<return_code><![CDATA[SUCCESS]]></return_code>
			<sign><![CDATA[0134E6A1C41E714D03193003EC51552D]]></sign>
			<time_end><![CDATA[20151105183006]]></time_end>
			<total_fee>5</total_fee>
			<trade_type><![CDATA[JSAPI]]></trade_type>
			<transaction_id><![CDATA[1002690406201511051467804891]]></transaction_id>
		</xml>
	 * 支付成功后，微信回调地址
	 */
	/**
	 * 1.同步此通知回调方法
	 * 2.检查业务数据状态
	 * 通过以上两步：防止微信重复通知，造成数据混乱
	 */
	@Clear
	public void wxAppResult(){
		log.info("===================小程序支付成功，成功回调");
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
		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			use = authCertService.findDefault();
		}
		if(use.getPayMchId() == null || use.getPaySecretKey() ==null || use.getCertFile() == null){
			log.error("[" + authAppId + "]公众号支付配置设置不全");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		String secretKey=use.getPaySecretKey();
		String sign = SignKit.sign(params, secretKey);
		String tenpaySign = root.elementText("sign").toUpperCase();
		if(StrKit.isBlank(sign) 
				|| StrKit.isBlank(tenpaySign) 
				|| !sign.equals(tenpaySign)){
			log.error("微信支付异步通知签名错误");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		log.debug("成功回调，签名正确");
		
		//根据out_trade_no 判断订单支付来源
		String tradeNo = (String) params.get("out_trade_no");
		log.debug("tradeNo:" + tradeNo);
		String openid = (String) params.get("openid");
		BuyerUser user = buyerUserService.findByOpenId(openid);
		if(user==null){
			log.error("["+ openid + "] user is null");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		synchronized (userTradeSet) {
			if(userTradeSet.contains(tradeNo+user.getId())){
				//正在处理的订单数据，本次不处理
				renderText(TO_RES_WEIXIN, "text/xml");
				return;
			}
			userTradeSet.add(tradeNo+user.getId());
		}
		try {
			//==========================业务处理
			if(StrKit.notBlank(tradeNo) && tradeNo.startsWith("l-")){
				//说明是从抽奖充值支付过来的
				payService.resultLotteryCallback(user, params);
			}else if(StrKit.notBlank(tradeNo) && tradeNo.startsWith("c-")){
				payService.resultMemberCardCallback(user, params);				
			}else{
				//回调更新订单状态
				payService.resultOrderCallback(user, params);
			}
		} catch (PayException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			//处理成功后，移除
			userTradeSet.remove(tradeNo+user.getId());
		}
		
		renderText(TO_RES_WEIXIN, "text/xml");
	}
	
}
