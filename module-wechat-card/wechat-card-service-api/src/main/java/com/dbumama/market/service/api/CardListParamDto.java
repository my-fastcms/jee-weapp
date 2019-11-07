package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractPageParamDto;

@SuppressWarnings("serial")
public class CardListParamDto extends AbstractPageParamDto{

	public CardListParamDto(Long authUserId, Integer pageNo) {
		super(authUserId, pageNo);
	}

}
