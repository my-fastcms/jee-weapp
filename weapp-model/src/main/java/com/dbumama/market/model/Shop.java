package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseShop;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_shop", primaryKey = "id")
public class Shop extends BaseShop<Shop> {
	public static final String table = "t_shop";
}
