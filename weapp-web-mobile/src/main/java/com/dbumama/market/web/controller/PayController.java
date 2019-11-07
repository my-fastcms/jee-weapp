package com.dbumama.market.web.controller;

import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.utils.SignKit;
import com.dbumama.market.web.core.controller.BaseMobileController;
import com.dbumama.market.web.core.utils.IpKit;
import com.jfinal.aop.Clear;
import com.jfinal.core.JFinal;
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

@RequestMapping(value="pay")
public class PayController extends BaseMobileController{

	//返回成功的xml给微信
	static final String TO_RES_WEIXIN = "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
			+ "<return_msg><![CDATA[OK]]></return_msg></xml>";
	
	//正在处理的用户订单
	public static Set<String> userTradeSet = new HashSet<String>();
	
	@RPCInject
	private PayService payService;
	@RPCInject
	private OrderService orderService;
	@RPCInject
	private CardService cardService;
	@RPCInject
	private AreaService areaService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private AuthCertService authCertService;
	@RPCInject
	private BuyerReceiverService receiverService;
	@RPCInject
	private BuyerUserService buyerUserService;
	@RPCInject
	private MemberRankService memberRankService;
	@RPCInject
	private ProductService productService;
	@RPCInject
	private OrderItemService orderItemService;
	@RPCInject
	private OrderGroupService orderGroupService;
	
	/**
	 * h5 发起支付
	 */
	public void index(){
		String type = getPara("type");
		Long cardId = getParaToLong("cardId");
		Long rankId = getParaToLong("rankId");	//按会员等级方式充值
		if(StrKit.notBlank(type)){
			//充值抽奖
			lottery();
		}else if(cardId != null){
			if(rankId != null){
				//会员卡等级购买
				try {
					TreeMap<String, Object> params = cardService.rechargeCardRank(getBuyerId(), cardId, rankId, IpKit.getRealIpV2(getRequest()));
					rendSuccessJson(params);
				} catch (CardException e) {
					rendFailedJson(e.getMessage());
				}
			}else{
				try {
					TreeMap<String, Object> params = cardService.rechargeCard(getBuyerId(), cardId, getPara("recharge"), IpKit.getRealIpV2(getRequest()));
					rendSuccessJson(params);
				} catch (CardException e) {
					rendFailedJson(e.getMessage());
				}				
			}
		}else{
			try {
				TreeMap<String, Object> params = payService.prepareToPay(getParaToLong("orderId"), authUserService.getAuthUserByAppId(getAppId()), "");
				rendSuccessJson(params);
			} catch (OrderException e){
				rendFailedJson(e.getMessage());
			} catch (PayException e) {
				rendFailedJson(e.getMessage());
			}
		}
	}
	
	/**
	 * 订单结算
	 */
	public void balance (){
		final String items = getPara("items");
		setAttr("items", items);
		try {
			OrderResultDto orderDto = orderService.balance(getBuyerId(), null, items);
			Card card = cardService.getCardByUser(getBuyerId());
			setAttr("card", card);
			if(getBuyerUser() == null) {
				render("/pay/jiesuan_error.html");
				return;
			}
			if(getBuyerUser().getMemberRankId()!=null){
				MemberRank rank = memberRankService.findById(getBuyerUser().getMemberRankId());
				setAttr("rank", rank);
			}
			setAttr("order", orderDto);
			List<OrderItemResultDto> orderItems = orderDto.getOrderItems();
			if(orderItems != null && orderItems.size() == 1){
				Product pro = productService.findById(orderItems.get(0).getProductId());
				//跳转自提商品结算页面
				if(pro.getIsPickUp() != null && pro.getIsPickUp()){
					render("/pay/ziti_jiesuan.html");
					return;
				}
			}
			//查询默认收货地址
			BuyerReceiver buyerReceiver = receiverService.getDefaultReceiver(getBuyerId());
			if(buyerReceiver != null) {
				Area area=areaService.findById(buyerReceiver.getAreaId());
				setAttr("fullName",area.getFullName());
				setAttr("receiver", buyerReceiver);
			}
			render("/pay/jiesuan.html");
		} catch (OrderException e) {
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		} catch (CardException e){
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		}
	}
	
	public void jifenBalance (){
		final String items = getPara("items");
		Product pro = productService.findById(getParaToLong("pId"));
		if(pro == null) render("/pay/jiesuan_error.html");
		
		setAttr("items", items);
		setAttr("awardSendId", getPara("awardSendId"));

		try {
			OrderResultDto orderDto = orderService.balance(getBuyerId(), null, items);
			setAttr("order", orderDto);
			//跳转自提商品结算页面
			if(pro.getIsPickUp() != null && pro.getIsPickUp()){
				render("/pay/pick_up_jiesuan.html");
				return;
			}
			//查询默认收货地址
			BuyerReceiver buyerReceiver = receiverService.getDefaultReceiver(getBuyerId());
			if(buyerReceiver != null) {
				Area area=areaService.findById(buyerReceiver.getAreaId());
				setAttr("fullName",area.getFullName());
				setAttr("receiver", buyerReceiver);
			}
			
			render("/pay/jifen_jiesuan.html");
		} catch (OrderException e) {
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		} catch (CardException e){
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		}
	}
	
