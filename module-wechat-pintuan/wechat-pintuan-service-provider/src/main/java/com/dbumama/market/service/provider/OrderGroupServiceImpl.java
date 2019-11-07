package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.GroupStatus;
import com.dbumama.market.service.enmu.OrderStatus;
import com.dbumama.market.service.enmu.OrderType;
import com.dbumama.market.service.enmu.PaymentStatus;
import com.dbumama.market.service.sqlhelper.QueryHelper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
@RPCBean
public class OrderGroupServiceImpl extends WxmServiceBase<OrderGroup> implements OrderGroupService {

	@Inject
	private MultiGroupService multiGroupService;
	@Inject
	private OrderService orderService;
	@Inject
	private RefundErrorService refundErrorService;
	@Inject
	private ProductService productService;
	@Inject
	private BuyerUserService buyerUserService;

	private static final OrderGuser orderGuserdao = new OrderGuser().dao();
	private static final BuyerReceiver receiverDao = new BuyerReceiver().dao();
	private static final SpecificationValue specValueDao = new SpecificationValue().dao();

	@Override
	public Page<OrderTuanResultDto> list4Tuan(OrderListParamDto orderParamDto) throws OrderException {

		if(orderParamDto == null || orderParamDto.getAuthUserId() == null) return null;

		String select = " SELECT  o.*, o.id as o_id, o.created as o_created, b.id as b_id, b.nickname, b.open_id, "
				+ "r.*, r.phone as r_phone, r.city as r_city, r.province as r_province, r.district as r_district, "
				+ "si.bill_number, si.exp_key, si.exp_name, "
				+ "og.buyer_id as groupHeader, og.created as groupCreated, mg.name as mgname, mg.offer_num ";
		String sqlExceptSelect = " FROM "+Order.table+" o "
				+ " left join " + BuyerUser.table + " b on o.buyer_id=b.id "
				+ " left join " + BuyerReceiver.table + " r on o.receiver_id=r.id "
				+ " left join " + Shipping.table + " si on si.order_id=o.id "
				+ " left join " + OrderGroup.table + " og on o.group_id=og.id ";
//				+ " left join " + MultiGroup.table + " mg on og.multi_group_id=mg.id";

		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("o.order_type", OrderType.pintuan.ordinal());
		helper.addWhere("o.order_sn", orderParamDto.getOrderSn());
		orderService.setQuery(helper, orderParamDto);

		helper.build();

		Page<Record> orders = Db.paginate(orderParamDto.getPageNo(), orderParamDto.getPageSize(), helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		List<OrderTuanResultDto> orderList = new ArrayList<OrderTuanResultDto>();
		for(Record order : orders.getList()){
			OrderTuanResultDto tuanOrder = getOrderTuanResult(order);
			orderList.add(tuanOrder);
		}
		return new Page<OrderTuanResultDto>(orderList, orderParamDto.getPageNo(), orderParamDto.getPageSize(), orders.getTotalPage(), orders.getTotalRow());

	}

	@Override
	@Before(Tx.class)
	public Long gcreate(OrderCreateParamDto orderParamDto) throws OrderException {
		if(orderParamDto == null || orderParamDto.getBuyerId() == null
				|| (orderParamDto.getReceiverId() == null && orderParamDto.getShopId() ==null)
				|| StrKit.isBlank(orderParamDto.getItems()) || orderParamDto.getAuthUserId() == null){
			throw new OrderException("创建拼团订单失败，请检查参数");
		}
		//解析出提交的订单数据
		OrderResultDto orderDto = null;
		try {
			orderDto = gbalance(orderParamDto.getBuyerId(), orderParamDto.getReceiverId(), orderParamDto.getItems());
		} catch (OrderException e) {
			throw new OrderException(e.getMessage());
		}
		if(orderDto == null) throw new OrderException("创建拼团订单失败，解析订单数据出错");

		if(orderDto.getOrderItems() == null || orderDto.getOrderItems().size()<=0)
			throw new OrderException("创建拼团订单失败，商品不存在");

		//理论上这里拼团只有一个商品
		OrderItemResultDto orderItemDto = orderDto.getOrderItems().get(0);

		//检查是否需要关注和是否超过限购
		//MultiGroup group = checkFollowsAndQuota(orderParamDto.getBuyerId(),orderItemDto.getProductId(),orderItemDto.getQuantity());

		Product product = productService.findById(orderItemDto.getProductId());
		//记录开团数据
		OrderGroup orderGroup = new OrderGroup();
		orderGroup.setBuyerId(orderParamDto.getBuyerId());//团长
		orderGroup.setAppId(orderParamDto.getAuthUserId());
		//orderGroup.setMultiGroupId(group.getId());
		orderGroup.setProductId(product.getId());
		orderGroup.setActive(true);
		orderGroup.setCreated(new Date());
		orderGroup.setUpdated(new Date());
		try {
			orderGroup.save();
		}catch(Exception e){
			throw new OrderException(e.getMessage());
		}

		//支付成功再记录用户参团信息
/*		OrderGuser guser = new OrderGuser();
		guser.setBuyerId(orderParamDto.getBuyerId());
		guser.setGroupId(orderGroup.getId());
		guser.setActive(true);
		guser.setCreated(new Date());
		guser.setUpdated(new Date());
		try {
			guser.save();
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}*/

		Order order = orderService.create(orderParamDto, orderDto);
		order.setGroupId(orderGroup.getId()); 	//当前拼团订单属于哪一个拼团活动

		try {
			order.save();
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}

		//保存订单项数据
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
		try {
			orderItem.save();
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}

		return order.getId();
	}

	@Override
	public OrderResultDto gbalance(Long buyerId, Long receiverId, String items) throws OrderException {
		if(StrKit.isBlank(items)) throw new OrderException("调用结算接口缺少必要参数");

		JSONArray jsonArray = null;
		try {
			jsonArray = JSONArray.parseArray(items);
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}

		if(jsonArray==null || jsonArray.size()<=0) throw new OrderException("请选择要结算的项");

		BuyerReceiver receiver = null;
		if(receiverId == null){
			receiver = receiverDao.findFirst(" select * from " + BuyerReceiver.table + " where buyer_id=? and is_default=1", buyerId);
		}else{
			receiver = receiverDao.findById(receiverId);
		}

		OrderResultDto orderDto = new OrderResultDto();
		orderDto.setOrderType(OrderType.pintuan.ordinal());		//拼团订单
		List<OrderItemResultDto> orderItemDtos = new ArrayList<OrderItemResultDto>();
		orderDto.setOrderItems(orderItemDtos);
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			final String prodId = jsonObj.getString("productId");
			final String specivalues = jsonObj.getString("speci");
			Product product = productService.findById(prodId);
			if(product == null || product.getIsMarketable() == false
					|| product.getStock() == null || product.getStock()<=0){
				throw new OrderException("该商品目前不可拼团，可能是库存不足，或已下架，请见谅");
			}

			//检查下单是否是自提商品
			if(product.getIsPickUp() !=null && product.getIsPickUp()){
				if(jsonArray.size()>1) throw new OrderException("自提商品请单独下单");
				orderDto.setIsPickup(true);
			}

//			MultiGroup group = multiGroupService.getProductMultiGroup(product);
//			if(group == null) throw new OrderException("商品拼团活动已失效");
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
			if(StrKit.isBlank(specivalues)){
				//没有传规格值，视统一规格
//				BigDecimal groupPrice = multiGroupService.getCollagePrice(product, null);
//    			if(groupPrice != null) orderItem.setPrice(groupPrice.toString());
			}else{
				//多规格
				final StringBuffer sfvalueBuff = new StringBuffer();
				List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();
				JSONArray jsonArr = JSON.parseArray(specivalues);

				if(jsonArr !=null && jsonArr.size()<=0){
					//单规格处理
//        			BigDecimal groupPrice = multiGroupService.getCollagePrice(product, null);
//        			if(groupPrice != null) orderItem.setPrice(groupPrice.toString());
				}else{
					for(int k=0;k<jsonArr.size();k++){
						JSONObject json = jsonArr.getJSONObject(k);
						Long spvid = json.getLong("spvId");
						sfvalueBuff.append(spvid).append(",");
						specificationValues.add(specValueDao.findById(spvid));
					}
					orderItem.setSpecificationValues(specificationValues);
//            		BigDecimal groupPrice = multiGroupService.getCollagePrice(product, sfvalueBuff.deleteCharAt(sfvalueBuff.length() - 1).toString());
//            		if(groupPrice != null){
//            			orderItem.setPrice(groupPrice.toString());
//            		}
				}
			}
			//订单项商品小计金额
			orderItem.setTotalPrice(new BigDecimal(orderItem.getQuantity())
					.multiply(new BigDecimal(orderItem.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP));
			orderItemDtos.add(orderItem);
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
				Product product = productService.findById(orderItemDto.getProductId());
				BigDecimal postFees = orderService.getDeliveryFees(product, orderDto, receiver, orderDto.getNum());
				orderPostFee = orderPostFee.add(postFees);
			}
		}
		orderDto.setPostFee(orderPostFee);

		//最终得出订单应支付金额
		orderDto.setPayFee(orderDto.getTotalPrice().add(orderDto.getPostFee()).setScale(2, BigDecimal.ROUND_HALF_UP));
		return orderDto;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderGroupService#findUnSuccessGroup()
	 */
	@Override
	public List<OrderGroup> findGroupingGroups() {
		return DAO.find("select * from " + OrderGroup.table + " where group_status=0");
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderGroupService#makeSuccess(com.dbumama.market.model.OrderGroup)
	 */
	@Override
	public void updateGroupStatus(OrderGroup orderGroup) {
		if(orderGroup == null) return;
		
		MultiGroup multiGroup = multiGroupService.findById(orderGroup.getMultiGroupId());
		if(multiGroup == null) return;

		List<Order> orders = orderService.getOrdersByGroup(orderGroup.getId());

		List<Order> validOrders = new ArrayList<Order>();

		for(Order order : orders){
			if(order.getPaymentStatus() == PaymentStatus.paid.ordinal()
					&& order.getOrderStatus() != OrderStatus.completed.ordinal()
					&& order.getOrderStatus() != OrderStatus.cancelled.ordinal()){
				//找出已支付，未取消未完成的订单
				validOrders.add(order);
			}
		}

		int count = validOrders.size();

		//规定时间内，达到拼团人数，则拼团成功
		if(count == multiGroup.getOfferNum()){
			orderGroup.setGroupStatus(GroupStatus.success.ordinal());
			update(orderGroup);

			for(Order _order: validOrders){
				if(_order.getGroupStatus() != GroupStatus.success.ordinal()){
					_order.setGroupStatus(GroupStatus.success.ordinal());
					_order.update();
				}
			}
		}else{
			//check 是否超时，当前开的团是否超时
			//开团时间
			Date created = orderGroup.getCreated();
			//时效，单位，小时
			Integer validTime = multiGroup.getValidTime();

			//缓冲30秒超时
			if(System.currentTimeMillis() > created.getTime() + ((validTime * 60 * 60 + 30) * 1000)){
				orderGroup.setGroupStatus(GroupStatus.timeout.ordinal());
				update(orderGroup);
				for(Order _order: validOrders){
					if(_order.getGroupStatus() != GroupStatus.timeout.ordinal()){
						_order.setGroupStatus(GroupStatus.timeout.ordinal());
						_order.update();
					}
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderGroupService#getTimeOutGroups()
	 */
	@Override
	public List<OrderGroup> getTimeOutGroups() {
		return DAO.find("select * from " + OrderGroup.table + " where group_status=3");
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderGroupService#dealTimeOutOrderGroup(com.dbumama.market.model.OrderGroup)
	 */
	@Override
	@Before(Tx.class)
	public void dealTimeOutOrderGroup(OrderGroup orderGroup) {

		if(orderGroup == null) {
			logger.info("===========================dealTimeOutOrderGroup orderGroup is null");
			return;	
		}
		
		//check orders
		MultiGroup multiGroup = multiGroupService.findById(orderGroup.getMultiGroupId());
		
		//拼团活动已过时
		if(multiGroup == null) {
			logger.info("===========================dealTimeOutOrderGroup multiGroup is null");
			return;
		}
		
		List<Order> orders = orderService.getOrdersByGroup(orderGroup.getId());
		
		if(orders == null) {
			logger.info("===========================dealTimeOutOrderGroup orders is null");
			return;	
		}
		
		//check 是否开启模拟成团
		if(multiGroup.getEnableMoniSuc()==null || !multiGroup.getEnableMoniSuc() || orders.size() == 1){
			//没有开启模拟成团，直接退款，不管差多少人才成团，所有人都直接退款
			//检查是否有其他人员加入
			//如果只有团长一个人参团，直接退款
			//退款
			for(Order order : orders){
				if(order.getGroupStatus() !=null && order.getGroupStatus() != GroupStatus.fail.ordinal()){
					order.setGroupStatus(GroupStatus.fail.ordinal());
					order.update();
				}

				try {
					orderService.refund(order);
				} catch (WxmallBaseException e) {
					//如果中途有退款接口调用失败，记录失败记录到数据库
					//由于此处把拼团超时状态更改后，不管退款成功与否，
					//定时任务是再也扫描不到这个拼团任务了，所以必须记录退款失败的情况
					RefundError rerror = refundErrorService.findByOrderId(order.getId());
					if(rerror == null){
						rerror = new RefundError();
						rerror.setOrderId(order.getId());
						rerror.setActive(true);
					}
					rerror.setRefundError(e.getMessage());
					refundErrorService.saveOrUpdate(rerror);
				}
			}

			orderGroup.setGroupStatus(GroupStatus.fail.ordinal());
			update(orderGroup);

			//send group fail event
			Jboot.sendEvent(new JbootEvent("group_fail", orders.get(0)));
		}else{
			//使用匿名机器人使组团成功
			//插入不够人数的机器人订单
			//差几个订单插入几个机器人订单数据
			_create(orderGroup);

			orderGroup.setGroupStatus(GroupStatus.success.ordinal());
			update(orderGroup);

			if(orders.size() >0 ){
				for(Order order : orders){
					if(order.getGroupStatus() != GroupStatus.success.ordinal()){
						order.setGroupStatus(GroupStatus.success.ordinal());
						order.update();
					}
				}
				Jboot.sendEvent(new JbootEvent("group_success", orders.get(0)));
			}

		}
		
	}

	@Override
	@Before(Tx.class)
	public Long joinGroup(OrderJoinParamDto orderJoinParam) throws OrderException {
		if(orderJoinParam == null || orderJoinParam.getGroupId() == null
				|| orderJoinParam.getBuyerId() == null
				||  (orderJoinParam.getReceiverId() == null && orderJoinParam.getShopId() ==null)
				|| StrKit.isBlank(orderJoinParam.getItems()) || orderJoinParam.getAuthUserId() == null)
			throw new UmpException("joinGroup方法，拼团缺少必要参数");

		OrderResultDto orderResult  = null;
		try {
			orderResult = gbalance(orderJoinParam.getBuyerId(), orderJoinParam.getReceiverId(), orderJoinParam.getItems());
		} catch (OrderException e) {
			throw new OrderException(e.getMessage());
		}

		if(orderResult == null || orderResult.getOrderItems() == null || orderResult.getOrderItems().size()<=0)
			throw new OrderException("拼团失败，解析订单数据出错");
		OrderItemResultDto orderItemDto = orderResult.getOrderItems().get(0);

		//检查是否需要关注和是否超过限购
		//MultiGroup multiGroup = checkFollowsAndQuota(orderJoinParam.getBuyerId(),orderItemDto.getProductId(),orderItemDto.getQuantity());

		//判断活动是否开启拼团条件
//		if(multiGroup.getGroupCondition() != null && multiGroup.getGroupCondition().intValue() == 1){
//			Integer count = getCountByBuyer(orderJoinParam.getBuyerId());
//			if(count > 0)
//				throw new OrderException("拼团失败，该活动仅限新用户参团");
//		}

		//检查发起者正在组团的订单
		OrderGroup orderGroup = DAO.findById(orderJoinParam.getGroupId());
		if(orderGroup == null) throw new OrderException("拼团失败，orderGroup is null");

		//检查拼团活动的时效
//		if(multiGroup.getValidTime() != null){
//			Integer expiresIn = multiGroup.getValidTime() * 3600; //转换成秒
//			Long expiredTime = orderGroup.getCreated().getTime() + ((expiresIn -5) * 1000);
//			if(expiredTime < System.currentTimeMillis()) throw new OrderException("拼团失败，本次拼团已过期");
//		}

		//检查拼团活动是否满员
//		List<OrderGuser> gusers = orderGuserdao.find("select * from " + OrderGuser.table + " where group_id=?", orderGroup.getId());
		//if(gusers!=null && gusers.size()>=multiGroup.getOfferNum()) throw new OrderException("拼团失败，该团已满员");

		//插入拼团者信息
		OrderGuser guser = orderGuserdao.findFirst("select * from " + OrderGuser.table + " where "
						+ " buyer_id=? and group_id=? AND active=1 ",
				orderJoinParam.getBuyerId(), orderGroup.getId());
		if(guser != null) throw new OrderException("您已参与本次拼团，不可重复拼团");

		//等到付款时才插入拼团者信息
//		guser = new OrderGuser();
//		guser.setBuyerId(orderJoinParam.getBuyerId());
//		guser.setGroupId(orderGroup.getId());
//		guser.setActive(true);
//		guser.setCreated(new Date());
//		guser.setUpdated(new Date());
//		try {
//			guser.save();
//		} catch (Exception e) {
//			throw new OrderException(e.getMessage());
//		}

		//创建订单
		Order order = orderService.create(orderJoinParam, orderResult);
		order.setGroupId(orderJoinParam.getGroupId());

		try {
			order.save();
		} catch (ActiveRecordException e) {
			throw new OrderException(e.getMessage());
		}

		Product product = productService.findById(orderItemDto.getProductId());
		//保存订单项数据
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
		try {
			orderItem.save();
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}
		//付款后才发送加入拼团信息
//		Jboot.sendEvent(new JbootEvent("group_join", order));

		return order.getId();
	}

	@Override
	public void cancelRefunds(Long orderId,Long buyerId) throws OrderException {
		if(orderId == null|| buyerId == null){
			throw new OrderException("订单撤销退款出错");
		}
		Order order = orderService.findById(orderId);
		Long orderbuyerId = order.getBuyerId();
		if(order == null || orderbuyerId==null ||
				buyerId.compareTo(orderbuyerId)!=0 ||
				order.getPaymentStatus()!=5 ||
				order.getOrderStatus()!=0){
			throw new OrderException("订单撤销退款出错");
		}
		//处理拼团订单撤销退款
		if(order.getOrderType() == OrderType.pintuan.ordinal()){
			//查询出拼团的团长
			//OrderGroup orderGroup = orderGroupdao.findFirst("SELECT * FROM t_order_group WHERE id=?",order.getGroupId());
			OrderGroup orderGroup = findById(order.getGroupId());
			//是否可以撤销退款,取决于团长是否把团解散了
			if(!orderGroup.getActive()) {
				throw new OrderException("亲,该团已解散,无法撤销退款申请");
			}

		}

		order.setPaymentStatus(PaymentStatus.paid.ordinal());
		order.setUpdated(new Date());
		orderService.update(order);
	}

	@Override
	public Integer getMultiGroupCount(Long buyerId, Long productId) {
		final String select = "SELECT sum(oi.quantity) from "+Order.table+" o LEFT JOIN "+OrderItem.table+" oi ON o.id = oi.order_id WHERE o.group_id is not null AND o.order_type = 2 AND o.buyer_id =? AND oi.product_id=? ";
		Integer count = Db.queryInt(select, buyerId,productId);
		return count == null ? 0 : count;
	}

	/**
	 * 获取拼团订单
	 * @param record
	 * @return
	 */
	private OrderTuanResultDto getOrderTuanResult(Record record){
		OrderResultDto orderResult = orderService.getOrderResult(record);
		OrderTuanResultDto tuanResult = new OrderTuanResultDto();
		tuanResult.setBuyerMemo(orderResult.getBuyerMemo());
		tuanResult.setBuyerMemoSimple(orderResult.getBuyerMemoSimple());
		tuanResult.setBuyerNick(orderResult.getBuyerNick());
		tuanResult.setNum(orderResult.getNum());
		tuanResult.setOldPostFee(orderResult.getOldPostFee());
		tuanResult.setOldPrice(orderResult.getOldPrice());
		tuanResult.setOrderCreated(orderResult.getOrderCreated());
		tuanResult.setOrderId(orderResult.getOrderId());
		tuanResult.setOrderSn(orderResult.getOrderSn());
		tuanResult.setOrderStatus(orderResult.getOrderStatus());
		tuanResult.setOrderItems(orderResult.getOrderItems());
		tuanResult.setOrderType(orderResult.getOrderType());
		tuanResult.setPayFee(orderResult.getPayFee());
		tuanResult.setPostFee(orderResult.getPostFee());
		tuanResult.setReceiverAddr(orderResult.getReceiverAddr());
		tuanResult.setReceiverCity(orderResult.getReceiverCity());
		tuanResult.setReceiverCountry(orderResult.getReceiverCountry());
		tuanResult.setReceiverName(orderResult.getReceiverName());
		tuanResult.setReceiverPhone(orderResult.getReceiverPhone());
		tuanResult.setReceiverProvince(orderResult.getReceiverProvince());
		tuanResult.setTransactionId(orderResult.getTransactionId());
		tuanResult.setTotalPrice(orderResult.getTotalPrice());
		tuanResult.setZipCode(orderResult.getZipCode());

		tuanResult.setOrderStatusInt(orderResult.getOrderStatusInt());
		tuanResult.setPaymentStatusInt(orderResult.getPaymentStatusInt());
		tuanResult.setShipStatusInt(orderResult.getShipStatusInt());

		tuanResult.setGroupCreated(record.getDate("groupCreated"));
		tuanResult.setGroupedStatusInt(orderResult.getGroupedStatusInt());
		tuanResult.setMultiGroupName(record.getStr("mgname"));
		tuanResult.setGroupInfo(record.getInt("offer_num") + "人团");
		tuanResult.setGroupId(record.getLong("group_id"));

		BuyerUser buyer = buyerUserService.findById(record.getLong("groupHeader"));

		tuanResult.setGroupHeader(buyer.getNickname());

		Long haveUserCount = Db.queryLong("select count(id) from " + OrderGuser.table + " where group_id=? AND active=1 ", tuanResult.getGroupId());

		int diffcount = record.getInt("offer_num").intValue() - haveUserCount.intValue();
		if(diffcount <=0) tuanResult.setDiffCount("已成团");
		else tuanResult.setDiffCount("还差" + diffcount + "成团");
		return tuanResult;
	}

	/**处理拼团订单退款*/
	@Before(Tx.class)
	private void spellGroupRefund(Order order)throws OrderException {
		//判断是否是团长退款,团长退款全团退款,团员退款就退一个
		Long groupId = order.getGroupId();
		//下单的用户
		Long appbuyerId = order.getBuyerId();
		//查询团长信息
		OrderGroup reorderGroup = DAO.findFirst("SELECT * FROM t_order_group WHERE id=? AND active=1",groupId);
		if(reorderGroup==null){
			//throw new OrderException("找不到拼团订单,可能已撤销了");
			//当团取消,就返回,不管了
			return;
		}
		//查询团队信息
		List<OrderGuser> groupGuser = orderGuserdao.find("SELECT * FROM t_order_guser WHERE group_id=? AND active=1", groupId);
		//拼团表的团长(用户)ID
		Long orderbuyerId = reorderGroup.getBuyerId();

		//这里表示是团长退款
		if(appbuyerId.intValue() == orderbuyerId.intValue()){
			// 查询一个拼团的所有已支付的订单
			List<Record> groupRefund = Db.find("SELECT id,buyer_id,payment_status FROM t_order WHERE order_type=? AND group_id=? AND active=1 AND payment_status=2 ", order.getOrderType(),order.getGroupId());
			for (Record record : groupRefund) {
				Long buyerId = record.getLong("buyer_id");
				Long orderId = record.getLong("id");
				Integer paymentStatus = record.getInt("payment_status");
				//支付状态为2才给你发起退款
				if(paymentStatus==PaymentStatus.paid.ordinal()){
					//把一个拼团的都修改成退款状态  ,全部退款
					orderService.applyRefund(buyerId,orderId);
					//直接调用退款接口,给其他团员退款
					//refund(groupOrder);
				}

			}
			//把团解散
			reorderGroup.setUpdated(new Date());
			reorderGroup.setActive(false);
			reorderGroup.update();
			//把团队信息的状态也改了
			for (OrderGuser guser : groupGuser) {
				guser.setUpdated(new Date());
				guser.setActive(false);
				guser.update();
			}
			return;
		}
		//团员退款 ,修改拼团表的团员信息,改成删除状态
		for (OrderGuser guser : groupGuser) {
			//拼团团队信息表的用户ID
			Long groupUserId = guser.getBuyerId();
			if(groupUserId.intValue() != appbuyerId.intValue()){
				continue;
			}
			guser.setUpdated(new Date());
			guser.setActive(false);
			guser.update();
		}

	}

	private void _create(OrderGroup orderGroup){
		//TODO
	}

}