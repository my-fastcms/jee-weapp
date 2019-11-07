package com.dbumama.market.service.api;

@SuppressWarnings("serial")
public class OrderJoinParamDto extends OrderCreateParamDto{

	//组团发起者
	private Long groupId;

	/**
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public OrderJoinParamDto(Long groupUserId, Long buyerId, Long authUserId, Long receiverId, String items, String formId) {
		super(buyerId, authUserId, receiverId, items, formId);
		this.groupId = groupUserId;
	}

}
