package com.dbumama.market.service.api;

import java.util.Date;

import com.dbumama.market.service.common.AbstractPageParamDto;

@SuppressWarnings("serial")
public class CustomerParamDto extends AbstractPageParamDto{

	private String nameOrOpenId; //名称或openId
	private String tagsBasic;	//标签条件
	private String tagidList;	//标签ID列表
	private String sSceneBasic; //用户关注的渠道来源条件
	private String subscribeScene; //用户关注的渠道来源
	private Integer active;		//关注取关状态
	private Integer sex;	//性别
	private String followBasic;	//关注的条件
	private Date followStartDate;	//	关注的开始时间
	private Date followEndDate;	//	关注的结束时间
	private String cancelBasic;	//取关的条件
	private Date cancelStartDate;	//	关注的开始时间
	private Date cancelEndDate;	//	关注的结束时间
	
	public CustomerParamDto(Long appId, Integer pageNo) {
		super(appId, pageNo);
	}
	public String getNameOrOpenId() {
		return nameOrOpenId;
	}
	public void setNameOrOpenId(String nameOrOpenId) {
		this.nameOrOpenId = nameOrOpenId;
	}
	public String getTagsBasic() {
		return tagsBasic;
	}
	public void setTagsBasic(String tagsBasic) {
		this.tagsBasic = tagsBasic;
	}
	public String getTagidList() {
		return tagidList;
	}
	public void setTagidList(String tagidList) {
		this.tagidList = tagidList;
	}
	public String getsSceneBasic() {
		return sSceneBasic;
	}
	public void setsSceneBasic(String sSceneBasic) {
		this.sSceneBasic = sSceneBasic;
	}
	public String getSubscribeScene() {
		return subscribeScene;
	}
	public void setSubscribeScene(String subscribeScene) {
		this.subscribeScene = subscribeScene;
	}
	public Integer getActive() {
		return active;
	}
	public void setActive(Integer active) {
		this.active = active;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getFollowBasic() {
		return followBasic;
	}
	public void setFollowBasic(String followBasic) {
		this.followBasic = followBasic;
	}
	public Date getFollowStartDate() {
		return followStartDate;
	}
	public void setFollowStartDate(Date followStartDate) {
		this.followStartDate = followStartDate;
	}
	public Date getFollowEndDate() {
		return followEndDate;
	}
	public void setFollowEndDate(Date followEndDate) {
		this.followEndDate = followEndDate;
	}
	public String getCancelBasic() {
		return cancelBasic;
	}
	public void setCancelBasic(String cancelBasic) {
		this.cancelBasic = cancelBasic;
	}
	public Date getCancelStartDate() {
		return cancelStartDate;
	}
	public void setCancelStartDate(Date cancelStartDate) {
		this.cancelStartDate = cancelStartDate;
	}
	public Date getCancelEndDate() {
		return cancelEndDate;
	}
	public void setCancelEndDate(Date cancelEndDate) {
		this.cancelEndDate = cancelEndDate;
	}
	
}
