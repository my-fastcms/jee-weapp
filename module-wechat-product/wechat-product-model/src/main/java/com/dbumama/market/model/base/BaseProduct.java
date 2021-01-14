package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseProduct<M extends BaseProduct<M>> extends WxmModel<M> implements IBean {

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

	public M setSn(java.lang.String sn) {
		set("sn", sn);
		return (M)this;
	}
	
	public java.lang.String getSn() {
		return getStr("sn");
	}

	public M setName(java.lang.String name) {
		set("name", name);
		return (M)this;
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public M setPrice(java.lang.String price) {
		set("price", price);
		return (M)this;
	}
	
	public java.lang.String getPrice() {
		return getStr("price");
	}

	public M setMarketPrice(java.math.BigDecimal marketPrice) {
		set("market_price", marketPrice);
		return (M)this;
	}
	
	public java.math.BigDecimal getMarketPrice() {
		return get("market_price");
	}

	public M setImage(java.lang.String image) {
		set("image", image);
		return (M)this;
	}
	
	public java.lang.String getImage() {
		return getStr("image");
	}

	public M setStock(java.lang.Integer stock) {
		set("stock", stock);
		return (M)this;
	}
	
	public java.lang.Integer getStock() {
		return getInt("stock");
	}

	public M setDeliveryWeight(java.math.BigDecimal deliveryWeight) {
		set("delivery_weight", deliveryWeight);
		return (M)this;
	}
	
	public java.math.BigDecimal getDeliveryWeight() {
		return get("delivery_weight");
	}

	public M setIsMarketable(java.lang.Boolean isMarketable) {
		set("is_marketable", isMarketable);
		return (M)this;
	}
	
	public java.lang.Boolean getIsMarketable() {
		return get("is_marketable");
	}

	public M setIsPickUp(java.lang.Boolean isPickUp) {
		set("is_pick_up", isPickUp);
		return (M)this;
	}
	
	public java.lang.Boolean getIsPickUp() {
		return get("is_pick_up");
	}

	public M setIsVirtualGoods(java.lang.Boolean isVirtualGoods) {
		set("is_virtual_goods", isVirtualGoods);
		return (M)this;
	}
	
	public java.lang.Boolean getIsVirtualGoods() {
		return get("is_virtual_goods");
	}

	public M setIsPurchaseLimitation(java.lang.Boolean isPurchaseLimitation) {
		set("is_purchase_limitation", isPurchaseLimitation);
		return (M)this;
	}
	
	public java.lang.Boolean getIsPurchaseLimitation() {
		return get("is_purchase_limitation");
	}

	public M setIsCityDis(java.lang.Boolean isCityDis) {
		set("is_city_dis", isCityDis);
		return (M)this;
	}
	
	public java.lang.Boolean getIsCityDis() {
		return get("is_city_dis");
	}

	public M setPurchaseCount(java.lang.Integer purchaseCount) {
		set("purchase_count", purchaseCount);
		return (M)this;
	}
	
	public java.lang.Integer getPurchaseCount() {
		return getInt("purchase_count");
	}

	public M setIsUnifiedSpec(java.lang.Boolean isUnifiedSpec) {
		set("is_unified_spec", isUnifiedSpec);
		return (M)this;
	}
	
	public java.lang.Boolean getIsUnifiedSpec() {
		return get("is_unified_spec");
	}

	public M setIsList(java.lang.Boolean isList) {
		set("is_list", isList);
		return (M)this;
	}
	
	public java.lang.Boolean getIsList() {
		return get("is_list");
	}

	public M setIntroduction(java.lang.String introduction) {
		set("introduction", introduction);
		return (M)this;
	}
	
	public java.lang.String getIntroduction() {
		return getStr("introduction");
	}

	public M setSales(java.lang.Long sales) {
		set("sales", sales);
		return (M)this;
	}
	
	public java.lang.Long getSales() {
		return getLong("sales");
	}

	public M setProductCategoryId(java.lang.Long productCategoryId) {
		set("product_category_id", productCategoryId);
		return (M)this;
	}
	
	public java.lang.Long getProductCategoryId() {
		return getLong("product_category_id");
	}

	public M setDeliveryType(java.lang.Integer deliveryType) {
		set("delivery_type", deliveryType);
		return (M)this;
	}
	
	public java.lang.Integer getDeliveryType() {
		return getInt("delivery_type");
	}

	public M setDeliveryFees(java.math.BigDecimal deliveryFees) {
		set("delivery_fees", deliveryFees);
		return (M)this;
	}
	
	public java.math.BigDecimal getDeliveryFees() {
		return get("delivery_fees");
	}

	public M setDeliveryTemplateId(java.lang.Long deliveryTemplateId) {
		set("delivery_template_id", deliveryTemplateId);
		return (M)this;
	}
	
	public java.lang.Long getDeliveryTemplateId() {
		return getLong("delivery_template_id");
	}

	public M setShowDeliveryTime(java.lang.Boolean showDeliveryTime) {
		set("show_delivery_time", showDeliveryTime);
		return (M)this;
	}
	
	public java.lang.Boolean getShowDeliveryTime() {
		return get("show_delivery_time");
	}

	public M setShowCompensate(java.lang.Boolean showCompensate) {
		set("show_compensate", showCompensate);
		return (M)this;
	}
	
	public java.lang.Boolean getShowCompensate() {
		return get("show_compensate");
	}

	public M setShowSafeguard(java.lang.Boolean showSafeguard) {
		set("show_safeguard", showSafeguard);
		return (M)this;
	}
	
	public java.lang.Boolean getShowSafeguard() {
		return get("show_safeguard");
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