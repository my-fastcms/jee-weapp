package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseKeywordsReplyNews;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_keywords_reply_news", primaryKey = "id")
public class KeywordsReplyNews extends BaseKeywordsReplyNews<KeywordsReplyNews> {
	public static final String table = "t_keywords_reply_news";
	public static final KeywordsReplyNews dao = new KeywordsReplyNews().dao();
}
