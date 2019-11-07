package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class CardActiveParamDto extends AbstractParamDto{

	private Long buyerId;
	private Long cardId;
	private String phone;
	private String phoneCode;		//短信验证码
	private String code;			//系统验证码
	private String codeInSession;	//session中系统验证码
	
	public CardActiveParamDto(Long buyerId, Long authUserId, Long cardId, String phone, String phoneCode, String code, String codeInSession) {
		super();
		this.buyerId = buyerId;
		this.authUserId = authUserId;
		this.cardId = cardId;
		this.phone = phone;
		this.phoneCode = phoneCode;
		this.code = code;
		this.codeInSession = codeInSession;
	}
	
	public Long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoneCode() {
		return phoneCode;
	}
	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeInSession() {
		return codeInSession;
	}
	public void setCodeInSession(String codeInSession) {
		this.codeInSession = codeInSession;
	}
	
}
