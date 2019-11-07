package com.dbumama.market.service.provider;

import com.dbumama.market.model.*;
import com.dbumama.market.service.api.AuthCertService;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.PayException;
import com.dbumama.market.service.api.PayService;
import com.dbumama.market.service.enmu.OrderType;
import com.dbumama.market.service.enmu.PaymentStatus;
import com.dbumama.market.utils.SignKit;
import com.dbumama.market.wxpay.WxpayConfig;
import com.dbumama.weixin.pay.UnifiedOrderApi;
import com.dbumama.weixin.pay.UnifiedOrderReqData;
import com.dbumama.weixin.pay.UnifiedOrderResData;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.math.BigDecimal;
import java.util.*;

@Bean
@RPCBean
public class PayServiceImpl implements PayService{
	
	@Inject
	private AuthUserService authUserService;
	@Inject
	private AuthCertService authCertService;

//	private static final BuyerCard buyerCardDao = new BuyerCard().dao();
	private static final BuyerRecharge rechargeDao = new BuyerRecharge().dao();
	private static final BuyerUser buyerUserdao = new BuyerUser().dao();
//	private static final Card cardDao = new Card().dao();
	private static final Order orderdao = new Order().dao();
	private static final OrderItem orderItemdao = new OrderItem().dao();
	private static final Product productDao = new Product().dao();

	private WxpayConfig wxpayConfig = Jboot.config(WxpayConfig.class);

	@Override
	public void resultLotteryCallback(BuyerUser user, TreeMap<String, Object> params) throws PayException {
		//更新用户抽奖次数
		String totalFee = (String) params.get("total_fee");
		String tradeNo = (String) params.get("out_trade_no");
		//tradeNo = tradeNo.replaceAll("'", "");
		String transactionId = (String) params.get("transaction_id");
		if("200".equals(totalFee)){
			user.setScore(user.getScore() + 20);
		}else if("500".equals(totalFee)){
			user.setScore(user.getScore() + 60);
		}else if("1000".equals(totalFee)){
			user.setScore(user.getScore() + 120);
		}else{
			throw new PayException("支付金额错误");
		}
		
		user.setUpdated(new Date());
		if(!user.update()) throw new PayException("更新公众号["+user.getAppId()+"]用户["+user.getId()+"]信息失败");
		//记录用户充值记录
		BuyerRecharge recharge = new BuyerRecharge();
		recharge.setBuyerId(user.getId());
		recharge.setRecharge(new BigDecimal(totalFee));
		recharge.setOutTradeId(tradeNo);
		recharge.setTransactionId(transactionId);
		recharge.setActive(true);
		recharge.setCreated(new Date());
		recharge.setUpdated(new Date());
		try {
			recharge.save();			
		} catch (Exception e) {
			throw new PayException(e.getMessage());
		}
	}

	@Override
	public TreeMap<String, Object> prepareToPay(Long orderId, String ip)
			throws PayException {
        return prepareToPay(orderId, null, ip);
	}

	@Override
	public TreeMap<String, Object> prepareToPay(Long orderId, AuthUser authUser, String ip) throws PayException {
		
		if(authUser == null) throw new PayException("调用微信统一下单接口失败, authUser is null");
		
		Order order = orderdao.findById(orderId);
		if(order == null) throw new PayException("调用微信统一下单接口失败, order is null");
		
		BuyerUser buyer = buyerUserdao.findById(order.getBuyerId());
		if(buyer == null) throw new PayException("调用微信统一下单接口失败, buyer is null");
		
		String payFee_ = String.valueOf(order.getPayFee().multiply(new BigDecimal(100)).intValue());
		
		UnifiedOrderResData unifiedOrderResData = null;
		UnifiedOrderReqData unifiedOrderReqData = null;

		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			use = authCertService.findDefault();
		}
		if(use == null || use.getPayMchId() == null || use.getPaySecretKey() ==null || use.getCertFile() == null){
			throw new PayException("公众号支付配置设置不全");
		}
		
		unifiedOrderReqData = new UnifiedOrderReqData(
				use.getAppId(), use.getPayMchId(), use.getPaySecretKey(),
				buyer.getOpenId(), order.getOrderSn(), order.getTradeNo(), payFee_,  ip, "JSAPI", "http://"+authUser.getAppId()+".dbumama.com/pay/result");
		
