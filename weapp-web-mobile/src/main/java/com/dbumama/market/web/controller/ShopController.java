package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.Shop;
import com.dbumama.market.service.api.ShopService;
import com.dbumama.market.web.core.controller.BaseMobileController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "shop")
public class ShopController extends BaseMobileController{
	@RPCInject
	private ShopService shopService;
	
	public void index(){
		List<Shop> list = shopService.getShopByAppId(getAuthUserId());
		setAttr("shopList", list);
		render("/shop/index.html");
	}
	
}
