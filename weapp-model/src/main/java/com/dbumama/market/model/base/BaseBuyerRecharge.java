package com.dbumama.market.model.base;

import com.dbumama.market.model.gen.WxmModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseBuyerRecharge<M extends BaseBuyerRecharge<M>> extends WxmModel<M> implements IBean {

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

	public M setMemberRankId(java.lang.Long memberRankId) {
		set("member_rank_id", memberRankId);
		return (M)this;
	}
	
	public java.lang.Long getMemberRankId() {
		return getLong("member_rank_id");
	}

	public M setCardId(java.lang.String cardId) {
		set("card_id", cardId);
		return (M)this;
	}
	
	public java.lang.String getCardId() {
		return getStr("card_id");
	}

	public M setRecharge(java.math.BigDecimal recharge) {
		set("recharge", recharge);
		return (M)this;
	}
	
	public java.math.BigDecimal getRecharge() {
		return get("recharge");
	}

	public M setOutTradeId(java.lang.String outTradeId) {
		set("out_trade_id", outTradeId);
		return (M)this;
	}
	
	public java.lang.String getOutTradeId() {
		return getStr("out_trade_id");
	}

	public M setTransactionId(java.lang.String transactionId) {
		set("transaction_id", transactionId);
		return (M)this;
	}
	
	public java.lang.String getTransactionId() {
		return getStr("transaction_id");
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
