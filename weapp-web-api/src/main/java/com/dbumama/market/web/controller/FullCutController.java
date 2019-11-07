package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.service.api.FullCutService;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "fullcut")
public class FullCutController extends BaseApiController {

	@RPCInject
	private FullCutService fullCutService;
	
	public void list(){
		List<Record> productResultDtos = fullCutService.getFullCutMini(getAuthUserId(), getPageNo(), getPageSize());
		rendSuccessJson(productResultDtos);
	}
}
