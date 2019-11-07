package com.dbumama.market.service.api;

import java.util.Date;
import java.util.List;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * 手机端订单列表
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class OrderMobileResultDto extends AbstractResultDto{

	private Long orderId;
	private Long buyerId;
	private String sn;
	private String totalPrice;
	private Integer orderStatus;
	private Integer paymentStatus;
	private Integer shipStatus;
	private Integer groupedStatus;
	
	private String status;
	private String groupStatus;
	private Date created;
	private String orderType;
	private List<OrderItemResultDto> orderItems;
	
	public Long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the orderStatus
	 */
	public Integer getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * @return the paymentStatus
	 */
	public Integer getPaymentStatus() {
		return paymentStatus;
	}
	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	/**
	 * @return the shipStatus
	 */
	public Integer getShipStatus() {
		return shipStatus;
	}
	/**
	 * @param shipStatus the shipStatus to set
	 */
	public void setShipStatus(Integer shipStatus) {
		this.shipStatus = shipStatus;
	}
	/**
	 * @return the groupedStatus
	 */
	public Integer getGroupedStatus() {
		return groupedStatus;
	}
	/**
	 * @param groupedStatus the groupedStatus to set
	 */
	public void setGroupedStatus(Integer groupedStatus) {
		this.groupedStatus = groupedStatus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public List<OrderItemResultDto> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItemResultDto> orderItems) {
		this.orderItems = orderItems;
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
}
