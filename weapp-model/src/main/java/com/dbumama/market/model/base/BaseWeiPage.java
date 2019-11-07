package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseWeiPage<M extends BaseWeiPage<M>> extends WxmModel<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setSellerId(java.lang.Long sellerId) {
		set("seller_id", sellerId);
		return (M)this;
	}
	
	public java.lang.Long getSellerId() {
		return getLong("seller_id");
	}

	public M setTitle(java.lang.String title) {
		set("title", title);
		return (M)this;
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public M setContent(java.lang.String content) {
		set("content", content);
		return (M)this;
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public M setPublished(java.util.Date published) {
		set("published", published);
		return (M)this;
	}
	
	public java.util.Date getPublished() {
		return get("published");
	}

	public M setCategoryId(java.lang.Long categoryId) {
		set("category_id", categoryId);
		return (M)this;
	}
	
	public java.lang.Long getCategoryId() {
		return getLong("category_id");
	}

	public M setProducts(java.lang.String products) {
		set("products", products);
		return (M)this;
	}
	
	public java.lang.String getProducts() {
		return getStr("products");
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
