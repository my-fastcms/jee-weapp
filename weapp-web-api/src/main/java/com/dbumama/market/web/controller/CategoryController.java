package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.service.api.ProductCategoryService;
import com.dbumama.market.web.core.controller.BaseApiController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "category")
public class CategoryController extends BaseApiController{

	@RPCInject
	ProductCategoryService productCategoryService;
	
	public void list(){
		List<ProductCategory> categories = productCategoryService.list(getAuthUserId());
		rendSuccessJson(categories);
	}
}