		try {
			UnifiedOrderApi unifiedOrderApi = new UnifiedOrderApi();
			unifiedOrderResData = (UnifiedOrderResData) unifiedOrderApi.post(unifiedOrderReqData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PayException(e.getMessage());
		}
		
		if(unifiedOrderResData==null || !"OK".equals(unifiedOrderResData.getReturn_msg())){
			if(unifiedOrderResData != null){
				System.out.println(unifiedOrderResData.getReturn_msg() + "appId:" + authUser.getAppId() + ",secretKey:" + use.getPaySecretKey() + ",mchid:" + use.getPayMchId());
			}
			throw new PayException("调用微信统一下单接口失败");
		}
		
		//更新订单
		if(StrKit.isBlank(unifiedOrderResData.getPrepay_id())) throw new PayException("调用微信统一下单接口失败，prepay_id is null");
		
		order.setPrepayId(unifiedOrderResData.getPrepay_id());
		order.update();
		
		//准备调用支付js接口的参数
		TreeMap<String, Object> params = new TreeMap<String, Object>();
    	params.put("appId", unifiedOrderReqData.getAppid());
        params.put("timeStamp", Long.toString(new Date().getTime()));
        params.put("nonceStr", SignKit.genRandomString32());
        params.put("package", "prepay_id="+unifiedOrderResData.getPrepay_id());
        params.put("signType", "MD5");
        
        String paySign = SignKit.sign(params, use==null ? null : use.getPaySecretKey());
        
        params.put("paySign", paySign);
        params.put("packageValue", "prepay_id="+unifiedOrderResData.getPrepay_id());
        params.put("returnMsg", unifiedOrderResData.getReturn_msg());
        params.put("sendUrl", "http://"+authUser.getAppId()+".dbumama.com/pay/result/");
        params.put("tradeno", order.getTradeNo());
		return params;
	}
	
	/**
	 * 微信小程序
	 */
	@Override
	public TreeMap<String, Object> wxAppPrepareToPay(Long orderId, String ip) throws PayException {
		
		Order order = orderdao.findById(orderId);
		if(order == null) throw new PayException("调用微信统一下单接口失败, order is null");
		
		BuyerUser buyer = buyerUserdao.findById(order.getBuyerId());
		if(buyer == null) throw new PayException("调用微信统一下单接口失败: buyer is null ");
		
		String payFee_ = String.valueOf(order.getPayFee().multiply(new BigDecimal(100)).intValue());
		
		UnifiedOrderResData unifiedOrderResData = null;
		UnifiedOrderReqData unifiedOrderReqData = null;
		
		AuthUser authUser = authUserService.findById(buyer.getAppId());
		if(authUser == null) throw new PayException("授权小程序不存在,appId:" + buyer.getAppId());
		
		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			use = authCertService.findDefault();
		}
		if(use == null || use.getPayMchId() == null || use.getPaySecretKey() ==null || use.getCertFile() == null){
			throw new PayException("公众号支付配置设置不全");
		}
		
