package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseProductCategory<M extends BaseProductCategory<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setAppId(java.lang.Long appId) {
		set("app_id", appId);
		return (M)this;
	}
	
	public java.lang.Long getAppId() {
		return getLong("app_id");
	}

	public M setName(java.lang.String name) {
		set("name", name);
		return (M)this;
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public M setImgPath(java.lang.String imgPath) {
		set("img_path", imgPath);
		return (M)this;
	}
	
	public java.lang.String getImgPath() {
		return getStr("img_path");
	}

	public M setParentId(java.lang.Long parentId) {
		set("parent_id", parentId);
		return (M)this;
	}
	
	public java.lang.Long getParentId() {
		return getLong("parent_id");
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
