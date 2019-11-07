package com.dbumama.market.web.controller;

import com.dbumama.market.service.api.MenuService;
import com.dbumama.market.web.core.controller.BaseAdminController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="menu")
public class MenuController extends BaseAdminController {
	
	@RPCInject
	private MenuService menuService;
	
	public void list(){
		Long id = getParaToLong("id");
		if(id == null){
			rendJson(menuService.getJsTreeMenus(getParaToLong("roleId"), false));	
		}else{
			rendJson(menuService.getJsTreeChildren(id, getParaToLong("roleId"), false));
		}
	}
	
	public void listread(){
		Long id = getParaToLong("id");
		if(id == null){
			rendJson(menuService.getJsTreeMenus(getParaToLong("roleId"), true));	
		}else{
			rendJson(menuService.getJsTreeChildren(id, getParaToLong("roleId"), true));
		}
	}
	
}