		String mchId=use.getPayMchId();
		String payScretKey = use.getPaySecretKey();
		unifiedOrderReqData = new UnifiedOrderReqData(use.getAppId(),mchId,payScretKey,
				buyer.getOpenId(), order.getOrderSn(), order.getTradeNo(), payFee_, ip, "JSAPI", "https://api.dbumama.com/pay/wxAppResult");
		try {
			UnifiedOrderApi unifiedOrderApi = new UnifiedOrderApi();
			unifiedOrderResData = (UnifiedOrderResData) unifiedOrderApi.post(unifiedOrderReqData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PayException(e.getMessage());
		}
		
		if(unifiedOrderResData==null || !"OK".equals(unifiedOrderResData.getReturn_msg())){
			if(unifiedOrderResData != null){
				throw new PayException("调用微信统一下单接口失败;" + unifiedOrderResData.getReturn_msg());
			}else{
				throw new PayException("调用微信统一下单接口失败 unifiedOrderResData is null");				
			}
		}
		
		//更新订单
		if(StrKit.isBlank(unifiedOrderResData.getPrepay_id())) throw new PayException("调用微信统一下单接口失败，prepay_id is null");
		
		order.setPrepayId(unifiedOrderResData.getPrepay_id());
		order.update();
		
		//准备调用支付js接口的参数
		TreeMap<String, Object> params = new TreeMap<String, Object>();
    	params.put("appId",unifiedOrderReqData.getAppid());
    	
    	String now=Long.toString(new Date().getTime());
    	if(now.length()>10){
			now=now.substring(0, 10);
		}
        params.put("timeStamp",now);
        
        params.put("nonceStr", SignKit.genRandomString32());
        params.put("package", "prepay_id="+unifiedOrderResData.getPrepay_id());
        params.put("signType", "MD5");
        
        String paySign = SignKit.sign(params, payScretKey);
        
        params.put("paySign", paySign);
        params.put("packageValue", "prepay_id="+unifiedOrderResData.getPrepay_id());
        params.put("returnMsg", unifiedOrderResData.getReturn_msg());
      
        params.put("tradeno", order.getTradeNo());
		return params;
	}

	@Override
	public void resultOrderCallback(BuyerUser user, TreeMap<String, Object> params) throws PayException {
		String tradeNo = (String) params.get("out_trade_no");
		String transactionId = (String) params.get("transaction_id");
		Order order = orderdao.findFirst("select * from "+Order.table+" where trade_no = ?", tradeNo);
		if(order == null){
			throw new PayException("===支付回调:pay result order is null");
		}
		order.setTransactionId(transactionId);
		dealOrder(order, user);
	}
	
	/**
	 * 订单支付后回调处理
	 * @param order
	 * @param user
	 */
	private void dealOrder(Order order, BuyerUser user){
		//更新订单状态为已支付
		order.setPaymentStatus(PaymentStatus.paid.ordinal());
		order.setUpdated(new Date());
		order.update();
		
		List<OrderItem> orderItems = orderItemdao.find("select * from " + OrderItem.table + " where order_id=? ", order.getId());
		if(orderItems ==null || orderItems.size()<=0){
			return;
		}
		
		//存储该笔订单中的商品，避免重复发sql查询多次
		//更新商品库存
		List<Product> products = new ArrayList<Product>();
		for(OrderItem orderItem : orderItems){
			Product product = productDao.findById(orderItem.getProductId());
			products.add(product);
			product.setStock(product.getStock() - orderItem.getQuantity() > 0 ? product.getStock() - orderItem.getQuantity() : 0);
			product.setSales(product.getSales() + orderItem.getQuantity());
			product.update();
		}
		
	}
	
