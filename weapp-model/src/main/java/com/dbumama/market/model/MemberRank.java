package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseMemberRank;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_member_rank", primaryKey = "id")
public class MemberRank extends BaseMemberRank<MemberRank> {
	public static final String table = "t_member_rank";
}
