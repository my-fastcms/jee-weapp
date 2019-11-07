package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseShipping<M extends BaseShipping<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setOrderId(java.lang.Long orderId) {
		set("order_id", orderId);
		return (M)this;
	}
	
	public java.lang.Long getOrderId() {
		return getLong("order_id");
	}

	public M setExpKey(java.lang.String expKey) {
		set("exp_key", expKey);
		return (M)this;
	}
	
	public java.lang.String getExpKey() {
		return getStr("exp_key");
	}

	public M setExpName(java.lang.String expName) {
		set("exp_name", expName);
		return (M)this;
	}
	
	public java.lang.String getExpName() {
		return getStr("exp_name");
	}

	public M setBillNumber(java.lang.String billNumber) {
		set("bill_number", billNumber);
		return (M)this;
	}
	
	public java.lang.String getBillNumber() {
		return getStr("bill_number");
	}

	public M setActive(java.lang.Integer active) {
		set("active", active);
		return (M)this;
	}
	
	public java.lang.Integer getActive() {
		return getInt("active");
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

}
