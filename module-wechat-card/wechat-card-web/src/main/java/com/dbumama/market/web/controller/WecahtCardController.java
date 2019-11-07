package com.dbumama.market.web.controller;

import com.dbumama.market.service.api.CardException;
import com.dbumama.market.service.api.CardListParamDto;
import com.dbumama.market.service.api.WechatCardService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="wechatCard")
public class WecahtCardController extends BaseAppAdminController{
	
	@RPCInject
	private WechatCardService wechatCardService;
	
    public void list(){
    	CardListParamDto cardParamDto = new CardListParamDto(getAuthUserId(), getPageNo());
    	try {
    		rendSuccessJson(wechatCardService.list(cardParamDto,getPara("cardType")));
		} catch (CardException e) {
			rendFailedJson(e.getMessage());
		}
	}
   
}