	@Override
	public void resultMemberCardCallback(BuyerUser user, TreeMap<String, Object> params) throws PayException {
		String tradeNo = (String) params.get("out_trade_no");
		String transactionId = (String) params.get("transaction_id");
		
		BuyerRecharge br = rechargeDao.findFirst("select * from " + BuyerRecharge.table + " where buyer_id=? and out_trade_id=? and active=0", user.getId(), tradeNo);
		if(br == null) throw new PayException("会员卡充值回调出现异常：充值明细记录不存在");
		
		//获取当前充值会员卡信息，需要激活
//		BuyerCard buyerCard = buyerCardDao.findFirst("select * from " + BuyerCard.table + " where buyer_id=? and card_id=? and status=1 ", user.getId(), br.getCardId());
//		if(buyerCard == null) throw new PayException("会员卡充值回调出现异常:会员卡激活信息记录不存在");
		
//		Card mcard = cardDao.findFirst("select * from " + Card.table + " where card_id=? ", br.getCardId());
//		if(mcard == null) throw new PayException("会员卡充值回调出现异常：会员卡不存在");
//		if(!"true".equals(mcard.getSupplyBalance())) throw new PayException("会员卡充值回调出现异常：会员卡不支持充值");
			
		//获取该会员卡旧的积分以及余额信息
//		Map<String, String> paramsCardInfoMap = ParaMap.create().put("card_id", buyerCard.getCardId()).put("code", buyerCard.getUserCardCode()).getData();
		
//		AuthUser authUser = authUserService.findById(user.getAppId());
//		ApiResult cardInfoResult = CompCardApi.memberCardInfo(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsCardInfoMap));
//		if(!cardInfoResult.isSucceed()) throw new PayException(cardInfoResult.getErrorMsg());
//		String user_card_status = cardInfoResult.getStr("user_card_status");
//		if(!"NORMAL".equals(user_card_status)) throw new PayException("会员卡充值回调出现异常:会员卡状态不可用,当前状态值：" + user_card_status);
		/*Integer bonus = cardInfoResult.getInt("bonus");	//原来的积分
		BigDecimal newBonus = new BigDecimal(bonus);*/
		
//		Integer balance = cardInfoResult.getInt("balance");	//原来的余额
//		BigDecimal newBalance = new BigDecimal(balance).add(new BigDecimal(params.get("total_fee").toString()));
		//调用接口//更新会员卡信息
//		Map<String, String> paramsMap = ParaMap.create().put("code", buyerCard.getUserCardCode())
//				.put("card_id", buyerCard.getCardId()).getData();
//		if(StrKit.notBlank(mcard.getSupplyBonus()) && "true".equals(mcard.getSupplyBonus())){
//			paramsMap.put("bonus", cardInfoResult.getInt("bonus") == null ? "0" : cardInfoResult.getInt("bonus").toString());
//			paramsMap.put("add_bonus", "0");
//			paramsMap.put("record_bonus", "消费送积分");
//		}
//		if(StrKit.notBlank(mcard.getSupplyBalance()) && "true".equals(mcard.getSupplyBalance())){
//			paramsMap.put("balance", newBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//			paramsMap.put("add_balance",  params.get("total_fee").toString());
//			paramsMap.put("record_balance", "会员卡充值");
//		}
		
//		ApiResult cardResult = CompCardApi.updateUser(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsMap));
//		if(!cardResult.isSucceed()){
//			throw new PayException(cardResult.getErrorMsg());
//		}
		
		br.setUpdated(new Date());
		br.setTransactionId(transactionId);//用户微信接口退款标志
		br.setActive(true);//改条记录已充值成功 
		br.update();
		
		//判断是否用户是购买会员卡等级
		if(br.getMemberRankId() != null){
			user.setMemberRankId(br.getMemberRankId());
			user.update();
		}
	}

	@Override
	public void payByCard(Long orderId, Long userId) throws PayException {
		if(orderId == null) throw new PayException("使用会员卡支付缺少订单号");
		Order order = orderdao.findById(orderId);
		
		if(order == null || order.getPaymentStatus() != PaymentStatus.unpaid.ordinal())
			throw new PayException("当前订单不可支付");
		
		BuyerUser user = buyerUserdao.findById(order.getBuyerId());
		if(order.getOrderType() == OrderType.pintuan.ordinal()){
			//拼团订单
			user = buyerUserdao.findById("userId");
		}
		if(user == null) throw new PayException("使用会员卡支付失败，缺少会员数据");
		
		BigDecimal payFee = order.getPayFee();
		
		if(payFee == null || payFee.compareTo(new BigDecimal(0)) != 1)
			throw new PayException("使用会员卡支付失败，订单金额少于0");
		
//		BuyerCard buyerCard = buyerCardDao.findFirst("select * from " + BuyerCard.table + " where buyer_id=? and status=1 ", user.getId());
//		if(buyerCard == null) throw new PayException("使用会员卡支付失败，您还不是会员");
//
//		Card card = cardDao.findFirst("select * from " + Card.table + " where card_id=? ", buyerCard.getCardId());
//		if(card == null) throw new PayException("使用会员卡支付失败，会员卡不存在");
		
		AuthUser authUser = authUserService.findById(user.getAppId());
		
		//检查会员卡时效
		//获取会员卡信息，检查是否够钱支付
		//获取该会员卡旧的积分以及余额信息
//		Map<String, String> paramsCardInfoMap = ParaMap.create().put("card_id", buyerCard.getCardId()).put("code", buyerCard.getUserCardCode()).getData();
//		ApiResult cardInfoResult = CompCardApi.memberCardInfo(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsCardInfoMap));
//		if(!cardInfoResult.isSucceed()) throw new PayException(cardInfoResult.getErrorMsg());
//		String user_card_status = cardInfoResult.getStr("user_card_status");
//		if(!"NORMAL".equals(user_card_status)) throw new PayException("使用会员卡支付失败,会员卡状态不可用," + user_card_status);
//		Integer balance = cardInfoResult.getInt("balance");	//原来的余额
//		if(new BigDecimal(balance).compareTo(payFee) !=1) throw new PayException("使用会员卡支付失败，余额不足");
//		//根据订单金额调整会员卡余额
//		BigDecimal newBalance = new BigDecimal(balance).subtract(payFee);
//		//调用接口//更新会员卡信息
//		Map<String, String> paramsMap = ParaMap.create().put("code", buyerCard.getUserCardCode())
//				.put("card_id", buyerCard.getCardId()).getData();
//		if(StrKit.notBlank(card.getSupplyBonus()) && "true".equals(card.getSupplyBonus())){
//			paramsMap.put("bonus", cardInfoResult.getInt("bonus") == null ? "0" : cardInfoResult.getInt("bonus").toString());
//			paramsMap.put("add_bonus", "0");
//			paramsMap.put("record_bonus", "消费送积分");
//		}
//		if(StrKit.notBlank(card.getSupplyBalance()) && "true".equals(card.getSupplyBalance())){
//			paramsMap.put("balance", newBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//			paramsMap.put("add_balance", "-"+payFee.toString());
//			paramsMap.put("record_balance", "支付订单：" + order.getOrderSn());
//		}
//		ApiResult cardResult = CompCardApi.updateUser(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsMap));
//		if(!cardResult.isSucceed()){
//			throw new PayException(cardResult.getErrorMsg());
//		}
//		order.setTransactionId(buyerCard.getCardId());
		dealOrder(order, user);
	}

