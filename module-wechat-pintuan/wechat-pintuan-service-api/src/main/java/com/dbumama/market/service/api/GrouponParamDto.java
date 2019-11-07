package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractPageParamDto;
@SuppressWarnings("serial")
public class GrouponParamDto extends AbstractPageParamDto {
	
	public GrouponParamDto(Long appId, Integer pageNo, Integer pageSize, Integer status, Integer active) {
		super(appId, pageNo, pageSize);
		this.active = active;
		this.status = status;
	}

}
	