package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseMenuReplyRule<M extends BaseMenuReplyRule<M>> extends WxmModel<M> implements IBean {

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

	public M setMenuKey(java.lang.String menuKey) {
		set("menu_key", menuKey);
		return (M)this;
	}
	
	public java.lang.String getMenuKey() {
		return getStr("menu_key");
	}

	public M setRuleType(java.lang.Integer ruleType) {
		set("rule_type", ruleType);
		return (M)this;
	}
	
	public java.lang.Integer getRuleType() {
		return getInt("rule_type");
	}

	public M setExpiresIn(java.lang.Integer expiresIn) {
		set("expires_in", expiresIn);
		return (M)this;
	}
	
	public java.lang.Integer getExpiresIn() {
		return getInt("expires_in");
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
