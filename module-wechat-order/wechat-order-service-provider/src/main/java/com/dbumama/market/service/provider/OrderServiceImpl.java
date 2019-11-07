package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.*;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.pay.RefundApi;
import com.dbumama.weixin.pay.RefundReqData;
import com.dbumama.weixin.pay.RefundResData;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.rpc.annotation.RPCBean;

import java.math.BigDecimal;
import java.util.*;

@Bean
@RPCBean
public class OrderServiceImpl extends WxmServiceBase<Order> implements OrderService {
	@Inject
	private SerinumService serinumService;		//序号
	@Inject
	private AuthUserService authUserService;
	@Inject
	private BuyerUserService buyerUserService;
	@Inject
	private AuthCertService authCertService;
	@Inject
	private RefundErrorService refundErrorService;
	@Inject
	private OrderPressRcdService orderPressRcdService;
	@Inject
	private ProductService productService;
	@Inject
	private OrderItemService orderItemService;

//	private static final BuyerCard buyerCardDao = new BuyerCard().dao();
	private static final BuyerReceiver receiverDao = new BuyerReceiver().dao();
	private static final BuyerUser buyerUserdao = new BuyerUser().dao();
//	private static final Card cardDao = new Card().dao();
	private static final Cart cartDao = new Cart().dao();
	private static final DeliverySet deliveryDao = new DeliverySet().dao();
	private static final DeliveryTemplate deliveryTpldao = new DeliveryTemplate().dao();
	private static final MemberRank mbRankdao = new MemberRank().dao();
	private static final Order orderdao = new Order().dao();
	private static final OrderItem orderItemdao = new OrderItem().dao();
	private static final Product productDao = new Product().dao();
	private static final ProductSpecItem prodSpecItemdao = new ProductSpecItem().dao();
	private static final SpecificationValue specValueDao = new SpecificationValue().dao();
	private static final OrderPressRcd orderPressRcddao = new OrderPressRcd().dao();
	private static final ExpressComp expCompDao = new ExpressComp().dao();

