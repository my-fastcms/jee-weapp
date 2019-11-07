package com.dbumama.market.service.api;

import com.dbumama.market.model.BuyerReceiver;
import com.dbumama.market.model.Order;
import com.dbumama.market.model.Product;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

	public Integer getCountByBuyer(Long buyerId);

	OrderResultDto getOrderResult(Record record);

	Order create(OrderCreateParamDto orderParamDto, OrderResultDto orderDto);

	BigDecimal getDeliveryFees(Product product, OrderResultDto orderDto, BuyerReceiver buyerReceiver, final int pnum) throws OrderException;

	void setQuery(QueryHelper helper, OrderListParamDto orderParamDto);

	public void sendPickupGood(Long orderId, Long appId);

	List<Record> getOrderByDay(Long appId);

	List<Record> getOrderByMonth(Long appId);

	List<Order> getOrdersByGroup(Long groupId);

	/**
	 * 获取所有未支付订单，定时任务用
	 * 
	 * @return
	 */
	List<Order> getUnpayOrder();

	/**
	 * 获取未支付订单，需要通知提醒的订单
	 * 
	 * @return
	 */
	List<Order> getUnpayNeedNotifyOrder();

	/**
	 * 订单催付记录，定时任务用
	 * 
	 * @param order
	 */
	void unpayNotifyRcd(Order order);

	/**
	 * 管理后台查询订单列表
	 * 
	 * @param orderParamDto
	 * @return
	 * @throws OrderException
	 */
	public Page<OrderResultDto> list(OrderListParamDto orderParamDto) throws OrderException;

	/**
	 * 微信端创建订单
	 * 
	 * @param orderParamDto
	 * @throws OrderException
	 */
	public Long create(OrderCreateParamDto orderParamDto) throws OrderException;

	/**
	 * 微信端取消订单
	 * 
	 * @param orderId
	 * @throws OrderException
	 */
	public void cancel(Long orderId) throws OrderException;

	/**
	 * 取消订单，并发送模板消息
	 * 
	 * @param order
	 */
	void cancelWhithMsg(Order order);

	/**
	 * 微信端订单确认收货
	 * 
	 * @param orderId
	 * @throws OrderException
	 */
	public void confirm(Long orderId) throws OrderException;

	/**
	 * 微信端订单列表
	 * 
	 * @return
	 * @throws OrderException
	 */
	public List<OrderMobileResultDto> list4Mobile(OrderListParamDto orderParamDto) throws OrderException;

	/**
	 * 小程序退款订单列表
	 * 
	 * @return
	 * @throws OrderException
	 */
	public List<OrderMobileResultDto> list4Refunds(OrderListParamDto orderParamDto) throws OrderException;

	/**
	 * 订单详情
	 * 
	 * @param orderId
	 * @return
	 * @throws OrderException
	 */
	public OrderResultDto getOrder(Long orderId) throws OrderException;

	/**
	 * 获取订单支付信息
	 * 
	 * @param orderId
	 * @return
	 * @throws OrderException
	 */
	public OrderPayResultDto getOrderPayInfo(Long orderId) throws OrderException;

	/**
	 * 批量获取订单详情
	 * 
	 * @param orderIds
	 * @return
	 * @throws OrderException
	 */
	public List<OrderResultDto> getOrders(String orderIds) throws OrderException;

	/**
	 * 批量发货
	 * 
	 * @param items
	 * @throws OrderException
	 */
	public void batchShipping(Long appId, String items) throws OrderException;

	/**
	 * 发货
	 * 
	 * @param sendGoodParam
	 * @throws OrderException
	 */
	public String shipping(SendGoodParamDto sendGoodParam) throws OrderException;

	/**
	 * 订单原路退款
	 * @throws OrderException
	 */
	public void refund(RefundParamDto refundParam) throws OrderException;

	public void refund(Long appId, String items) throws OrderException;

	/**
	 * 调用微信接口退款
	 * 
	 * @param order
	 * @throws OrderException
	 */
	public void refund(Order order) throws WxmallBaseException;

	/**
	 * 买家申请退款
	 * 
	 * @param buyerId
	 * @param orderId
	 * @throws WxmallBaseException
	 */
	public void applyRefund(Long buyerId, Long orderId) throws WxmallBaseException;

	/**
	 * 结算订单
	 * 
	 * @param buyerId    买家
	 * @param receiverId 收货地址
	 * @param items      商品项
	 * @return
	 * @throws PayException
	 */
	public OrderResultDto balance(Long buyerId, Long receiverId, String items) throws OrderException;

	/**
	 * 根据状态获取买家订单数量
	 * 
	 * @param buyerId
	 * @param status
	 * @return
	 * @throws OrderException
	 */
	public Long getCountByBuyerAndStatus(Long buyerId, Integer status) throws OrderException;

	/**
	 * find model by primary key
	 *
	 * @param id
	 * @return
	 */
	public Order findById(Object id);

	/**
	 * find all model
	 *
	 * @return all <Order
	 */
	public List<Order> findAll();

	/**
	 * delete model by primary key
	 *
	 * @param id
	 * @return success
	 */
	public boolean deleteById(Object id);

	/**
	 * delete model
	 *
	 * @param model
	 * @return
	 */
	public boolean delete(Order model);

	/**
	 * save model to database
	 *
	 * @param model
	 * @return id value if save success
	 */
	public Object save(Order model);

	/**
	 * save or update model
	 *
	 * @param model
	 * @return id value if save or update success
	 */
	public Object saveOrUpdate(Order model);

	/**
	 * update data model
	 *
	 * @param model
	 * @return
	 */
	public boolean update(Order model);

	/**
	 * page query
	 *
	 * @param page
	 * @param pageSize
	 * @return page data
	 */
	public Page<Order> paginate(int page, int pageSize);

	/**
	 * page query by columns
	 *
	 * @param page
	 * @param pageSize
	 * @param columns
	 * @return page data
	 */
	public Page<Order> paginateByColumns(int page, int pageSize, Columns columns);

	/**
	 * page query by columns
	 *
	 * @param page
	 * @param pageSize
	 * @param columns
	 * @param orderBy
	 * @return page data
	 */
	public Page<Order> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}