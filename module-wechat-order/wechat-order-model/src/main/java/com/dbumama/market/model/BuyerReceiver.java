package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseBuyerReceiver;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_buyer_receiver", primaryKey = "id")
public class BuyerReceiver extends BaseBuyerReceiver<BuyerReceiver> {
	public static final String table = "t_buyer_receiver";
}