	@Override
	public Page<OrderResultDto> list(OrderListParamDto orderParamDto) throws OrderException {
		if(orderParamDto == null || orderParamDto.getAuthUserId() == null)
			throw new OrderException("查询订单参数错误");
		
		String select = " SELECT  o.*, o.id as o_id, o.created as o_created, b.id as b_id, b.nickname, b.open_id, r.*, r.phone as r_phone, r.city as r_city, r.province as r_province, r.district as r_district, si.bill_number, si.exp_key, si.exp_name ";
		String sqlExceptSelect = " FROM "+Order.table+" o "
				+ " left join " + BuyerUser.table + " b on o.buyer_id=b.id "
				+ " left join " + BuyerReceiver.table + " r on o.receiver_id=r.id "
				+ " left join " + Shipping.table + " si on si.order_id=o.id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("o.order_type", OrderType.common.ordinal());
		helper.addWhere("o.order_sn", orderParamDto.getOrderSn());
		
		setQuery(helper, orderParamDto);
		
		helper.build();
		
		Page<Record> orders = Db.paginate(orderParamDto.getPageNo(), orderParamDto.getPageSize(), helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		List<OrderResultDto> orderList = new ArrayList<OrderResultDto>();
		for(Record order : orders.getList()){
			OrderResultDto orderListResultDto = getOrderResult(order);
			orderList.add(orderListResultDto);
		}
		
		return new Page<OrderResultDto>(orderList, orderParamDto.getPageNo(), orderParamDto.getPageSize(), orders.getTotalPage(), orders.getTotalRow());
	}
	
	//统一设置订单公共查询条件
	@Override
	public void setQuery(QueryHelper helper, OrderListParamDto orderParamDto){
		
		helper.addWhere("o.app_id", orderParamDto.getAuthUserId())
		.addWhereLike("b.nickname", orderParamDto.getBuyerNickName())
		.addWhereLike("r.receiver_name", orderParamDto.getReceiverName())
		.addWhere("r.phone", orderParamDto.getReceiverPhone());
		
		if(StrKit.notBlank(orderParamDto.getStartDate()) && StrKit.notBlank(orderParamDto.getEndDate())){
			List<String> dateList = new ArrayList<String>();
			dateList.add(orderParamDto.getStartDate());
			dateList.add(orderParamDto.getEndDate());
			helper.addWhereBETWEEN("o.created", dateList);
		}
		
		if(StrKit.notBlank(orderParamDto.getOrderStatus())){
			String [] orderStatus = orderParamDto.getOrderStatus().split("_");
			helper.addWhere("o.payment_status", PaymentStatus.valueOf(orderStatus[0]).ordinal());
			if(orderStatus.length == 2){
				helper.addWhere("o.shipping_status", ShippingStatus.valueOf(orderStatus[1]).ordinal());
			}
			if(orderStatus.length == 3){
				helper.addWhere("o.shipping_status", ShippingStatus.valueOf(orderStatus[1]).ordinal());
				helper.addWhere("o.order_status", OrderStatus.valueOf(orderStatus[2]).ordinal());
			}
			if(orderStatus.length >= 4){
				helper.addWhere("o.shipping_status", ShippingStatus.valueOf(orderStatus[1]).ordinal());
				helper.addWhere("o.order_status", OrderStatus.valueOf(orderStatus[2]).ordinal());
				helper.addWhere("o.group_status", GroupStatus.valueOf(orderStatus[3]).ordinal());
			}
		}
		helper.addOrderBy("desc", "o.created");
	}
	
	private void check(OrderCreateParamDto orderParamDto){
		JSONArray itemsArr = null;
		//查找该用户是否合法
		BuyerUser buyer = buyerUserdao.findById(orderParamDto.getBuyerId());
		if(buyer == null || buyer.getAppId() == null || orderParamDto.getAuthUserId() == null || buyer.getAppId().longValue() != orderParamDto.getAuthUserId().longValue()){
			throw new OrderException("该用户不合法");
		}
		try {
			itemsArr = JSONArray.parseArray(orderParamDto.getItems());
		} catch (Exception e) {
			throw new OrderException("OrderCreateParamDto json parse error");
		}
		//循环每一件商品
		for(int i = 0; i<itemsArr.size(); i++){
			JSONObject itemsJsonObj = (JSONObject) itemsArr.get(i);
			Long pId = itemsJsonObj.getLong("productId");
			Product product = productService.findById(pId);
			if(product == null){
				throw new OrderException("创建订单失败，请检查参数");
			}
			//是否限购商品
			if(product.getIsPurchaseLimitation() != null && product.getIsPurchaseLimitation()){
				//查询该用户购买该商品的次数是否大于限购次数
				Integer purchaseCount = orderItemService.getPurchaseCount(buyer.getId(),orderParamDto.getAuthUserId(),pId);
				if(purchaseCount != null && product.getPurchaseCount() != null 
						&& product.getPurchaseCount().intValue() < (purchaseCount.intValue() + itemsJsonObj.getIntValue("pcount"))){
					throw new OrderException(product.getName()+"(您对该商品的购买数量已达到上限)");
				}
			}
		}
	}

	@Override
	@Before(Tx.class)
	public Long create(OrderCreateParamDto orderParamDto) throws OrderException {
		if(orderParamDto == null || orderParamDto.getAuthUserId() == null 
				|| orderParamDto.getBuyerId() == null 
				|| (orderParamDto.getReceiverId() == null && orderParamDto.getShopId() ==null)
				|| StrKit.isBlank(orderParamDto.getItems())){
			throw new OrderException("创建订单失败，请检查参数");
		}
		//检测是否存在限购商品
		try {
			check(orderParamDto);
		} catch (OrderException e) {
			throw new OrderException(e.getMessage());
		}
		//解析出提交的订单数据
		OrderResultDto orderDto = null;
		try {
			orderDto = balance(orderParamDto.getBuyerId(), orderParamDto.getReceiverId(), orderParamDto.getItems());
		} catch (OrderException e) {
			throw new OrderException(e.getMessage());
		}
		if(orderDto == null) throw new OrderException("创建订单失败，解析订单数据出错");
		
		Order order = create(orderParamDto, orderDto);
		
		 //此处查询该用户是否销售员
//		Agent agent = agentService.getBuyerAgent(order.getBuyerId());
//		if(orderDto.getOrderType().intValue() == OrderType.agent.ordinal() && agent != null){
//			boolean flag = true;
//			AgentConfig agentConfig = agentConfigService.findByApp(agent.getAppId());
//			if(agentConfig == null) flag = false;
//				//判断是否销售员是否需要审核
//			if(flag && agentConfig.getNeedAudit() != null && agentConfig.getNeedAudit()){
//				//如果需要审核，而该销售员未审核通过，则该订单不属于分销订单
//				if(agent.getStatus() == null || agent.getStatus().intValue() != 1) flag = false;
//			}
//			
//			//是否开启的内销,没有开启内销时，如果上级为空时，该订单视为普通订单，没人能获取到佣金
//			if(!flag || (agent.getParentId() == null && (agentConfig.getSelfBuyRate() == null || !agentConfig.getSelfBuyRate()))){
//				order.setOrderType(OrderType.common.ordinal());
//			}
//		}
		
		try {
			order.save();
		} catch (ActiveRecordException e) {
			throw new OrderException(e.getMessage());
		}
		
		//保存订单项数据
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (OrderItemResultDto orderItemDto : orderDto.getOrderItems()) {
			Product product = productDao.findById(orderItemDto.getProductId());
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(product.getSn());
			orderItem.setName(product.getName());
			orderItem.setProductImg(product.getImage());
			orderItem.setQuantity(orderItemDto.getQuantity());
			orderItem.setProductId(product.getId());
			orderItem.setOrderId(order.getId());
			if(orderItemDto.getSpecificationValues() != null){
				final StringBuffer sbff = new StringBuffer();
				for(SpecificationValue sfv : orderItemDto.getSpecificationValues()){
					sbff.append(sfv.getId()).append(",");
				}
				orderItem.setSpecificationValue(sbff.length()>0 ? sbff.deleteCharAt(sbff.length()-1).toString() : sbff.toString());
			}
			orderItem.setPrice(new BigDecimal(orderItemDto.getPrice()));
			orderItem.setActive(true);
			orderItem.setCreated(new Date());
			orderItem.setUpdated(new Date());
			orderItems.add(orderItem);
		}
		
		try {
			Db.batchSave(orderItems, orderItems.size());
		} catch (ActiveRecordException e) {
			throw new OrderException(e.getMessage());
		}
		
		//如果是分销订单，分配订单佣金
		if(order.getOrderType().intValue() == OrderType.agent.ordinal()){
			//1.检查该用户是否有上级分销商
			//2.根据分销规则给上级分销商分佣
			//List<Agent> agents = agentService.getSelfAndParents(agent.getId());
//			List<Agent> agents = agentService.getSuperior(agent.getId());
//			if(agents != null && agents.size() > 0){
//				for(Agent ag : agents){
//					try {
//						agentService.setAgentCommission(ag, order);
//					} catch (Exception e) {
//						logger.error(e.getMessage());//设置佣金失败的原因是否记录到数据库
//					}
//				}
//			}
		}
		
		//创建订单成功后检查是否有购物车数据，删除掉
		for(OrderItemResultDto tempOrderItem : orderDto.getOrderItems()){
			Cart c = cartDao.findFirst(" select * from " + Cart.table + " where buyer_id=? and product_id=? ", orderParamDto.getBuyerId(), tempOrderItem.getProductId());
			if(c!=null) c.delete();
		}
		
		//发送模板消息
		//由于微信对模板消息规则进行限制，此处不再消费一次模板消息
		//Jboot.sendEvent(new JbootEvent("order_created", order));
		
		return order.getId();
	}

	@Override
	public Order create(OrderCreateParamDto orderParamDto, OrderResultDto orderDto){
		Order order = new Order();
		if(orderDto.getOrderType().intValue() == OrderType.pickup.ordinal()){
			order.setShopId(orderParamDto.getShopId());
			order.setOrderType(orderDto.getOrderType());   	//自提订单
		}else if(orderDto.getOrderType().intValue() == OrderType.citydistribution.ordinal()){
			order.setShopId(orderParamDto.getShopId());
			order.setReceiverId(orderParamDto.getReceiverId());
			order.setOrderType(orderDto.getOrderType());		//配送订单
		}else{
			if(orderDto.getIsPickup() != null && orderDto.getIsPickup()){
				order.setShopId(orderParamDto.getShopId());
			}else{
				order.setReceiverId(orderParamDto.getReceiverId());
			}
			order.setOrderType(orderDto.getOrderType());		//普通订单  拼团订单
		}
		order.setBuyerId(orderParamDto.getBuyerId());
		order.setAppId(orderParamDto.getAuthUserId());  //从哪个公众号下单
		order.setFormId(orderParamDto.getFormId());		//用于小程序模板消息推送
		order.setOrderSn(serinumService.getOrderSn());
		order.setOrderStatus(OrderStatus.unconfirmed.ordinal());
		order.setPaymentStatus(PaymentStatus.unpaid.ordinal());
		order.setShippingStatus(ShippingStatus.unshipped.ordinal());
		order.setMemo(orderParamDto.getMemo());
		order.setTotalPrice(orderDto.getTotalPrice());		//商品价格总和，不含邮费
		order.setPostFee(orderDto.getPostFee());			//订单邮费
		BigDecimal payFee = order.getTotalPrice().add(order.getPostFee());	//最终支付金额
		order.setPayFee(payFee);
		order.setTradeNo(getTradeNo());
		order.setActive(true);
		order.setCreated(new Date());
		order.setUpdated(new Date());
		return order;
	}
	
	@Override
	public List<OrderMobileResultDto> list4Mobile(OrderListParamDto orderParamDto) throws OrderException {
		if(orderParamDto == null || orderParamDto.getBuyerId() == null)
			throw new OrderException("调用手机端订单列表数据接口缺少参数");
		
		String select = " SELECT  * ";
		String sqlExceptSelect = " FROM "+Order.table;

		QueryHelper queryHelper = new QueryHelper(select, sqlExceptSelect);
		queryHelper.addWhere("buyer_id", orderParamDto.getBuyerId())
		.addWhere("app_id", orderParamDto.getAuthUserId())
		.addWhere("payment_status", StrKit.notBlank(orderParamDto.getPaymentStatus()) ? PaymentStatus.valueOf(orderParamDto.getPaymentStatus()).ordinal() : null)
		.addWhere("order_status", StrKit.notBlank(orderParamDto.getOrderStatus()) ? OrderStatus.valueOf(orderParamDto.getOrderStatus()).ordinal() : null)
		.addWhere("shipping_status", StrKit.notBlank(orderParamDto.getShippingStatus()) ? ShippingStatus.valueOf(orderParamDto.getShippingStatus()).ordinal() : null)
		.addWhere("group_status", StrKit.notBlank(orderParamDto.getGroupStatus()) ? GroupStatus.valueOf(orderParamDto.getGroupStatus()).ordinal() : null)
		.addWhere("order_type", orderParamDto.getOrderType())
		.addOrderBy("desc", "created");
		
		queryHelper.build();
		
		Page<Order> orders = orderdao.paginate(orderParamDto.getPageNo(), orderParamDto.getPageSize(), queryHelper.getSelect(), queryHelper.getSqlExceptSelect(), queryHelper.getParams());

		List<OrderMobileResultDto> results = new ArrayList<OrderMobileResultDto>();
		for(Order order : orders.getList()){
			OrderMobileResultDto orderListResultDto = new OrderMobileResultDto();
			orderListResultDto.setCreated(order.getCreated());
			orderListResultDto.setOrderId(order.getId());
			orderListResultDto.setSn(order.getOrderSn());
			
			orderListResultDto.setOrderStatus(order.getOrderStatus());
			orderListResultDto.setPaymentStatus(order.getPaymentStatus());
			orderListResultDto.setShipStatus(order.getShippingStatus());
			
			if(order.getPaymentStatus() == PaymentStatus.unpaid.ordinal()){
				orderListResultDto.setStatus("待支付");
			}else if(order.getPaymentStatus() == PaymentStatus.paid.ordinal()){
				orderListResultDto.setStatus("已支付");
			}
			if(order.getShippingStatus() == ShippingStatus.shipped.ordinal()){
				orderListResultDto.setStatus("已发货");
			}
			//已退款状态
			if(order.getPaymentStatus() == PaymentStatus.waitRefund.ordinal()){
				orderListResultDto.setStatus("待退款");
			}
			//已退款
			if(order.getPaymentStatus() == PaymentStatus.refunded.ordinal()){
				orderListResultDto.setStatus("已退款");
			}
			if(order.getOrderStatus() == OrderStatus.completed.ordinal()){
				orderListResultDto.setStatus("已完成");
			}else if(order.getOrderStatus() == OrderStatus.cancelled.ordinal()){
				orderListResultDto.setStatus("已取消");
			}
			if(order.getOrderType() == OrderType.pintuan.ordinal()){
				orderListResultDto.setOrderType("拼团");
				//检查orderGroup状态 显示正在拼团中的订单，待成团
				if(order.getGroupStatus() != null){
					if(order.getGroupStatus() == GroupStatus.grouping.ordinal())
						orderListResultDto.setGroupStatus("待成团");
					if(order.getGroupStatus() == GroupStatus.success.ordinal())
						orderListResultDto.setGroupStatus("组团成功");
					if(order.getGroupStatus() == GroupStatus.fail.ordinal())
						orderListResultDto.setGroupStatus("组团失败");
					orderListResultDto.setGroupedStatus(order.getGroupStatus());
				}
			}
			orderListResultDto.setTotalPrice(order.getPayFee().toString());
			orderListResultDto.setOrderItems(getOrderItems(orderListResultDto.getOrderId()));
			results.add(orderListResultDto);
		}
		
		return results;
	}
	
	@Override
	public OrderResultDto getOrder(Long orderId) throws OrderException {
		if(orderId == null) throw new OrderException("调用订单详情接口出错，请传入订单id");
		Order order = findById(orderId);
		if(order == null) throw new OrderException("调用订单详情接口出错，请传入正确的订单id");
		Record record = null;
		if(order.getOrderType().intValue() == OrderType.pickup.ordinal()){
			record = Db.findFirst(" SELECT  o.*, o.id as o_id, o.created as o_created, s.id as s_id, s.shop_name ,s.shop_address,s.shop_contact,s.shop_contact_phone, bu.nickname FROM "+Order.table+" o "
					+ " left join " + Shop.table + " s on o.shop_id=s.id "
					+ " left join " + BuyerUser.table + " bu on o.buyer_id=bu.id "
					+ " where o.id=?", orderId);
		}else if(order.getOrderType().intValue() == OrderType.citydistribution.ordinal()){
			record = Db.findFirst(" SELECT  o.*, o.id as o_id, o.created as o_created,r.*, r.id as r_id, r.phone as r_phone, r.city as r_city, r.province as r_province, r.district as r_district,"
					+ "s.id as s_id, s.shop_name ,s.shop_address,s.shop_contact,s.shop_contact_phone, bu.nickname FROM "+Order.table+" o "
					+ " left join " + BuyerReceiver.table + " r on o.receiver_id=r.id "
					+ " left join " + Shop.table + " s on o.shop_id=s.id "
					+ " left join " + BuyerUser.table + " bu on o.buyer_id=bu.id "
					+ " where o.id=?", orderId);
		}else{
			record = Db.findFirst(" SELECT  o.*, o.id as o_id, o.created as o_created, r.*, r.id as r_id, r.phone as r_phone, r.city as r_city, r.province as r_province, r.district as r_district, bu.nickname FROM "+Order.table+" o "
					+ " left join " + BuyerReceiver.table + " r on o.receiver_id=r.id "
					+ " left join " + BuyerUser.table + " bu on o.buyer_id=bu.id "
					+ " where o.id=?", orderId);
		}
		
		return getOrderResult(record);
	}
	
	@Override
	public List<OrderResultDto> getOrders(String orderIds) throws OrderException {
		if(StrKit.isBlank(orderIds)) throw new OrderException("调用批量获取订单详情接口参数错误");
		final String [] orderIdArrs = orderIds.split("-");
		List<Object> params = new ArrayList<Object>();
		final StringBuffer condition = new StringBuffer(); 
		for(String id : orderIdArrs){
			params.add(Long.valueOf(id));
			condition.append("?").append(",");
		}
		condition.deleteCharAt(condition.length() -1);
		
		final StringBuffer where = new StringBuffer(" where 1=1 ");
		where.append(" and o.id in ("+condition+")");
		
		List<Record> orders = Db.find(" SELECT  o.*, o.id as o_id, o.created as o_created, r.*, r.id as r_id, r.phone as r_phone, r.city as r_city, r.province as r_province, r.district as r_district, b.*, b.id as b_id FROM "+Order.table+" o "
				+ " left join " + BuyerUser.table + " b on o.buyer_id=b.id "
				+ " left join " + BuyerReceiver.table + " r on o.receiver_id=r.id "
				+ where.toString(), params.toArray());
		
		List<OrderResultDto> orderResultList = new ArrayList<OrderResultDto>();
		for(Record record : orders){
			OrderResultDto orderDetailDto = getOrderResult(record);
			orderResultList.add(orderDetailDto);
		}
		
		return orderResultList;
	}
	
	@Override
	public void batchShipping(Long appId, String items) throws OrderException {
		if(StrKit.isBlank(items)) throw new OrderException("调用批量发货缺少必要参数");
		
		if(appId == null) throw new OrderException("没有发货权限");
		
		JSONArray jsonArray = null;
		try {
			jsonArray = JSONArray.parseArray(items);
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}
		
		if(jsonArray == null || jsonArray.size() <=0){
			throw new OrderException("batchShipping jsonArray is null or size <=0");
		}
				
		List<String> batchError = new ArrayList<String>();
		for(int i=0;i<jsonArray.size();i++){
			JSONObject json = jsonArray.getJSONObject(i);
			SendGoodParamDto sendGoodParam = new SendGoodParamDto();
			sendGoodParam.setOrderId(json.getLong("orderId"));
			sendGoodParam.setBuyerId(json.getLong("buyer_id"));
			sendGoodParam.setExpKey(json.getString("exp_key"));
			sendGoodParam.setBillNumber(json.getString("bill_number"));
			try {
				shipping(sendGoodParam);
			} catch (OrderException e) {
				batchError.add("订单[" + sendGoodParam.getOrderId() + "]发货失败，原因："+e.getMessage());
			}
		}
		if(batchError.size()>0){
			StringBuffer msg = new StringBuffer();
			for(String e : batchError){
				msg.append(e);
			}
			throw new OrderException(msg.toString());
		}
	}
	
	@Override
	@Before(Tx.class)
	public String shipping(SendGoodParamDto sendGoodParam) throws OrderException {
		if(sendGoodParam == null || StrKit.isBlank(sendGoodParam.getExpKey()) 
				|| StrKit.isBlank(sendGoodParam.getBillNumber()) || sendGoodParam.getOrderId() == null){
			throw new OrderException("调用订单发货接口缺少必要参数");
		}
		
		Order order = orderdao.findById(sendGoodParam.getOrderId());
		
		if(order == null || order.getPaymentStatus() != PaymentStatus.paid.ordinal()) throw new OrderException("只有支付状态的订单才能发货");

		if(order.getAppId().intValue() != sendGoodParam.getAuthUserId().intValue()) throw new OrderException("别人的订单不用你发货哦!!!");
		
		if(order.getShippingStatus() == ShippingStatus.shipped.ordinal()) throw new OrderException("订单已发货，无需重复发货");
		
		order.setShippingStatus(ShippingStatus.shipped.ordinal());
		order.update();
		
		Shipping ship=new Shipping();
		if("else".equals(sendGoodParam.getExpKey()) && !StrKit.isBlank(sendGoodParam.getExpName())){
			ship.setExpName(sendGoodParam.getExpName());
			ship.setExpKey(null);
		}else{
			ExpressComp expComp = expCompDao.findFirst("select * from " + ExpressComp.table + " where exp_key=? ", sendGoodParam.getExpKey());
			ship.setExpKey(sendGoodParam.getExpKey());
			ship.setExpName(expComp == null ? "未知快递" : expComp.getExpName());
		}
		ship.setOrderId(sendGoodParam.getOrderId());
		ship.setBillNumber(sendGoodParam.getBillNumber());
		ship.setActive(1);
		ship.setCreated(new Date());
		ship.setUpdated(new Date());
		
		try {
			ship.save();
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}
		
		Jboot.sendEvent(new JbootEvent("order_send_goods", order));		
		return order.getOrderSn();
	}

	@Override
	@Before(Tx.class)
	public void refund(RefundParamDto refundParam) throws OrderException {
		if(refundParam == null || refundParam.getOrderId() == null || refundParam.getRefundFee() == null || refundParam.getAuthUserId() == null)
			throw new OrderException("订单退款缺少必要参数");
		
		Order order = orderdao.findById(refundParam.getOrderId());
		if(order == null) throw new OrderException("订单不存在");
		if(StrKit.isBlank(order.getTransactionId())) throw new OrderException("退款标识order transactionId is null");
		if(order.getAppId().intValue() != refundParam.getAuthUserId().intValue()) throw new OrderException("别人的订单你没有权限退款哦");
		if(order.getPaymentStatus() == PaymentStatus.refunded.ordinal()) throw new OrderException("订单已退款，不可重复退款");
		if(order.getPaymentStatus() != PaymentStatus.paid.ordinal() && order.getPaymentStatus() != PaymentStatus.waitRefund.ordinal()) throw new OrderException("未支付订单不允许退款");
		if(order.getOrderStatus() == OrderStatus.completed.ordinal()) throw new OrderException("交易成功不可退款");
		if(order.getOrderStatus() == OrderStatus.cancelled.ordinal()) throw new OrderException("已取消订单不可退款");
		if(refundParam.getRefundFee().compareTo(new BigDecimal(0)) !=1) throw new OrderException("退款金额少于0");

		//需要调用接口线上退款
		try {
			refund(order);
		} catch (WxmallBaseException e) {
			RefundError rerror = refundErrorService.findByOrderId(order.getId());
			if(rerror == null){
				rerror = new RefundError();
				rerror.setOrderId(order.getId());
			}
			rerror.setRefundError(e.getMessage());
			refundErrorService.saveOrUpdate(rerror);
			throw new OrderException(e.getMessage());
		}
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#refund(java.lang.Long, java.lang.String)
	 */
	@Override
	public void refund(Long appId, String items) throws OrderException {
		if(StrKit.isBlank(items)) throw new OrderException("调用退款接口缺少必要参数");
		
		if(appId == null) throw new OrderException("appId is null");
		
		JSONArray jsonArray = null;
		try {
			jsonArray = JSONArray.parseArray(items);
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}
		
		if(jsonArray == null || jsonArray.size() <=0){
			throw new OrderException("batchShipping jsonArray is null or size <=0");
		}
		
				
		List<String> batchError = new ArrayList<String>();
		for(int i=0;i<jsonArray.size();i++){
			JSONObject json = jsonArray.getJSONObject(i);
			RefundParamDto refundParamDto = new RefundParamDto(appId, json.getLong("orderId"), json.getBigDecimal("cash"));
			
			try {
				refund(refundParamDto);
				//处理拼团订单退款
				try {
					Order order = orderdao.findById(json.getLong("orderId"));
//					if(order.getOrderType() == OrderType.pintuan.ordinal()){
//						spellGroupRefund(order);
//					}
				} catch (Exception e) {
					batchError.add("处理拼团订单退款失败，原因："+e.getMessage());
				}
				//查找该订单是否存在佣金记录，更新其使用状态
//				List<AgentCommRcd> list = agentCommRcdService.findByOrderId(json.getLong("orderId"));
//				if(list != null && list.size() > 0){
//					for (AgentCommRcd agentCommRcd : list) {
//						agentCommRcd.setActive(false).setUpdated(new Date());
//						try {
//							agentCommRcdService.update(agentCommRcd);
//						} catch (Exception e) {
//							batchError.add("佣金记录更新失败[" + agentCommRcd.getId() + "]更新失败，原因："+e.getMessage());
//						}
//					}
//				}
				
			} catch (OrderException e) {
				batchError.add("订单[" + refundParamDto.getOrderId() + "]退款失败，原因："+e.getMessage());
			}
		}
		if(batchError.size()>0){
			StringBuffer msg = new StringBuffer();
			for(String e : batchError){
				msg.append(e);
			}
			throw new OrderException(msg.toString());
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#refund(com.dbumama.market.model.Order)
	 */
	@Override
	@Before(Tx.class)
	public void refund(Order order) throws WxmallBaseException {
		
		if(order == null) throw new WxmallBaseException("order is null");
		
		if(order.getPaymentStatus() != PaymentStatus.paid.ordinal() && order.getPaymentStatus() != PaymentStatus.waitRefund.ordinal()) throw new WxmallBaseException("order is not payment status");
		
		if(order.getPaymentStatus() == PaymentStatus.refunded.ordinal()) throw new WxmallBaseException("order is refunded status");
		
		BuyerUser buyer = buyerUserService.findById(order.getBuyerId());
		if(buyer == null) throw new WxmallBaseException("buyer is null");
		
		AuthUser authUser = authUserService.findById(buyer.getAppId());
		if(authUser == null) throw new WxmallBaseException("authUser is null");
		
		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			throw new WxmallBaseException("authUser[" + authUser.getNickName() + "]未配置支付商户号以及支付密钥相关信息");
		}

		if(StrKit.isBlank(use.getPayMchId()) || StrKit.isBlank(use.getPaySecretKey()))
			throw new WxmallBaseException("authUser[" + authUser.getNickName() + "]未配置支付商户号以及支付密钥相关信息");
		
		if(use.getCertFile() == null)
			throw new WxmallBaseException("authUser[" + authUser.getNickName() + "]未上传支付证书文件");
		
		String payFee = String.valueOf(order.getPayFee().multiply(new BigDecimal(100)).intValue());
		
		RefundReqData refundReqData = new RefundReqData(authUser.getAppId(), use.getPayMchId(), use.getPaySecretKey(),
				order.getOrderSn(), order.getTradeNo(), payFee, payFee, order.getTransactionId());
		RefundApi refundApi = new RefundApi();
		try {
			RefundResData refundResData = (RefundResData) refundApi.post(refundReqData, use.getCertFile());
			
			if("SUCCESS".equals(refundResData.getResult_code())){
				
				Refund refund=new Refund();
				refund.setOrderId(order.getId());
				refund.setTransactionId(order.getTransactionId());
				refund.setCash(order.getPayFee());	//暂时不支持部分退款，即退款金额为订单支付金额
				refund.setCreated(new Date());
				refund.setUpdated(new Date());
				refund.setActive(true);
				try {
					refund.save();
				} catch (Exception e) {
					throw new OrderException(e.getMessage());
				}
				
				order.setPaymentStatus(PaymentStatus.refunded.ordinal());
				order.update();
			}else{
				throw new WxmallBaseException("err_code_msg:" + refundResData.getErr_code_des() + ",return_msg:" + refundResData.getReturn_msg());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new WxmallBaseException(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#applyRefund(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void applyRefund(Long buyerId, Long orderId) throws WxmallBaseException {
		if(buyerId == null) throw new WxmallBaseException("buyerId is null");
		if(orderId == null) throw new WxmallBaseException("orderId is null");
		
		Order order = findById(orderId);
		if(order == null) throw new WxmallBaseException("order is null");
		
		if(order.getPaymentStatus().intValue() == PaymentStatus.waitRefund.ordinal())
			throw new WxmallBaseException("此订单正在等待卖家退款...");
		
		if(order.getPaymentStatus().intValue() != PaymentStatus.paid.ordinal())
			throw new WxmallBaseException("此订单不允许退款");
		
		if(order.getOrderStatus().intValue() == OrderStatus.completed.ordinal())
			throw new WxmallBaseException("此订单已完成，不能申请退款");

		if(order.getBuyerId().intValue() != buyerId.intValue())
			throw new WxmallBaseException("您没有申请此订单退款权限");
		
		order.setPaymentStatus(PaymentStatus.waitRefund.ordinal());
		update(order);
		
	}

	@Override
	public OrderResultDto getOrderResult(Record order){
		OrderResultDto orderDetailDto = new OrderResultDto();
		orderDetailDto.setOrderId(order.getLong("o_id"));
		orderDetailDto.setPayFee(order.getBigDecimal("pay_fee"));   			//需计算应付金额
		orderDetailDto.setBuyerNick(order.getStr("nickname"));
		if(StrKit.notBlank(order.getStr("memo"))){
			orderDetailDto.setBuyerMemo(order.getStr("memo"));
			orderDetailDto.setBuyerMemoSimple("留言:" + orderDetailDto.getBuyerMemo());
			if(orderDetailDto.getBuyerMemo().length()>5){
				orderDetailDto.setBuyerMemoSimple("留言:"+orderDetailDto.getBuyerMemo().substring(0, 5).concat("..."));
			}
		}
		orderDetailDto.setTotalPrice(order.getBigDecimal("total_price"));	//商品金额
		orderDetailDto.setPostFee(order.getBigDecimal("post_fee"));			//运费
		orderDetailDto.setOrderCreated(order.getDate("o_created"));
		orderDetailDto.setOrderSn(order.getStr("order_sn"));
		orderDetailDto.setGroupedStatusInt(order.getInt("group_status"));
		orderDetailDto.setReceiverProvince(order.getStr("r_province"));
		orderDetailDto.setReceiverCity(order.getStr("r_city"));
		if(StrKit.notBlank(order.getStr("r_district")))
			orderDetailDto.setReceiverCountry(order.getStr("r_district"));
		orderDetailDto.setReceiverAddr(order.getStr("address"));
		orderDetailDto.setReceiverName(order.getStr("receiver_name"));
		orderDetailDto.setReceiverPhone(order.getStr("r_phone"));
		orderDetailDto.setZipCode(order.getStr("zip_code"));
		orderDetailDto.setTransactionId(order.getStr("transaction_id"));
		orderDetailDto.setOrderType(order.getInt("order_type"));
		orderDetailDto.setShopName(order.getStr("shop_name"));
		orderDetailDto.setShopAddr(order.getStr("shop_address"));
		orderDetailDto.setShopContact(order.getStr("shop_contact"));
		orderDetailDto.setShopContacPhone(order.getStr("shop_contact_phone"));
		if(StrKit.notBlank(order.getStr("bill_number"))){
			orderDetailDto.setBillNumber(order.getStr("bill_number"));
		}
		if(StrKit.notBlank(order.getStr("exp_name"))){
			orderDetailDto.setExpName(order.getStr("exp_name"));
		}
		if(order.getInt("payment_status") == PaymentStatus.unpaid.ordinal()){
			orderDetailDto.setOrderStatus("待支付");
		}else if(order.getInt("payment_status") == PaymentStatus.paid.ordinal()){
			orderDetailDto.setOrderStatus("已支付");
		}else if(order.getInt("payment_status") == PaymentStatus.waitRefund.ordinal()){
			orderDetailDto.setOrderStatus("待退款");
		}else if(order.getInt("payment_status") == PaymentStatus.refunded.ordinal()){
			orderDetailDto.setOrderStatus("已退款");
		}
		
		if(order.getInt("shipping_status") == ShippingStatus.shipped.ordinal()){
			orderDetailDto.setOrderStatus("已发货");
		}
		
		if(order.getInt("order_status") == OrderStatus.completed.ordinal()){
			orderDetailDto.setOrderStatus("已完成");
		}else if(order.getInt("order_status") == OrderStatus.cancelled.ordinal()){
			orderDetailDto.setOrderStatus("已取消");
		}
		
		orderDetailDto.setOrderStatusInt(order.getInt("order_status"));
		orderDetailDto.setPaymentStatusInt(order.getInt("payment_status"));
		orderDetailDto.setShipStatusInt(order.getInt("shipping_status"));
		
		//查询订单项
		List<OrderItemResultDto> orderItemDtos = getOrderItems(orderDetailDto.getOrderId());
		orderDetailDto.setOrderItems(orderItemDtos);
		return orderDetailDto;
	}

	/**
	 * 订单项
	 * @param orderId
	 * @return
	 */
	private List<OrderItemResultDto> getOrderItems (Long orderId){
		List<OrderItemResultDto> orderItemDtos = new ArrayList<OrderItemResultDto>();
		List<OrderItem> orderItems = orderItemdao.find("select * from " + OrderItem.table + " where order_id=? ", orderId);
		for(OrderItem orderItem : orderItems){
			OrderItemResultDto itemDto = new OrderItemResultDto();
			itemDto.setPrice(orderItem.getPrice().toString());
			itemDto.setProductId(orderItem.getProductId());
			itemDto.setProductImg(getImageDomain() + orderItem.getProductImg());
			itemDto.setProductName(orderItem.getName().length()>20?orderItem.getName().substring(0, 20).concat("..."):orderItem.getName());
			itemDto.setQuantity(orderItem.getQuantity());
			itemDto.setSn(orderItem.getSn());
			
			String speciValueIds = orderItem.getSpecificationValue();
			if(StrKit.notBlank(speciValueIds)){
				String [] orderIdArrs = speciValueIds.split(",");
				List<Long> ids = new ArrayList<Long>();
				StringBuffer condition = new StringBuffer(); 
				for(String id : orderIdArrs){
					ids.add(Long.valueOf(id));
					condition.append("?").append(",");
				}
				condition.deleteCharAt(condition.length() -1);
				List<SpecificationValue> specificationValues = specValueDao.find("select * from " + SpecificationValue.table + " where id in("+condition+") ", ids.toArray());
				itemDto.setSpecificationValues(specificationValues);
				final StringBuffer valueNames = new StringBuffer();
				for(SpecificationValue specificationValue : itemDto.getSpecificationValues()){
					valueNames.append(specificationValue.getName()).append("/");
				}
				if(valueNames.length()>0) valueNames.deleteCharAt(valueNames.length()-1);
				itemDto.setSpecificationValueNames(valueNames.toString());
				
//				ProductSpecItem stock=prodSpecItemdao.findFirst("select * FROM "+ProductSpecItem.table+" WHERE product_id = ? and specification_value = ?", itemDto.getProductId(), speciValueIds);
//        		if(stock != null){
//	        		itemDto.setPrice(stock.getPrice().toString());
//        		}
			}
			
			itemDto.setTotalPrice(new BigDecimal(itemDto.getPrice()).multiply(new BigDecimal(itemDto.getQuantity())));
			orderItemDtos.add(itemDto);
		}
		return orderItemDtos;
	}
	
	@Override
	public OrderResultDto balance(Long buyerId, Long receiverId, String items) throws OrderException {
		if(StrKit.isBlank(items)) throw new OrderException("调用结算接口缺少必要参数");

		JSONArray jsonArray = null;
		try {
			 jsonArray = JSONArray.parseArray(items);
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}
		
		if(jsonArray==null || jsonArray.size()<=0) throw new OrderException("请选择要结算的项");
		
		BuyerReceiver receiver = receiverDao.findById(receiverId);
		if(receiverId == null){
			receiver = receiverDao.findFirst(" select * from " + BuyerReceiver.table + " where buyer_id=? and is_default=1", buyerId);
		}
		
		OrderResultDto orderDto = new OrderResultDto();
		List<OrderItemResultDto> orderItemDtos = new ArrayList<OrderItemResultDto>();
		orderDto.setOrderItems(orderItemDtos);
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			final String prodId = jsonObj.getString("productId");
			final String specivalues = jsonObj.getString("speci");
			Product product = productDao.findById(prodId);
			
			if(product == null) throw new OrderException("结算商品出错");
			
			//检查下单是否是配送商品或自提商品
			if(product.getIsPickUp() !=null && product.getIsPickUp()){
				if(jsonArray.size()>1) throw new OrderException("自提商品请单独下单");
				orderDto.setOrderType(OrderType.pickup.ordinal());
			}

			if(product.getIsCityDis() !=null && product.getIsCityDis()){
				if(jsonArray.size()>1) throw new OrderException("配送商品请单独下单");
				orderDto.setOrderType(OrderType.citydistribution.ordinal());
			}
			
			//检查是否属于分销商品
//			List<AgentProduct> agentProducts = agentProductService.getAgentProducts(product.getId());
//			if(agentProducts != null && agentProducts.size() >0){
//				if(jsonArray.size()>1) throw new OrderException("分销商品请单独下单");
//				orderDto.setOrderType(OrderType.agent.ordinal());
//			}
			
			if(product.getIsUnifiedSpec() !=null && product.getIsUnifiedSpec() == false && StrKit.isBlank(specivalues)){
				//多规格商品，却没有选择规格值，这个是不行的
				continue;//排除这种错误数据的商品，无法结算
			}
			
			if(product.getIsMarketable() == true 
					&& product.getStock()!=null && product.getStock()>0){
				OrderItemResultDto orderItem = new OrderItemResultDto();
				orderItem.setProductName(product.getName());
				orderItem.setQuantity(
						product.getStock()!=null && jsonObj.getInteger("pcount")>product.getStock()
						? product.getStock()
						: jsonObj.getInteger("pcount"));
				orderItem.setPrice(product.getPrice());
				orderItem.setProductId(product.getId());
				orderItem.setSn(product.getSn());
				orderItem.setProductImg(product.getImage());
				
				final StringBuffer sfvalueBuff = new StringBuffer();
				if(StrKit.isBlank(specivalues)){
					//没有传规格值，视统一规格
//					String promoPrice = promotionService.getProductPromotionPrice(product);
//	    			if(StrKit.notBlank(promoPrice)) orderItem.setPrice(promoPrice);
	    			
//	    			List<AgentProduct> list = agentProductService.findBySpeAndProductId(product.getId(), null);
//	    			if(list != null && list.size() > 0){
//	    				//匹配该用户的等级分销价格
//	    				for (AgentProduct agentProduct : list) {
//	    					if(agentProduct.getAgentPrice() != null){
//	    						orderItem.setPrice(agentProduct.getAgentPrice().toString());
//	    						break;
//	    					}
//	    				}
//	    			}
				}else{
					JSONArray jsonArr = JSON.parseArray(specivalues);
	        		if(jsonArr==null || jsonArr.size()<=0){
	        			//没有传规格值，视统一规格
//						String promoPrice = promotionService.getProductPromotionPrice(product);
//		    			if(StrKit.notBlank(promoPrice)) orderItem.setPrice(promoPrice);
		    			
//		    			List<AgentProduct> list = agentProductService.findBySpeAndProductId(product.getId(), null);
//		    			if(list != null && list.size() > 0){
//		    				//匹配该用户的等级分销价格
//		    				for (AgentProduct agentProduct : list) {
//		    					if(agentProduct.getAgentPrice() != null){
//		    						orderItem.setPrice(agentProduct.getAgentPrice().toString());
//		    						break;
//		    					}
//		    				}
//		    			}
	        		}else{
	        			//多规格
						List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();
		        		for(int k=0;k<jsonArr.size();k++){
		        			JSONObject json = jsonArr.getJSONObject(k);
		        			Long spvid = json.getLong("spvId");
		        			sfvalueBuff.append(spvid).append(",");
		        			specificationValues.add(specValueDao.findById(spvid));
		        		}
		        		orderItem.setSpecificationValues(specificationValues);
		        		ProductSpecItem stock = prodSpecItemdao.findFirst(
		        				"select * FROM "+ProductSpecItem.table+" WHERE product_id = ? and specification_value = ?", 
		        				product.getId(), sfvalueBuff.deleteCharAt(sfvalueBuff.length()-1).toString());
		        		if(stock == null || stock.getPrice() == null){
		        			throw new OrderException("请选择完整的规格值");
		        		}
		        		orderItem.setPrice(stock.getPrice().toString());
		        		//限时折扣价
//		        		String promoPrice = promotionService.getProductPromotionPrice(product, stock);
//		        		if(StrKit.notBlank(promoPrice)) orderItem.setPrice(promoPrice);
		        		
		        		//商品是否有分销价
//		    			List<AgentProduct> list = agentProductService.findBySpeAndProductId(product.getId(), sfvalueBuff.toString());
//		    			if(list != null && list.size() > 0){
//		    				BigDecimal min = null;
//		    				BigDecimal max = null;
//		    				//匹配该用户的等级分销价格
//		    				for (AgentProduct agentProduct : list) {
//		    					if(min == null && max == null){
//		    						min = agentProduct.getAgentPrice();
//		    						max = agentProduct.getAgentPrice();
//		    					}else if(min.compareTo(agentProduct.getAgentPrice()) == 1){
//		    						min = agentProduct.getAgentPrice();
//		    					}else if(max.compareTo(agentProduct.getAgentPrice()) == -1){
//		    						max = agentProduct.getAgentPrice();
//		    					}
//		    				}
//		    				
//		    				if(min != null && max != null){
//		    					if(min.compareTo(max) == 0){
//		    						orderItem.setPrice(min.toString());
//		    					}else{
//		    						orderItem.setPrice(min + "~" + max);		    						
//		    					}
//		    				}
//		    			}
	        		}
				}
				
				//订单项商品小计金额
				orderItem.setTotalPrice(new BigDecimal(orderItem.getQuantity())
						.multiply(new BigDecimal(orderItem.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP));
				orderItemDtos.add(orderItem);
				
				//计算商品满减数据
//				List<ProdFullCutResultDto> fullCuts = fullCutService.getProductFullCut(product);
//				if(fullCuts != null && fullCuts.size()>0){
//					orderItem.setFullCutDtos(fullCuts);
//				}
			}
		}
		
		//商品总数量
		Integer num = 0;
		for(OrderItemResultDto orderItemDto : orderDto.getOrderItems()){
			num += orderItemDto.getQuantity();
		}
		orderDto.setNum(num);
		//订单总金额，不包含邮费
		BigDecimal totalPrice = new BigDecimal(0);
		for(OrderItemResultDto orderItemDto : orderDto.getOrderItems()){
			totalPrice = totalPrice.add(orderItemDto.getTotalPrice());
		}
		orderDto.setTotalPrice(totalPrice);
		
		//计算邮费
		BigDecimal orderPostFee = new BigDecimal(0);
		if(receiver != null){
			for(OrderItemResultDto orderItemDto : orderDto.getOrderItems()){
				Product product = productDao.findById(orderItemDto.getProductId());
				BigDecimal postFees = getDeliveryFees(product, orderDto, receiver, orderDto.getNum());
				orderPostFee = orderPostFee.add(postFees);	
			}
		}
		orderDto.setPostFee(orderPostFee);
		//计算满减
		setFullCut(orderDto);
		//计算会员价
//		if(buyerId !=null){
//			BuyerUser buyer = buyerUserdao.findById(buyerId);
//			if(buyer !=null){
//				setMemberDiscount(buyer, orderDto);
//			}
//		}
		//最终得出订单应支付金额
		orderDto.setPayFee(orderDto.getTotalPrice().add(orderDto.getPostFee()).setScale(2, BigDecimal.ROUND_HALF_UP));
		return orderDto;
	}

	/**
	 * 设置会员优惠
	 * @param user
	 * @param orderDto
	 */
//	private void setMemberDiscount(BuyerUser user, OrderResultDto orderDto){
//		BuyerCard buyerCard = buyerCardDao.findFirst("select * from " + BuyerCard.table + " where buyer_id=? and status=1 and active=1", user.getId());
//		if(buyerCard != null){
//
//			AuthUser authUser = authUserService.findById(user.getAppId());
//			if(authUser == null) return;
//
//			Map<String, String> paramsCardInfoMap = ParaMap.create().put("card_id", buyerCard.getCardId()).put("code", buyerCard.getUserCardCode()).getData();
//			ApiResult cardInfoResult = CompCardApi.memberCardInfo(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsCardInfoMap));
//			if(cardInfoResult.isSucceed() && "NORMAL".equals(cardInfoResult.getStr("user_card_status"))){
//				//检查会员卡状态跟时效
//				if(user.getMemberRankId() != null){
//					MemberRank rank = mbRankdao.findById(user.getMemberRankId());
//					//按会员等级进行优惠
//					if(rank != null){
//						//先打折，再满减
//						orderDto.setOldPrice(orderDto.getTotalPrice());
//						orderDto.setTotalPrice(orderDto.getTotalPrice().multiply(rank.getRankDiscount().divide(new BigDecimal(100))));
//						if(rank.getRankCashFull() != null && rank.getRankCashRward()!=null){//会员等级满减规则
//							if(orderDto.getTotalPrice().compareTo(rank.getRankCashFull()) == 1){
//								orderDto.setOldPrice(orderDto.getTotalPrice());
//								orderDto.setTotalPrice(orderDto.getTotalPrice().subtract(rank.getRankCashRward()));
//							}
//						}
//					}
//				}else{
//					//按普通会员进行优惠，即当前会员卡进行优惠
//					Card card = cardDao.findFirst("select * from " + Card.table + " where card_id=? ", buyerCard.getCardId());
//					if(card != null && card.getDiscount()!=null){
//						orderDto.setOldPrice(orderDto.getTotalPrice());
//						orderDto.setTotalPrice(orderDto.getTotalPrice().multiply(new BigDecimal(100).subtract(new BigDecimal(card.getDiscount()).divide(new BigDecimal(1000)))));
//					}
//				}
//			}
//		}
//	}
	
	/**
	 * 计算订单是否包含满减商品
	 * @param orderDto
	 */
	private void setFullCut(OrderResultDto orderDto){
		//该订单的满减数据，算法是：把订单中所有商品的满减设置数据找出来，
		//然后从小到大进行排序，最后把订单总金额跟集合中的满减数据一一比较，从小比到大，直到找到符合条件的满减数据
		List<ProdFullCutResultDto> orderFullCutDtos = new ArrayList<ProdFullCutResultDto>();
		for(OrderItemResultDto orderItemDto : orderDto.getOrderItems()){
			if(orderItemDto.getFullCutDtos() != null && orderItemDto.getFullCutDtos().size()>0){
				orderFullCutDtos.addAll(orderItemDto.getFullCutDtos());
			}
		}
		/**
		 * 按满减的金额进行升序排列
		 */
		Collections.sort(orderFullCutDtos, new Comparator<ProdFullCutResultDto>(){
			@Override
			public int compare(ProdFullCutResultDto o1, ProdFullCutResultDto o2) {
				return o1.getMeet().subtract(o2.getMeet()).intValue();
			}
		});
		BigDecimal fullCutTotalPrice = new BigDecimal(0);
		for(ProdFullCutResultDto fullCut : orderFullCutDtos){
			if(orderDto.getTotalPrice().compareTo(fullCut.getMeet()) != -1){//一个个比，从小比到大
				if(fullCut.getCash() != null){
					fullCutTotalPrice = orderDto.getTotalPrice().subtract(fullCut.getCash()).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				if(fullCut.getPostage() == 1){
					//说明包邮
					if(orderDto.getPostFee().compareTo(new BigDecimal(0)) ==1){
						orderDto.setOldPostFee(orderDto.getPostFee());
						orderDto.setPostFee(new BigDecimal(0));
					}
				}
			}
		}
		
		//说明有满减价格
		if(fullCutTotalPrice.compareTo(new BigDecimal(0)) ==1){
			orderDto.setOldPrice(orderDto.getTotalPrice());
			orderDto.setTotalPrice(fullCutTotalPrice);
		}
	}
    
	/**
	 * 算商品邮费
	 * @param product
	 * @param orderDto
	 * @param buyerReceiver
	 * @param pnum （订单中商品的总数量）按件算邮费使用
	 * @return
	 */
	@Override
	public BigDecimal getDeliveryFees(Product product, OrderResultDto orderDto, BuyerReceiver buyerReceiver, final int pnum) throws OrderException{
		if(product.getDeliveryType() == null || product.getDeliveryType()==0)
			//统一邮费
			return product.getDeliveryFees() == null ? new BigDecimal(0) : product.getDeliveryFees();
			
		//根据邮费模板算邮费
		BigDecimal deliveryFees = new BigDecimal(0);
		DeliveryTemplate dt = deliveryTpldao.findById(product.getDeliveryTemplateId());
		if(dt == null || dt.getActive() !=1) return deliveryFees;
		
		//找出运费模板的配置项
		List<DeliverySet> ds = deliveryDao.find("SELECT * FROM "+DeliverySet.table+"  WHERE template_id = ? and active =1", product.getDeliveryTemplateId());
		if(ds == null || ds.size()<=0) return deliveryFees; //throw new OrderException("计算邮费出错，运费模板没有配置项");
    	
		//找出买家收货地址id
		final String areaId=buyerReceiver.getAreaTreePath()+buyerReceiver.getAreaId();
		if(dt.getValuationType()==1){
    		//按商品件数计算邮费
    		for (DeliverySet deliverySet : ds) {
				String setAreaIds = deliverySet.getAreaId();
				if(contains(setAreaIds, areaId)){
					if(pnum<=deliverySet.getAddStandards()){
						//如果商品数量少于或等于模板中设置的起始值，按起始邮费算
						return deliverySet.getStartFees();
					}else{
						//如果商品数量大于起始值，计算超过的商品数量
						final int overNum = pnum - deliverySet.getStartStandards();
						//看看商品到底超过多少件
						int count = 0;
						if(deliverySet.getAddStandards() == null || deliverySet.getAddStandards() == 0)
							count = 0;
						else{
							count = overNum % deliverySet.getAddStandards() == 0 ? overNum/deliverySet.getAddStandards() : overNum/deliverySet.getAddStandards() + 1;
						}
						return deliverySet.getStartFees().add(new BigDecimal(count).multiply(deliverySet.getAddFees()));
					}
				}
			}	
    	}else if(dt.getValuationType()==2){
    		//按商品物流重量算邮费
    		//1.获取规格
    		List<SpecificationValue> specificationValues = null;
    		List<OrderItemResultDto> orderItems = orderDto.getOrderItems();
    		for(OrderItemResultDto orderItem : orderItems){
    			if(orderItem.getProductId() == product.getId()){
    				specificationValues = orderItem.getSpecificationValues();
    				break;
    			}
    		}
    		
    		if(specificationValues == null) throw new OrderException("计算邮费出错，找不到对应的规格值");
    		
    		StringBuffer specifValues = new StringBuffer();
    		for(SpecificationValue sv : specificationValues){
    			specifValues.append(sv).append(",");
    		}
    		
    		ProductSpecItem productStock = prodSpecItemdao.findFirst(
    				"select * from " + ProductSpecItem.table + " where product_id=? and specification_value=? ",
    				product.getId(), specifValues.deleteCharAt(specifValues.length()-1).toString());
    		
    		if(productStock == null) throw new OrderException("计算邮费出错，找不到对应规格设置的物流重量值");
    		
    		final int weight = productStock.getWeight().intValue();//物流重量
    		for (DeliverySet deliverySet : ds) {
    			String setAreaIds = deliverySet.getAreaId();
    			if(contains(setAreaIds, areaId)){
    				if(weight<=deliverySet.getAddStandards()){
						//如果商品数量少于或等于模板中设置的起始值，按起始邮费算
						return deliverySet.getStartFees();
					}else{
						//如果商品数量大于起始值，计算超过的重量
						final int overNum = weight - deliverySet.getStartStandards();
						//看看商品到底超过多少重量
						final int count = overNum % deliverySet.getAddStandards() == 0 ? overNum/deliverySet.getAddStandards() : overNum/deliverySet.getAddStandards() + 1;
						return deliverySet.getStartFees().add(new BigDecimal(count).multiply(deliverySet.getAddFees()));
					}
    			}
			}
    	}
		return deliveryFees;
	}
	
	/**
	 * 判断邮件模板中设置的地址是否包含买家的收货地址
	 * @param areaIdsets
	 * @param areaIds
	 * @return
	 */
	private boolean contains(String areaIdsets, String areaIds){
		for(String areaIdSet : areaIdsets.split(",")){
			for(String areaId : areaIds.split(",")){
				if(areaIdSet.equals(areaId)) return true;
			}
		}
		return false;
	}

	public Boolean isReviewed(Long buyerId, Long orderId, Long productId) throws OrderException {
		String sql = "SELECT count(*) FROM "+ProductReview.table+" review WHERE review.buyer_id = ? AND review.product_id = ? AND review.order_id= ?";
		Long count =Db.queryLong(sql,buyerId,productId,orderId);
		return count > 0;
	}

	@Override
	public void cancel(Long orderId) throws OrderException {
		if(orderId == null) throw new OrderException("取消订单缺少必要参数");
		Order order = orderdao.findById(orderId);
		if(order == null || order.getPaymentStatus() != PaymentStatus.unpaid.ordinal()) throw new OrderException("该订单不能取消，或已支付");
		order.setOrderStatus(OrderStatus.cancelled.ordinal());
//		List<AgentCommRcd> list = agentCommRcdService.findByOrderId(order.getId());
//		if(list != null && list.size() > 0){
//			for (AgentCommRcd agentCommRcd : list) {
//				agentCommRcd.setActive(false).setUpdated(new Date());
//				agentCommRcdService.update(agentCommRcd);
//			}
//		}
		order.update();
	}
	
	@Override
	public void confirm(Long orderId) throws OrderException {
		if(orderId == null) throw new OrderException("确认订单缺少必要参数");
		Order order = orderdao.findById(orderId);
		if(order == null 
				|| order.getPaymentStatus() != PaymentStatus.paid.ordinal() 
				|| order.getShippingStatus() != ShippingStatus.shipped.ordinal()) 
			throw new OrderException("该订单不能确认收货");
		order.setOrderStatus(OrderStatus.completed.ordinal());//确认收货视为交易成功
		order.update();		
	}
	
	@Override
	public OrderPayResultDto getOrderPayInfo(Long orderId) throws OrderException {
		if(orderId == null) throw new OrderException("获取订单缺少必要参数");
		
		Order order = orderdao.findById(orderId);
		if(order == null || order.getPaymentStatus() == PaymentStatus.paid.ordinal())
			throw new OrderException("订单不存在或已支付");
		
		OrderPayResultDto orderPayResultDto = new OrderPayResultDto();
		orderPayResultDto.setOrderSn(order.getOrderSn());
		orderPayResultDto.setPayFee(order.getPayFee());
		orderPayResultDto.setTradeNo(order.getTradeNo());
		
		return orderPayResultDto;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.order.OrderService#getCountByBuyerAndStatus(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Long getCountByBuyerAndStatus(Long buyerId, Integer status) throws OrderException {
		Long count = null;
		if(status == 1)
			//待付款
			count = Db.queryLong("select count(id) from " + Order.table + " where buyer_id=? and payment_status=0 and order_status=0 and shipping_status=0 ", buyerId);
		else if(status == 2){
			//待发货
			//已支付，未发货（不含拼团订单）
			count = Db.queryLong("select count(id) from " + Order.table + " where buyer_id=? and order_type!=2 and payment_status=2 and shipping_status=0 and order_status=0", buyerId);
			//已支付的拼团成功订单
			Long count2 = Db.queryLong("select count(id) from " + Order.table + " where buyer_id=? and order_type=2 and payment_status=2 and shipping_status=0 and group_status=1 ", buyerId);
			
			if(count !=null && count2 != null){
				count = count2.longValue() + count.longValue();
			}
		}
		else if(status == 3)
			//已支付，已发货，待收货（含拼团订单）
			count = Db.queryLong("select count(id) from " + Order.table + " where buyer_id=? and payment_status=2 and shipping_status=2 and order_status!=2", buyerId);
		else if(status == 4)
			//交易成功
			count = Db.queryLong("select count(id) from " + Order.table + " where buyer_id=? and order_status=2", buyerId);
		else if(status == 5){
			//待成团订单 已支付，拼团进行中
			count = Db.queryLong("select count(id) from " + Order.table + " where buyer_id=? and order_type=2 and payment_status=2 and group_status=0 and shipping_status=0 ", buyerId);
		}
		return count;
	}

	@Override
	public List<Record> getOrderByDay(Long appId) {
		return Db.find("select DATE_FORMAT(created,'%Y-%m-%d') as dt, count(id) as numCount, To_Days(DATE_FORMAT(NOW(),'%Y-%m-%d')) - To_Days(DATE_FORMAT(created,'%Y-%m-%d')) as dc " 
					+ " from t_order where payment_status=2 and app_id=? group by DATE_FORMAT(created,'%Y-%m-%d') HAVING dc<=30", appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.order.OrderService#getOrderByMonth(java.lang.Long)
	 */
	@Override
	public List<Record> getOrderByMonth(Long appId) {
		return Db.find("select DATE_FORMAT(created,'%Y-%m-%d') as dt, count(id) as numCount, To_Days(DATE_FORMAT(NOW(),'%Y-%m-%d')) - To_Days(DATE_FORMAT(created,'%Y-%m-%d')) as dc " 
				+ " from t_order where app_id=? group by DATE_FORMAT(created,'%Y-%m-%d') HAVING dc<=30", appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#getUnpayOrder()
	 */
	@Override
	public List<Order> getUnpayOrder() {
		return orderdao.find("select * from " + Order.table + " where payment_status=0 and order_status !=3");
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#unpayPressRcd(com.dbumama.market.model.Order)
	 */
	@Override
	public void unpayNotifyRcd(Order item) {
		if(DateTimeUtil.compareMinute(new Date(), item.getCreated()) >= 10){//10分钟后订单未支付
			OrderPressRcd orderPressRcd = orderPressRcddao.findFirst("select * from " + OrderPressRcd.table + " where order_id=?", item.getId());
			if(orderPressRcd == null){
				orderPressRcd = new OrderPressRcd();
				orderPressRcd.setOrderId(item.getId());
				orderPressRcd.setActive(true);
				orderPressRcd.setCreated(new Date());
				orderPressRcd.setUpdated(new Date());
				orderPressRcd.save();
				Jboot.sendEvent(new JbootEvent("order_unpay_notify", item));
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#getOrdersByGroup(java.lang.Long)
	 */
	@Override
	public List<Order> getOrdersByGroup(Long groupId) {
		return DAO.find("select * from " + Order.table + " where group_id=?", groupId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#cancelWhithMsg(com.dbumama.market.model.Order)
	 */
	@Override
	public void cancelWhithMsg(Order order) {
		order.setOrderStatus(OrderStatus.cancelled.ordinal());
//		if(order.getOrderType().intValue() == OrderType.agent.ordinal()){
//			//查找该订单是否存在佣金记录，更新其使用状态
//			List<AgentCommRcd> list = agentCommRcdService.findByOrderId(order.getId());
//			if(list != null && list.size() > 0){
//				for (AgentCommRcd agentCommRcd : list) {
//					agentCommRcd.setActive(false).setUpdated(new Date());
//					try {
//						agentCommRcdService.update(agentCommRcd);
//					} catch (Exception e) {
//						throw new OrderException("更新佣金记录状态失败");
//					}
//				}
//			}
//		}
		update(order);
		Jboot.sendEvent(new JbootEvent("order_closed", order));
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderService#getUnpayNeedNotifyOrder()
	 */
	@Override
	public List<Order> getUnpayNeedNotifyOrder() {
		List<Order> orders = getUnpayOrder();
		
		List<Order> _orders = new ArrayList<Order>();
		for(Order _order: orders){
			OrderPressRcd orderPressRcd = orderPressRcdService.findByOrderId(_order.getId());
			if(orderPressRcd == null){
				_orders.add(_order);
			}
		}
		
		return _orders;
	}

	@Override
	public void sendPickupGood(Long orderId,Long appId) {
		if(orderId == null){
			throw new OrderException("调用订单发货接口缺少必要参数");
		}
		
		Order order = orderdao.findById(orderId);
		
		if(order == null || order.getPaymentStatus() != PaymentStatus.paid.ordinal()) throw new OrderException("只有支付状态的订单才能发货");

		if(order.getAppId().intValue() != appId.intValue()) throw new OrderException("别人的订单不用你发货哦!!!");
		
		if(order.getShippingStatus() == ShippingStatus.shipped.ordinal()) throw new OrderException("订单已发货，无需重复发货");
		
		order.setShippingStatus(ShippingStatus.shipped.ordinal());
		order.update();
	}

	@Override
	public Integer getCountByBuyer(Long buyerId) {
		
		final String select = "SELECT count(id) from "+Order.table+"WHERE  buyer_id = ?";
		Integer count = Db.queryInt(select, buyerId);
		if(count == null) return 0;
		
		return count;
	}

	@Override
	public List<OrderMobileResultDto> list4Refunds(OrderListParamDto orderParamDto) throws OrderException {
		if(orderParamDto == null || orderParamDto.getAuthUserId() == null )
			throw new OrderException("调用手机端订单列表数据接口缺少参数");
		
		String select = " SELECT  * ";
		String sqlExceptSelect = " FROM "+Order.table;

		QueryHelper queryHelper = new QueryHelper(select, sqlExceptSelect);
		queryHelper.addWhere("buyer_id", orderParamDto.getBuyerId())
		.addWhere("app_id", orderParamDto.getAuthUserId())
		.addWhere("payment_status", StrKit.notBlank(orderParamDto.getPaymentStatus()) ? PaymentStatus.valueOf(orderParamDto.getPaymentStatus()).ordinal() : null)
		.addWhere("order_status", 0)
		.addOrderBy("desc", "created");
		
		queryHelper.build();
		
		Page<Order> orders = orderdao.paginate(orderParamDto.getPageNo(), orderParamDto.getPageSize(), queryHelper.getSelect(), queryHelper.getSqlExceptSelect(), queryHelper.getParams());

		List<OrderMobileResultDto> results = new ArrayList<OrderMobileResultDto>();
		for(Order order : orders.getList()){
			OrderMobileResultDto orderListResultDto = new OrderMobileResultDto();
			orderListResultDto.setCreated(order.getCreated());
			orderListResultDto.setOrderId(order.getId());
			orderListResultDto.setSn(order.getOrderSn());
			
			orderListResultDto.setOrderStatus(order.getOrderStatus());
			orderListResultDto.setPaymentStatus(order.getPaymentStatus());
			orderListResultDto.setShipStatus(order.getShippingStatus());
			
			if(order.getPaymentStatus() == PaymentStatus.partialRefunds.ordinal()){
				orderListResultDto.setStatus("部分退款");
			}else if(order.getPaymentStatus() == PaymentStatus.refunded.ordinal()){
				orderListResultDto.setStatus("已退款");
			}else if(order.getPaymentStatus() == PaymentStatus.waitRefund.ordinal()){
				orderListResultDto.setStatus("等待退款");
			}

			orderListResultDto.setTotalPrice(order.getPayFee().toString());
			orderListResultDto.setOrderItems(getOrderItems(orderListResultDto.getOrderId()));
			results.add(orderListResultDto);
		}
		
		return results;
	}

}