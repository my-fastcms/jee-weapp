package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseWeappAudit;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_weapp_audit", primaryKey = "id")
public class WeappAudit extends BaseWeappAudit<WeappAudit> {
	public static final String table = "t_weapp_audit";
}
