package com.dbumama.market.service.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class SpecificationsResultDto extends AbstractResultDto {
	private Long id;
	private String name;
	private Date created;
	private Integer active;
	/**
	 * @return the active
	 */
	public Integer getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Integer active) {
		this.active = active;
	}

	/** 规格值 */
	private List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();

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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<SpecificationValue> getSpecificationValues() {
		return specificationValues;
	}

	public void setSpecificationValues(List<SpecificationValue> specificationValues) {
		this.specificationValues = specificationValues;
	}

}
