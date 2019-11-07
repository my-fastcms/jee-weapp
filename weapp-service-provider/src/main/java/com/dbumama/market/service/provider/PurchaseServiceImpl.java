package com.dbumama.market.service.provider;

import com.dbumama.market.alipay.AlipayConfig;
import com.dbumama.market.alipay.AlipaySubmit;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.api.SerinumService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.utils.DateTimeUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.*;

@Bean
@RPCBean
public class PurchaseServiceImpl extends WxmServiceBase<Purchase> implements PurchaseService {
	private static final PurchaseOrder pOrderdao = new PurchaseOrder().dao();
	private static final SellerUser sellerUserdao = new SellerUser().dao();
	private static final SellerMission missionDao = new SellerMission().dao();

	@Inject
	private InvitecodeRuleService invitecodeRuleService;
	@Inject
	private SellerUserService sellerUserService;
	@Inject
	private AuthUserService authUserService;
	@Inject
	private AuthUserAppService authUserAppService;
	@Inject
	private PayService payService;
	@Inject
	private SerinumService serinumService;		//序号
	
	//判断是否购买了该插件
	public Boolean whetherPay(Long appId,Long authUserId){
		//判断该插件是否属于免费插件
		 List<Purchase> findByAppId = findByAppId(appId);
		 if(findByAppId != null && findByAppId.size()>0 && "免费".equals(findByAppId.get(0).getName())) {
			 return true;
		}
		//非免费插件，判断用户是否购买了该插件
		 AuthUserApp authUserApp = authUserAppService.findByApp(authUserId,appId);
		 Date now = new Date();
		 //没有购买或者时间过期跳转支付页面
		 if(authUserApp == null || now.after(authUserApp.getEndDate())){
			 return false;
		 }
		 return true;
	}
	
	@Override
	public List<Purchase> find() {
		return DAO.find("select * from " + Purchase.table+ " where status = 1");
	}

