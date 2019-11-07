package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class SpecificationResultDto extends AbstractResultDto{

	private SpecificationDto specification;
	private List<SpecificationValueDto> specificationValues;
	public SpecificationDto getSpecification() {
		return specification;
	}
	public void setSpecification(SpecificationDto specification) {
		this.specification = specification;
	}
	public List<SpecificationValueDto> getSpecificationValues() {
		return specificationValues;
	}
	public void setSpecificationValues(List<SpecificationValueDto> specificationValues) {
		this.specificationValues = specificationValues;
	}
	
}
