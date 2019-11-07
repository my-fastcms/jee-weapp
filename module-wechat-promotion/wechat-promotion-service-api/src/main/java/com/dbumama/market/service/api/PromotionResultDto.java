package com.dbumama.market.service.api;

import java.util.Date;
import java.util.List;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class PromotionResultDto extends AbstractParamDto{
	
	private Long promotionId;
	private String promotionName;
	private Date startDate;
	private Date endDate;
	private String promotionTag;


	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	private List<PromotionSetResultDto> promotionSets;

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
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

	public String getPromotionTag() {
		return promotionTag;
	}

	public void setPromotionTag(String promotionTag) {
		this.promotionTag = promotionTag;
	}

	public List<PromotionSetResultDto> getPromotionSets() {
		return promotionSets;
	}

	public void setPromotionSets(List<PromotionSetResultDto> promotionSets) {
		this.promotionSets = promotionSets;
	}

}
