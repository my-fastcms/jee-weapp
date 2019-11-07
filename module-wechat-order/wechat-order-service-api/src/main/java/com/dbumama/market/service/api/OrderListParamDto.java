package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractPageParamDto;

@SuppressWarnings("serial")
public class OrderListParamDto extends AbstractPageParamDto{

	private Long buyerId;
	private String startDate;		//下单开始时间
	private String endDate;			//下单结束时间
	private String buyerNickName;	//下单人昵称
	private String receiverName;	//收货人
	private String receiverPhone;	//收货人手机
	private String orderStatus;		//订单状态
	private String paymentStatus;	//支付状态
	private String shippingStatus;	//发货状态
	private String groupStatus;		//组团状态
	private String orderType;
	private String orderSn;
	
	public OrderListParamDto() {
	}
	
	public OrderListParamDto(Long authUserId, Integer pageNo) {
		super(authUserId, pageNo);
	}

	public OrderListParamDto(Long authUserId, Long buyerId, Integer pageNo) {
		super(authUserId, pageNo);
		this.buyerId = buyerId;
	}
	
	/**
	 * @return the groupStatus
	 */
	public String getGroupStatus() {
		return groupStatus;
	}

	/**
	 * @param groupStatus the groupStatus to set
	 */
	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}
	
	/**
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public Long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getBuyerNickName() {
		return buyerNickName;
	}
	public void setBuyerNickName(String buyerNickName) {
		this.buyerNickName = buyerNickName;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverPhone() {
		return receiverPhone;
	}
	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getShippingStatus() {
		return shippingStatus;
	}
	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}
	/**
	 * @return the orderSn
	 */
	public String getOrderSn() {
		return orderSn;
	}

	/**
	 * @param orderSn the orderSn to set
	 */
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}
}
