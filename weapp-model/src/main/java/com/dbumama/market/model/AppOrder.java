package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseAppOrder;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_app_order", primaryKey = "id")
public class AppOrder extends BaseAppOrder<AppOrder> {
	public static final String table = "t_app_order";
}
