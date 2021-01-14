package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseApp<M extends BaseApp<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setAppName(java.lang.String appName) {
		set("app_name", appName);
		return (M)this;
	}
	
	public java.lang.String getAppName() {
		return getStr("app_name");
	}

	public M setAppDesc(java.lang.String appDesc) {
		set("app_desc", appDesc);
		return (M)this;
	}
	
	public java.lang.String getAppDesc() {
		return getStr("app_desc");
	}

	public M setAppImage(java.lang.String appImage) {
		set("app_image", appImage);
		return (M)this;
	}
	
	public java.lang.String getAppImage() {
		return getStr("app_image");
	}

	public M setAppIndexPage(java.lang.String appIndexPage) {
		set("app_index_page", appIndexPage);
		return (M)this;
	}
	
	public java.lang.String getAppIndexPage() {
		return getStr("app_index_page");
	}

	public M setAppMenuId(java.lang.Long appMenuId) {
		set("app_menu_id", appMenuId);
		return (M)this;
	}
	
	public java.lang.Long getAppMenuId() {
		return getLong("app_menu_id");
	}

	public M setAppSecMenuId(java.lang.Long appSecMenuId) {
		set("app_sec_menu_id", appSecMenuId);
		return (M)this;
	}
	
	public java.lang.Long getAppSecMenuId() {
		return getLong("app_sec_menu_id");
	}

	public M setAppType(java.lang.Integer appType) {
		set("app_type", appType);
		return (M)this;
	}
	
	public java.lang.Integer getAppType() {
		return getInt("app_type");
	}

	public M setAppCategory(java.lang.Integer appCategory) {
		set("app_category", appCategory);
		return (M)this;
	}
	
	public java.lang.Integer getAppCategory() {
		return getInt("app_category");
	}

	public M setAppIcon(java.lang.String appIcon) {
		set("app_icon", appIcon);
		return (M)this;
	}
	
	public java.lang.String getAppIcon() {
		return getStr("app_icon");
	}

	public M setAppContent(java.lang.String appContent) {
		set("app_content", appContent);
		return (M)this;
	}
	
	public java.lang.String getAppContent() {
		return getStr("app_content");
	}

	public M setAppShowImages(java.lang.String appShowImages) {
		set("app_show_images", appShowImages);
		return (M)this;
	}
	
	public java.lang.String getAppShowImages() {
		return getStr("app_show_images");
	}

	public M setIsfree(java.lang.Boolean isfree) {
		set("isfree", isfree);
		return (M)this;
	}
	
	public java.lang.Boolean getIsfree() {
		return get("isfree");
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