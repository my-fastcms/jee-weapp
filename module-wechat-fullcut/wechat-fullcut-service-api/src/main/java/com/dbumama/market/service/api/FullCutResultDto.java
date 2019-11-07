package com.dbumama.market.service.api;

import java.util.Date;
import java.util.List;

import com.dbumama.market.model.FullCutSet;
import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class FullCutResultDto extends AbstractParamDto {
	private Long id;
	private String name;
	private Date startDate;
	private Date endDate;
	private String productIds;
	private List<FullCutSet> sets;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getProductIds() {
		return productIds;
	}

	public void setProductIds(String productIds) {
		this.productIds = productIds;
	}

	public List<FullCutSet> getSets() {
		return sets;
	}

	public void setSets(List<FullCutSet> sets) {
		this.sets = sets;
	}

}
