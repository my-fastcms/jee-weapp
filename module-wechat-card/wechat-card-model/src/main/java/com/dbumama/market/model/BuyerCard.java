package com.dbumama.market.model;

import com.dbumama.market.model.base.BaseBuyerCard;
import io.jboot.db.annotation.Table;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_buyer_card", primaryKey = "id")
public class BuyerCard extends BaseBuyerCard<BuyerCard> {
	public static final String table = "t_buyer_card";
	public static final BuyerCard dao = new BuyerCard().dao();
}
