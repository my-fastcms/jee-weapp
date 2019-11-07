package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;
@SuppressWarnings("serial")
public class ProdGroupResultDto extends AbstractResultDto {
	private Long multiGroupId;     	//拼团活动id 
    private String groupNum;  		//几人团
    private String collagePrice; 	//拼团价格
    private Integer quota;			//限购件数
	private String groupTime;
	public Long getMultiGroupId() {
		return multiGroupId;
	}
	public void setMultiGroupId(Long multiGroupId) {
		this.multiGroupId = multiGroupId;
	}
	public String getGroupNum() {
		return groupNum;
	}
	public void setGroupNum(String groupNum) {
		this.groupNum = groupNum;
	}
	public String getCollagePrice() {
		return collagePrice;
	}
	public void setCollagePrice(String collagePrice) {
		this.collagePrice = collagePrice;
	}
	public Integer getQuota() {
		return quota;
	}
	public void setQuota(Integer quota) {
		this.quota = quota;
	}
	public String getGroupTime() {
		return groupTime;
	}
	public void setGroupTime(String groupTime) {
		this.groupTime = groupTime;
	}
     
}
