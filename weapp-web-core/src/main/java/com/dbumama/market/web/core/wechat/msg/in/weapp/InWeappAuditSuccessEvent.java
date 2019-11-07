package com.dbumama.market.web.core.wechat.msg.in.weapp;

import com.dbumama.market.encrypt.XmlHelper;
import com.dbumama.market.web.core.wechat.msg.in.card.ICardMsgParse;
import com.dbumama.market.web.core.wechat.msg.in.event.EventInMsg;
/**
 * 小程序提交代码版本审核成功事件
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class InWeappAuditSuccessEvent extends EventInMsg implements ICardMsgParse {

	String succTime;
	public String getSuccTime() {
		return succTime;
	}
	public void setSuccTime(String succTime) {
		this.succTime = succTime;
	}
	
	public InWeappAuditSuccessEvent(String toUserName, String fromUserName, Integer createTime, String event) {
		super(toUserName, fromUserName, createTime, event);
	}
	/* (non-Javadoc)
	 * @see com.jfinal.weixin.sdk.msg.in.card.ICardMsgParse#parse(com.jfinal.weixin.sdk.utils.XmlHelper)
	 */
	@Override
	public void parse(XmlHelper xmlHelper) {
		setSuccTime(xmlHelper.getString("//SuccTime"));
	}

}
