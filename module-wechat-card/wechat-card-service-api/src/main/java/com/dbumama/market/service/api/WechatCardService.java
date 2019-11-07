package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;

public interface WechatCardService {

	Page<CardResultDto> list(CardListParamDto cardParamDto, String cardType);

}
