package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.service.api.ProductCategoryService;
import com.dbumama.market.web.core.controller.BaseMobileController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="category", viewPath="category")
public class CategoryController extends BaseMobileController{

	@RPCInject
	ProductCategoryService productCategoryService;
	
	public void index(){
		List<ProductCategory> categories = productCategoryService.list(getAuthUser().getId());
		System.out.println(categories);
		setAttr("categories", categories);
		render("index.html");
	}
	
}
