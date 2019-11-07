/**
 * 文件名:IndexController.java
 * 版本信息:1.0
 * 日期:2015-5-17
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.controller;

import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.model.Shop;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseMobileController;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-5-17
 */
@RequestMapping(value = "/")
public class IndexController extends BaseMobileController {

	@RPCInject
	private PhoneCodeService phoneCodeService;
	@RPCInject
	private ProductService productService;
	@RPCInject
	private ProductCategoryService productCategoryService;
	@RPCInject
	private ShopService shopService;
	@RPCInject
	private WeiPageService weiPageService;
	@RPCInject
	private ImageSowingService imageSowingService;

	public void index() {
		// 查询卖家创建的活动列表 在有效时间范围内可见的 并且是没有删除的活动 并且是进行中的活动
		Shop shop = shopService.findByApp(getAuthUserId());
		setAttr("shop", shop);

		ProductMobileParamDto mobileParamDto = new ProductMobileParamDto(getAuthUserId(), getPageNo());
		// 查询商品列表
		List<ProductMobileResultDto> indexProducts = productService.getIndexProduct(mobileParamDto);
		setAttr("indexProducts", indexProducts);
		List<ProductMobileResultDto> hotProducts = productService.getHotProduct(mobileParamDto);
		setAttr("hotProducts", hotProducts);
		List<ProductMobileResultDto> newProducts = productService.getNewProduct(mobileParamDto);
		setAttr("newProducts", newProducts);
		List<ProductMobileResultDto> commondProducts = productService.getRecommendProduct(mobileParamDto);
		setAttr("commondProducts", commondProducts);
		
		//首页轮播图
		Long authUserId = getAuthUserId();
		if(authUserId != null){
			Page<ImageSowingResultDto> pageList = imageSowingService.list(authUserId, 1, 10);
			setAttr("shopSowingImg", pageList.getList());
		}
		/*WeiPage entity = weiPageService.findIndex(getSellerId());
		if (entity != null) {
			setAttr("entity", entity);
			setAttr("url", "feature/show?id=" + entity.getId());
			render("/feature/index.html");
		}*/
	}

	@Clear
	public void forbid() {
	}

	@Clear
	public void error() {
	}

	@Clear
	public void auth() {
		setAttr("rUrl", getPara("rUrl"));
	}

	public void search() {
		List<ProductCategory> productCategorys = productCategoryService.list(getAuthUserId());
		/*
		 * List<ProductCategory> validCategorys = new ArrayList<ProductCategory>();
		 * for(ProductCategory pcate : productCategorys){ List<Product> products =
		 * productService.find("select * from " + Product.table +
		 * " where product_category_id=? ", pcate.getId()); if(products != null &&
		 * products.size()>0){ validCategorys.add(pcate); } }
		 */
		setAttr("productCategory", productCategorys);
		render("/search/search.html");
	}

	@Clear
	public void captcha() {
		renderCaptcha();
	}
}
