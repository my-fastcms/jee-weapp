package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseWeimoAppUser;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_weimo_app_user", primaryKey = "id")
public class WeimoAppUser extends BaseWeimoAppUser<WeimoAppUser> {
	public static final String table = "t_weimo_app_user";
}
