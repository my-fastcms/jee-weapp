package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BasePromotion;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_promotion", primaryKey = "id")
public class Promotion extends BasePromotion<Promotion> {
	public static final String table = "t_promotion";
}