	@Override
	public PurchaseOrder getPurchaseOrder(String tradeNo) {
		return pOrderdao.findFirst(" select * from " + PurchaseOrder.table + " where trade_no =　?", tradeNo);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.purchase.PurchaseService#createOrder(java.lang.String, java.lang.Long, java.lang.Long)
	 */
	@Override
	public String createOrder(Long sellerId, Long porderId, String payType, Long purchaseId, Long appId) throws WxmallBaseException {
		if(sellerId == null) throw new WxmallBaseException("用户信息异常");
		if(StrKit.isBlank(payType)) throw new WxmallBaseException("请选择支付类型");
		if(purchaseId ==null) throw new WxmallBaseException("请选择购买时长");
		
		SellerUser buyer = sellerUserdao.findById(sellerId);
		if(buyer == null) throw new WxmallBaseException("用户信息异常");
		
		Purchase purchase = findById(purchaseId);
		if(purchase == null || purchase.getStatus() != 1) throw new WxmallBaseException("订购数据异常");
		
		if(purchase.getPayFee() == 0){
			//一个模板只允许试用一次  支付金额为0说明是试用订单
			PurchaseOrder porder = pOrderdao.findFirst("select * from " + PurchaseOrder.table + " where user_id=? and active=1 and pay_fee=0 ", sellerId);
			if(porder != null) throw new WxmallBaseException("一个账号只能试用一次");
		}
		
		PurchaseOrder order = null;
		if(porderId !=null){
			order = pOrderdao.findById(porderId);
			if(order == null || order.getActive() !=0) throw new WxmallBaseException("订单数据异常,id:" + porderId.intValue());
		}else{
			order = new PurchaseOrder();
			order.setCreated(new Date());
			order.setOrderSn(serinumService.getPurchOrderSn());
			order.setUserId(sellerId);
			order.setAppId(appId);
		}
		
		order.setPurchaseId(purchaseId);
		order.setPayType(payType);
		order.setTradeNo(getTradeNo());
		order.setActive(purchase.getPayFee() == 0 ? 1 : 0);
		order.setPayFee(purchase.getPayFee() * purchase.getExpiresIn() / 30);
		order.setUpdated(new Date());
		
		if(StrKit.notBlank(buyer.getShareInviteCode())){//根据邀请码折扣计算最终订单支付金额
			//校验邀请码的有效性
			SellerUser shareUser = sellerUserService.getSellerByIncode(buyer.getShareInviteCode());
			if(shareUser != null){
				InvitecodeRule incodeRule = invitecodeRuleService.findRule();
				if(incodeRule != null && incodeRule.getActive() == true && incodeRule.getDiscountRate()!=null){
					//计算订单折扣
					BigDecimal _payFee2 = incodeRule.getDiscountRate().divide(new BigDecimal(100)).multiply(new BigDecimal(order.getPayFee())).setScale(BigDecimal.ROUND_HALF_UP, 2);
					order.setPayFee(_payFee2.doubleValue());
				}				
			}
		}
		
		if(order.getId() == null)
			order.save();
		else 
			order.update();
		
		if(order.getPayFee() == 0){
			setTime(order, purchase);
			return "test";
		}
		
		if("AliPay".equals(payType)){
			//支付宝
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", AlipayConfig.service);
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.seller_id);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", AlipayConfig.payment_type);
			sParaTemp.put("notify_url", AlipayConfig.notify_url);
			sParaTemp.put("return_url", AlipayConfig.return_url);
			sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
			sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
			sParaTemp.put("out_trade_no", order.getTradeNo());
			sParaTemp.put("subject", "购买WXMALL微拼团服务" + purchase.getName());
			
			BigDecimal payFee = new BigDecimal(order.getPayFee());
			sParaTemp.put("total_fee", payFee.toString());
			sParaTemp.put("body", purchase.getDesc());
			
			return AlipaySubmit.buildRequest(sParaTemp,"get","确认");
		}else if("WxPay".equals(payType)){
			
			String url_code= payService.prepareToPay4pc(order.getTradeNo(), new BigDecimal(order.getPayFee()), order.getOrderSn(), getLocalHostLANAddress());
			String urlcode64=Base64.getUrlEncoder().encodeToString(url_code.getBytes());  //java8  safeurl base64
			SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String url = "?qrcode="+urlcode64+"&payfee="+order.getPayFee().toString()+"&created="+myFmt2.format(new Date());
			
			//微信
			return "/pay/wxPayScan"+url;
		}else{
			throw new WxmallBaseException("不支持的支付类型");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.purchase.PurchaseService#getPurchaseOrdersBySeller(java.lang.Long)
	 */
	@Override
	public List<PurchaseOrderResultDto> getUnpayPurchaseOrdersBySellerAndModule(Long sellerId) {
		List<Record> records = Db.find("select p.name as pur_name, wm.module_name, po.pay_fee, po.pay_type, po.active as o_status, po.created as o_created from "
				+ PurchaseOrder.table + " po " 
				+ " left join " + Purchase.table + " p on po.purchase_id = p.id "
				+ " where po.user_id=? and po.module_id=? ", sellerId);
		
		List<PurchaseOrderResultDto> orderDtos = new ArrayList<PurchaseOrderResultDto>();
		for(Record r : records){
			PurchaseOrderResultDto orderDto = new PurchaseOrderResultDto();
			orderDto.setCreated(r.getDate("o_created"));
			orderDto.setOrderFee(r.getDouble("pay_fee").toString());
			orderDto.setPayType(r.getStr("pay_type"));
			orderDto.setPurchaseName(r.getStr("pur_name"));
			orderDto.setStatus(r.getInt("o_status"));
			orderDtos.add(orderDto);
		}
		return orderDtos;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.purchase.PurchaseService#getUnpayPurchaseOrderByModule(java.lang.Long, java.lang.Long)
	 */
	@Override
	public PurchaseOrder getUnpayPurchaseOrderByModule(Long sellerId, Long appId) {
		//当前模块对应的未支付订单
		return pOrderdao.findFirst("select * from " + PurchaseOrder.table + " where user_id=? and app_id=? and active=0 ", sellerId, appId);
	}
	
	private void setTime(PurchaseOrder order, Purchase purchase){
		AuthUser authUser = authUserService.findById(order.getAppId());
		
		if(authUser == null) throw new WxmallBaseException("auth user is null");
		
		//检查是否过期，过期才能继续试用
		if(purchase !=null && purchase.getPayFee() == 0 
				&& DateTimeUtil.nowDate().after(authUser.getStartDate()) 
				&& DateTimeUtil.nowDate().before(authUser.getEndDate())){
			//未过期的情况下
			throw new WxmallBaseException("正在使用期间，无需试用!");
		}
		
		Date startDate = null;
		//获取用户订购信息
		if(authUser.getEndDate()!=null && authUser.getStartDate()!=null 
				&& authUser.getEndDate().after(DateTimeUtil.nowDate())){
			startDate = authUser.getEndDate();
		}else{
			startDate = new Date();
		}
		
		authUser.setStartDate(startDate);
		//假如用户没有过期再续购
		String endDateStr = DateTimeUtil.getNextDateStringAddDay(DateTimeUtil.toDateString(startDate), purchase.getExpiresIn());
		Date endDate = DateTimeUtil.toDate(endDateStr, "00:00:00");
		authUser.setEndDate(endDate);
		authUser.setUpdated(new Date());
		
		authUser.update();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.purchase.PurchaseService#callBack(java.lang.String)
	 */
	@Override
	public void callBack(String outTradeNo, String tradeNo) {
		PurchaseOrder order = getPurchaseOrder(outTradeNo);
		
		if(order == null || order.getActive() !=0) {
			System.err.println("trade_no:[" + outTradeNo + "]查无此订单");
			return;
		}  
		//表示未支付订单
		order.setActive(1);		 //表示已支付订单
		order.setAlipayTradeNo(tradeNo);
		order.setUpdated(new Date());
		order.update();
		
		//当有邀请码的时候 计算佣金 
		//获取购买模板下单者
		SellerUser buyer = sellerUserdao.findById(order.getUserId());
		if(StrKit.notBlank(buyer.getShareInviteCode())){
			//获取邀请码，根据邀请码得到分享者
			SellerUser shareUser = sellerUserdao.findFirst("select * from " + SellerUser.table + " where my_invite_code=? ", buyer.getShareInviteCode());
			if(shareUser != null){
				InvitecodeRule incodeRule = invitecodeRuleService.findRule();
				if(incodeRule !=null && incodeRule.getActive() == true){
					BigDecimal rate = incodeRule.getMissionRate();
					if(rate !=null){
						BigDecimal commssion = rate.divide(new BigDecimal(100)).multiply(new BigDecimal(order.getPayType())).setScale(2, BigDecimal.ROUND_HALF_UP);
						
						//记录佣金流水
						SellerMissionRcd missionRcd = new SellerMissionRcd();
						missionRcd.setSellerId(shareUser.getId());//推广邀请码者获取佣金
						missionRcd.setOrderId(order.getId());
						missionRcd.setMemo("用户["+buyer.getPhone()+"]购买模板产生佣金");
						missionRcd.setSellerMission(commssion);
						missionRcd.setActive(true);
						missionRcd.setCreated(new Date());
						missionRcd.setUpdated(new Date());
						missionRcd.save();
						
						//累计佣金
						SellerMission mission = missionDao.findFirst("select * from " + SellerMission.table + " where seller_id=? ", shareUser.getId());
						if(mission == null){
							mission = new SellerMission();
							mission.setSellerId(shareUser.getId());
							mission.setMission(commssion);
							mission.setCreated(new Date());
							mission.setUpdated(new Date());
							mission.setActive(true);
							mission.save();
						}else{
							mission.setMission(mission.getMission().add(commssion).setScale(2, BigDecimal.ROUND_HALF_UP));
							mission.setUpdated(new Date());
							mission.update();
						}
					}						
				}
			}
		}
		
		Purchase purchase = findById(order.getPurchaseId());
		setTime(order, purchase);
	}
	
	@SuppressWarnings("rawtypes")
	private String getLocalHostLANAddress()  {
	    try {
	        // 遍历所有的网络接口
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // 在所有的接口下再遍历IP
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
	                    if (inetAddr.isSiteLocalAddress()) {//instanceof Inet4Address
	                        // 如果是site-local地址，就是它了
	                        return inetAddr.getHostAddress();
	                    }
	                }
	            }
	        }
	        
	        // 如果没有发现 non-loopback地址.只能用最次选的方案
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        jdkSuppliedAddress.getHostAddress();
	    } catch (Exception e) {
	        
	    }
	    return null;
	}

	@Override
	public List<Purchase> findByAppId(Object id) {
		return DAO.find("select * from " + Purchase.table+ " where status = 1 and app_id = ? ", id);
	}

}