	/**
	 * 插件活动商品结算
	 */
	public void taskBalance (){
		final String items = getPara("items");
		Product pro = productService.findById(getParaToLong("pId"));
		if(pro == null) render("/pay/jiesuan_error.html");
		
		setAttr("items", items);
		setAttr("awardSendId", getPara("awardSendId"));

		try {
			OrderResultDto orderDto = orderService.balance(getBuyerId(), null, items);
			setAttr("order", orderDto);
			//跳转自提商品结算页面
			if(pro.getIsPickUp() != null && pro.getIsPickUp()){
				render("/pay/pick_up_jiesuan.html");
				return;
			}
			//查询默认收货地址
			BuyerReceiver buyerReceiver = receiverService.getDefaultReceiver(getBuyerId());
			if(buyerReceiver != null) {
				Area area=areaService.findById(buyerReceiver.getAreaId());
				setAttr("fullName",area.getFullName());
				setAttr("receiver", buyerReceiver);
			}
			
			render("/pay/task_jiesuan.html");
		} catch (OrderException e) {
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		} catch (CardException e){
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		}
	}
	
	/**
	 * 结算拼团订单
	 */
	public void gbalance(){
		final String items = getPara("items");
		setAttr("items", items);
		final Long groupId = getParaToLong("groupId");
		setAttr("groupId", groupId);

		try {
			OrderResultDto orderDto = orderGroupService.gbalance(getBuyerId(), null, items);
			Card card = cardService.getCardByUser(getBuyerId());
			setAttr("card", card);
			if(getBuyerUser() == null) {
				render("/pay/jiesuan_error.html");
				return;
			}
			if(getBuyerUser().getMemberRankId()!=null){
				MemberRank rank = memberRankService.findById(getBuyerUser().getMemberRankId());
				setAttr("rank", rank);
			}
			setAttr("order", orderDto);
			List<OrderItemResultDto> orderItems = orderDto.getOrderItems();
			if(orderItems != null && orderItems.size() == 1){
				Product pro = productService.findById(orderItems.get(0).getProductId());
				//跳转拼团自提商品结算页面
				if(pro.getIsPickUp() != null && pro.getIsPickUp()){
					render("/pay/g_ziti_jiesuan.html");
					return;
				}
			}
			//查询默认收货地址
			BuyerReceiver buyerReceiver = receiverService.getDefaultReceiver(getBuyerId());
			if(buyerReceiver != null) {
				Area area=areaService.findById(buyerReceiver.getAreaId());
				setAttr("fullName",area.getFullName());
				setAttr("receiver", buyerReceiver);
			}
			render("/pay/g_jiesuan.html");
		} catch (OrderException e) {
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		} catch (CardException e){
			setAttr("error", e.getMessage());
			render("/pay/jiesuan_error.html");
		}
	}
	
	/**
	 * 通过会员卡进行支付
	 */
	public void card(){
		Long orderId = getParaToLong("orderId");
		try {
			payService.payByCard(orderId, getBuyerId());
			rendSuccessJson();
		} catch (PayException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	void lottery () {
		String type = getPara("type");
		int totalMoney = 0;
		if("x11".equals(type)){
			totalMoney = 2;
		}else if("x12".equals(type)){
			totalMoney = 5;
		}else if("x13".equals(type)){
			totalMoney = 10;
		}else{
			totalMoney = 0;
		}
		if(totalMoney<=0){
			rendFailedJson("提交的金额为0，请重新确认");
			return;
		}
		//生成本次交易号tradeNo，同步tradeNo 保证唯一
//		final String tradeNo = "l-" + getUUIDStr().substring(2);
//		try {
//			TreeMap<String, Object> params = payService.prepareToPay(getOpenId(), tradeNo, new BigDecimal(totalMoney), "疯狂抽奖", IpKit.getRealIpV2(getRequest()), authUserService.getAuthUserByAppId(getAppId()));
//	        rendSuccessJson(params);
//		} catch (PayException e) {
//			rendFailedJson(e.getMessage());
//		}
		rendSuccessJson();
	}
	
	/**
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
	public void result(){
		log.debug("===================支付成功，成功回调");
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
		String authAppId = ""; 
		for(Element e : elements){
			params.put(e.getName(), e.getTextTrim());
			if("appid".equals(e.getName())){
				authAppId = e.getTextTrim();
			}
		}
		
		AuthUser authUser = authUserService.getAuthUserByAppId(authAppId);
		if(authUser == null){
			log.error("[" + authAppId + "]授权公众号不存在");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			use = authCertService.findDefault();
		}
		if(use.getPayMchId() == null || use.getPaySecretKey() ==null || use.getCertFile() == null){
			log.error("公众号支付配置设置不全");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		String sign = SignKit.sign(params, use.getPaySecretKey());
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
