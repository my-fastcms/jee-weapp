package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseFullCutSet;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_full_cut_set", primaryKey = "id")
public class FullCutSet extends BaseFullCutSet<FullCutSet> {
	public static final String table = "t_full_cut_set";
	public static final FullCutSet dao = new FullCutSet().dao();
}
