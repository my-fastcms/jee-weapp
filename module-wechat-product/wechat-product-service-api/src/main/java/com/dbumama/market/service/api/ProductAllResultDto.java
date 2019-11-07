package com.dbumama.market.service.api;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.ProductSpecItem;
import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class ProductAllResultDto extends AbstractResultDto{
	/**库存 */
	private List<ProductSpecItem> stocks=new ArrayList<ProductSpecItem>();
	
	private List<ImagepathResultDto> imageList = new ArrayList<ImagepathResultDto>();

	public List<ImagepathResultDto> getImageList() {
		return imageList;
	}

	public void setImageList(List<ImagepathResultDto> imageList) {
		this.imageList = imageList;
	}

	/** 规格 */
	private List<SpecificationDto> specifications = new ArrayList<SpecificationDto>();

	/** 规格值 */
	private List<SpecificationValueDto> specificationValues = new ArrayList<SpecificationValueDto>();

	public List<ProductSpecItem> getStocks() {
		return stocks;
	}

	public void setStocks(List<ProductSpecItem> stocks) {
		this.stocks = stocks;
	}

	public List<SpecificationDto> getSpecifications() {
		return specifications;
	}

	public void setSpecifications(List<SpecificationDto> specifications) {
		this.specifications = specifications;
	}

	public List<SpecificationValueDto> getSpecificationValues() {
		return specificationValues;
	}

	public void setSpecificationValues(List<SpecificationValueDto> specificationValues) {
		this.specificationValues = specificationValues;
	}
	
}
