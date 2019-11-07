package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseMarketcodeCodeactiveRcd<M extends BaseMarketcodeCodeactiveRcd<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setCodeactiveId(java.lang.Long codeactiveId) {
		set("codeactive_id", codeactiveId);
		return (M)this;
	}
	
	public java.lang.Long getCodeactiveId() {
		return getLong("codeactive_id");
	}

	public M setApplicationId(java.lang.String applicationId) {
		set("application_id", applicationId);
		return (M)this;
	}
	
	public java.lang.String getApplicationId() {
		return getStr("application_id");
	}

	public M setCodeStart(java.lang.Integer codeStart) {
		set("code_start", codeStart);
		return (M)this;
	}
	
	public java.lang.Integer getCodeStart() {
		return getInt("code_start");
	}

	public M setCodeEnd(java.lang.Integer codeEnd) {
		set("code_end", codeEnd);
		return (M)this;
	}
	
	public java.lang.Integer getCodeEnd() {
		return getInt("code_end");
	}

	public M setActive(java.lang.Boolean active) {
		set("active", active);
		return (M)this;
	}
	
	public java.lang.Boolean getActive() {
		return get("active");
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
