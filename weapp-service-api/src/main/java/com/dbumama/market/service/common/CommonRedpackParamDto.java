package com.dbumama.market.service.common;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CommonRedpackParamDto implements Serializable {

	private Long appId;	
	private String actName;			//活动名称
	private Date startDate;			//开始时间
	private Date endDate;			//结束时间
	private Long replaceAppid;		//红包代发appid
	private Integer redpackType;	//红包类型
	private String redpackMax1; 	//单个红包金额 
	private String redpackMax2; 	//随机红包最大金额
	private String redpackMin; 		//随机红包最小金额
	private Integer redpackCount;	//红包数量 
	private String redpackWishText;	//红包祝福语
	private String redpackMemo;		//红包备注
	private String redpackEndMessage;		//红包发送完毕消息提示
	
	public CommonRedpackParamDto(){}
	
	public CommonRedpackParamDto(Long appId, String actName, Date startDate,
			Date endDate, Long replaceAppid, Integer redpackType,
			String redpackMax1, String redpackMax2, String redpackMin,
			Integer redpackCount, String redpackWishText, String redpackMemo ,String redpackEndMessage) {
		super();
		this.appId = appId;
		this.actName = actName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.replaceAppid = replaceAppid;
		this.redpackType = redpackType;
		this.redpackMax1 = redpackMax1;
		this.redpackMax2 = redpackMax2;
		this.redpackMin = redpackMin;
		this.redpackCount = redpackCount;
		this.redpackWishText = redpackWishText;
		this.redpackMemo = redpackMemo;
		this.redpackEndMessage = redpackEndMessage;
	}
	
	
	public String getRedpackEndMessage() {
		return redpackEndMessage;
	}
	public void setRedpackEndMessage(String redpackEndMessage) {
		this.redpackEndMessage = redpackEndMessage;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getReplaceAppid() {
		return replaceAppid;
	}
	public void setReplaceAppid(Long replaceAppid) {
		this.replaceAppid = replaceAppid;
	}
	public Integer getRedpackType() {
		return redpackType;
	}
	public void setRedpackType(Integer redpackType) {
		this.redpackType = redpackType;
	}
	public String getRedpackMax1() {
		return redpackMax1;
	}
	public void setRedpackMax1(String redpackMax1) {
		this.redpackMax1 = redpackMax1;
	}
	public String getRedpackMax2() {
		return redpackMax2;
	}
	public void setRedpackMax2(String redpackMax2) {
		this.redpackMax2 = redpackMax2;
	}
	public String getRedpackMin() {
		return redpackMin;
	}
	public void setRedpackMin(String redpackMin) {
		this.redpackMin = redpackMin;
	}
	public Integer getRedpackCount() {
		return redpackCount;
	}
	public void setRedpackCount(Integer redpackCount) {
		this.redpackCount = redpackCount;
	}
	public String getRedpackWishText() {
		return redpackWishText;
	}
	public void setRedpackWishText(String redpackWishText) {
		this.redpackWishText = redpackWishText;
	}
	public String getRedpackMemo() {
		return redpackMemo;
	}
	public void setRedpackMemo(String redpackMemo) {
		this.redpackMemo = redpackMemo;
	}
	
	

}
