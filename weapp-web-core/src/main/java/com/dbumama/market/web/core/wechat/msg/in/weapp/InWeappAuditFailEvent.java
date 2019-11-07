package com.dbumama.market.web.core.wechat.msg.in.weapp;

import com.dbumama.market.encrypt.XmlHelper;
import com.dbumama.market.web.core.wechat.msg.in.card.ICardMsgParse;
import com.dbumama.market.web.core.wechat.msg.in.event.EventInMsg;

@SuppressWarnings("serial")
public class InWeappAuditFailEvent extends EventInMsg implements ICardMsgParse {

	String reason;
	String failTime;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getFailTime() {
		return failTime;
	}
	public void setFailTime(String failTime) {
		this.failTime = failTime;
	}

	public InWeappAuditFailEvent(String toUserName, String fromUserName, Integer createTime, String event) {
		super(toUserName, fromUserName, createTime, event);
	}
	/* (non-Javadoc)
	 * @see com.jfinal.weixin.sdk.msg.in.card.ICardMsgParse#parse(com.jfinal.weixin.sdk.utils.XmlHelper)
	 */
	@Override
	public void parse(XmlHelper xmlHelper) {
		setReason(xmlHelper.getString("//Reason"));
		setFailTime(xmlHelper.getString("//FailTime"));
	}

}
