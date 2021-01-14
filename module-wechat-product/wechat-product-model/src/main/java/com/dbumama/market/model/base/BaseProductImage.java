package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseProductImage<M extends BaseProductImage<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setProductId(java.lang.Long productId) {
		set("product_id", productId);
		return (M)this;
	}
	
	public java.lang.Long getProductId() {
		return getLong("product_id");
	}

	public M setSource(java.lang.String source) {
		set("source", source);
		return (M)this;
	}
	
	public java.lang.String getSource() {
		return getStr("source");
	}

	public M setLarge(java.lang.String large) {
		set("large", large);
		return (M)this;
	}
	
	public java.lang.String getLarge() {
		return getStr("large");
	}

	public M setMedium(java.lang.String medium) {
		set("medium", medium);
		return (M)this;
	}
	
	public java.lang.String getMedium() {
		return getStr("medium");
	}

	public M setThumbnail(java.lang.String thumbnail) {
		set("thumbnail", thumbnail);
		return (M)this;
	}
	
	public java.lang.String getThumbnail() {
		return getStr("thumbnail");
	}

	public M setOrders(java.lang.Integer orders) {
		set("orders", orders);
		return (M)this;
	}
	
	public java.lang.Integer getOrders() {
		return getInt("orders");
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

	public M setActive(java.lang.Integer active) {
		set("active", active);
		return (M)this;
	}
	
	public java.lang.Integer getActive() {
		return getInt("active");
	}

}