package com.dbumama.market.web.controller;

import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.dbumama.market.web.core.interceptor.CSRFInterceptor;
import com.jfinal.aop.Clear;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "area")
@Clear(CSRFInterceptor.class)
public class AreaController extends BaseAdminController{
	@RPCInject
	private AreaService areaService;
	/**
	 * 地区
	 */
	public void list() {
		renderJson(areaService.list(getParaToLong("parentId")));
	}
	
	/**
	 * 地址多选
	 */
	public void areaMore() {
		renderJson(areaService.areaMore());
	}
}
