package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractPageParamDto;
@SuppressWarnings("serial")
public class MatterListParamDto extends AbstractPageParamDto{
	public MatterListParamDto(Long authUserId, Integer pageNo) {
		super(authUserId, pageNo);
	}

}
