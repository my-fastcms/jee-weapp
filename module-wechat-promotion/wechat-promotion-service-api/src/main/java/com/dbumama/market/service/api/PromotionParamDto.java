package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractPageParamDto;

@SuppressWarnings("serial")
public class PromotionParamDto extends AbstractPageParamDto{

	public PromotionParamDto(Long appId, Integer pageNo, Integer pageSize, Integer status, Integer active) {
		super(appId, pageNo, pageSize);
		this.status = status;
		this.active = active;
	}

}
