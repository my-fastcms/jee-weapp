package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.model.MultiGroup;
import com.dbumama.market.service.common.AbstractResultDto;
@SuppressWarnings("serial")
public class GrouponResultDto extends AbstractResultDto{
	private MultiGroup multiGroup;
	private List<ProductResultDto> productDto;
	public MultiGroup getMultiGroup() {
		return multiGroup;
	}
	public void setMultiGroup(MultiGroup multiGroup) {
		this.multiGroup = multiGroup;
	}
	public List<ProductResultDto> getProductDto() {
		return productDto;
	}
	public void setProductDto(List<ProductResultDto> productDto) {
		this.productDto = productDto;
	}
    
}
