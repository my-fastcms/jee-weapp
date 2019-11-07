package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class OrderCreateParamDto extends AbstractParamDto{

	private Long buyerId;			//下单者
	private Long receiverId;		//收货地址
	private String memo;			//买家留言
	private String items;			//订单项
	private String formId;			//用于订单模板消息推送
	private Long awardSendId;			//奖品记录发放id
	private Long shopId;			//自提门店地址
	
	public OrderCreateParamDto(Long buyerId, Long authUserId, Long receiverId, String items, String formId) {
		this.buyerId = buyerId;
		this.authUserId = authUserId;
		this.receiverId = receiverId;
		this.items = items;
		this.formId = formId;
	}


	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getAwardSendId() {
		return awardSendId;
	}

	public void setAwardSendId(Long awardSendId) {
		this.awardSendId = awardSendId;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}
	
	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

}
