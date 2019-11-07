package com.dbumama.market.service.api;

import java.util.Date;

/**
 * @author wangjun
 * 2018年5月8日
 */
@SuppressWarnings("serial")
public class OrderTuanResultDto extends OrderResultDto{
	private Long groupId;
	private Date groupCreated;							//拼团创建时间
	private String groupInfo;                 			//拼团信息
	private String groupStatus;               			//组团状态
	private String multiGroupName;
	private String groupHeader;
	private String diffCount;							//组团中，还差几人成团
	
	/**
	 * @return the diffCount
	 */
	public String getDiffCount() {
		return diffCount;
	}
	/**
	 * @param diffCount the diffCount to set
	 */
	public void setDiffCount(String diffCount) {
		this.diffCount = diffCount;
	}
	/**
	 * @return the groupHeader
	 */
	public String getGroupHeader() {
		return groupHeader;
	}
	/**
	 * @param groupHeader the groupHeader to set
	 */
	public void setGroupHeader(String groupHeader) {
		this.groupHeader = groupHeader;
	}
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
	/**
	 * @return the groupCreated
	 */
	public Date getGroupCreated() {
		return groupCreated;
	}
	/**
	 * @param groupCreated the groupCreated to set
	 */
	public void setGroupCreated(Date groupCreated) {
		this.groupCreated = groupCreated;
	}
	/**
	 * @return the groupInfo
	 */
	public String getGroupInfo() {
		return groupInfo;
	}
	/**
	 * @param groupInfo the groupInfo to set
	 */
	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}
	/**
	 * @return the groupStatus
	 */
	public String getGroupStatus() {
		return groupStatus;
	}
	/**
	 * @param groupStatus the groupStatus to set
	 */
	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}
	/**
	 * @return the multiGroupName
	 */
	public String getMultiGroupName() {
		return multiGroupName;
	}
	/**
	 * @param multiGroupName the multiGroupName to set
	 */
	public void setMultiGroupName(String multiGroupName) {
		this.multiGroupName = multiGroupName;
	}
}