	@Override
	public boolean checkSign(TreeMap<String, Object> params, String tenpaySign) {
		final String secretKey = wxpayConfig.getWxpayPaternerKey();
		final String sign = SignKit.sign(params, secretKey);
		return StrKit.isBlank(sign)
				|| StrKit.isBlank(tenpaySign)
				|| !sign.equals(tenpaySign);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.PayService#prepareToPay4pc(java.lang.String, java.math.BigDecimal, java.lang.String, java.lang.String)
	 */
	@Override
	public String prepareToPay4pc(String tradeNo, BigDecimal payFee, String desc, String ip) throws PayException {
		String payFee_ = String.valueOf(payFee.multiply(new BigDecimal(100)).intValue());
		UnifiedOrderResData unifiedOrderResData = null;
		UnifiedOrderReqData unifiedOrderReqData = null;
		unifiedOrderReqData = new UnifiedOrderReqData(
				wxpayConfig.getWxpayAppId(), wxpayConfig.getWxpayPartner(), wxpayConfig.getWxpayPaternerKey(),
				"", desc, tradeNo, payFee_,  ip, "NATIVE", wxpayConfig.getWxpayNotifyUrl());
		
		try {
			UnifiedOrderApi unifiedOrderApi = new UnifiedOrderApi();
			unifiedOrderResData = (UnifiedOrderResData) unifiedOrderApi.post(unifiedOrderReqData);//post
		} catch (Exception e) {
			e.printStackTrace();
			throw new PayException(e.getMessage());
		}
		
		if(unifiedOrderResData==null || !"OK".equals(unifiedOrderResData.getReturn_msg())){
			if(unifiedOrderResData != null){
				System.out.println(unifiedOrderResData.getReturn_msg() + ",appId:" + wxpayConfig.getWxpayAppId() + ",mchid:" + wxpayConfig.getWxpayPartner() + ",secretKey:" + wxpayConfig.getWxpayPaternerKey());
			}
			throw new PayException("调用微信统一下单接口失败");
		}
		if(StrKit.isBlank(unifiedOrderResData.getCode_url())){
			throw new PayException("调用微信统一下单接口失败");
		}
		
		unifiedOrderResData.setCode_url(Base64.getUrlEncoder().encodeToString(unifiedOrderResData.getCode_url().getBytes()));
		return unifiedOrderResData.getCode_url();
	}

}
