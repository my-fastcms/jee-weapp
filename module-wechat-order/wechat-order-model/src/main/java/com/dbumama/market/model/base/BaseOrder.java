package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseOrder<M extends BaseOrder<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setBuyerId(java.lang.Long buyerId) {
		set("buyer_id", buyerId);
		return (M)this;
	}
	
	public java.lang.Long getBuyerId() {
		return getLong("buyer_id");
	}

	public M setAppId(java.lang.Long appId) {
		set("app_id", appId);
		return (M)this;
	}
	
	public java.lang.Long getAppId() {
		return getLong("app_id");
	}

	public M setGroupId(java.lang.Long groupId) {
		set("group_id", groupId);
		return (M)this;
	}
	
	public java.lang.Long getGroupId() {
		return getLong("group_id");
	}

	public M setFormId(java.lang.String formId) {
		set("form_id", formId);
		return (M)this;
	}
	
	public java.lang.String getFormId() {
		return getStr("form_id");
	}

	public M setPrepayId(java.lang.String prepayId) {
		set("prepay_id", prepayId);
		return (M)this;
	}
	
	public java.lang.String getPrepayId() {
		return getStr("prepay_id");
	}

	public M setOrderType(java.lang.Integer orderType) {
		set("order_type", orderType);
		return (M)this;
	}
	
	public java.lang.Integer getOrderType() {
		return getInt("order_type");
	}

	public M setOrderSn(java.lang.String orderSn) {
		set("order_sn", orderSn);
		return (M)this;
	}
	
	public java.lang.String getOrderSn() {
		return getStr("order_sn");
	}

	public M setOrderStatus(java.lang.Integer orderStatus) {
		set("order_status", orderStatus);
		return (M)this;
	}
	
	public java.lang.Integer getOrderStatus() {
		return getInt("order_status");
	}

	public M setPaymentStatus(java.lang.Integer paymentStatus) {
		set("payment_status", paymentStatus);
		return (M)this;
	}
	
	public java.lang.Integer getPaymentStatus() {
		return getInt("payment_status");
	}

	public M setShippingStatus(java.lang.Integer shippingStatus) {
		set("shipping_status", shippingStatus);
		return (M)this;
	}
	
	public java.lang.Integer getShippingStatus() {
		return getInt("shipping_status");
	}

	public M setGroupStatus(java.lang.Integer groupStatus) {
		set("group_status", groupStatus);
		return (M)this;
	}
	
	public java.lang.Integer getGroupStatus() {
		return getInt("group_status");
	}

	public M setReceiverId(java.lang.Long receiverId) {
		set("receiver_id", receiverId);
		return (M)this;
	}
	
	public java.lang.Long getReceiverId() {
		return getLong("receiver_id");
	}

	public M setShopId(java.lang.Long shopId) {
		set("shop_id", shopId);
		return (M)this;
	}
	
	public java.lang.Long getShopId() {
		return getLong("shop_id");
	}

	public M setTotalPrice(java.math.BigDecimal totalPrice) {
		set("total_price", totalPrice);
		return (M)this;
	}
	
	public java.math.BigDecimal getTotalPrice() {
		return get("total_price");
	}

	public M setPostFee(java.math.BigDecimal postFee) {
		set("post_fee", postFee);
		return (M)this;
	}
	
	public java.math.BigDecimal getPostFee() {
		return get("post_fee");
	}

	public M setPayFee(java.math.BigDecimal payFee) {
		set("pay_fee", payFee);
		return (M)this;
	}
	
	public java.math.BigDecimal getPayFee() {
		return get("pay_fee");
	}

	public M setPoint(java.lang.Long point) {
		set("point", point);
		return (M)this;
	}
	
	public java.lang.Long getPoint() {
		return getLong("point");
	}

	public M setMemo(java.lang.String memo) {
		set("memo", memo);
		return (M)this;
	}
	
	public java.lang.String getMemo() {
		return getStr("memo");
	}

	public M setTradeNo(java.lang.String tradeNo) {
		set("trade_no", tradeNo);
		return (M)this;
	}
	
	public java.lang.String getTradeNo() {
		return getStr("trade_no");
	}

	public M setTransactionId(java.lang.String transactionId) {
		set("transaction_id", transactionId);
		return (M)this;
	}
	
	public java.lang.String getTransactionId() {
		return getStr("transaction_id");
	}

	public M setCreated(java.util.Date created) {
		set("created", created);
		return (M)this;
	}
	
	public java.util.Date getCreated() {
		return get("created");
	}

	public M setUpdated(java.util.Date updated) {
		set("updated", updated);
		return (M)this;
	}
	
	public java.util.Date getUpdated() {
		return get("updated");
	}

	public M setActive(java.lang.Boolean active) {
		set("active", active);
		return (M)this;
	}
	
	public java.lang.Boolean getActive() {
		return get("active");
	}

}
