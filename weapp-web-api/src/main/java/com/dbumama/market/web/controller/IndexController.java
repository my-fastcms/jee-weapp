package com.dbumama.market.web.controller;

import com.dbumama.market.model.Shop;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.render.Base64CaptchaRender;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/")
public class IndexController extends BaseApiController {

	@RPCInject
	private ProductService productService;
	@RPCInject
	private ShopService shopService;
	@RPCInject
	private PhoneCodeService phoneCodeService;
	@RPCInject
	ProductCategoryService productCategoryService;
	@RPCInject
	WeappStyleService weappStyleService;
	@RPCInject
	private ImageSowingService imageSowingService;
	
	public void index(){
		Map<String, Object> result = new HashMap<String, Object>();
		Shop shop = shopService.findByApp(getAuthUserId());
		if(shop != null && StrKit.notBlank(shop.getShopSign())){
	     	shop.setShopSign(this.getImageDomain()+shop.getShopSign());
		}
		if(shop != null && StrKit.notBlank(shop.getShopLogo())){
	     	shop.setShopLogo(this.getImageDomain()+shop.getShopLogo());
		}
		
		result.put("shop", shop);
		
		ProductMobileParamDto mobileParamDto = new ProductMobileParamDto(getAuthUserId(), getPageNo());
		//查询商品列表
//		List<ProductMobileResultDto> indexProducts = productService.getIndexProduct(mobileParamDto);
//		result.put("indexProducts", indexProducts);
//		List<ProductMobileResultDto> hotProducts = productService.getHotProduct(mobileParamDto);
//		result.put("hotProducts", hotProducts);
		List<ProductMobileResultDto> newProducts = productService.getNewProduct(mobileParamDto);
		result.put("newProducts", newProducts);
//		List<ProductMobileResultDto> commondProducts = productService.getRecommendProduct(mobileParamDto);
//		result.put("commondProducts", commondProducts);
		
		result.put("authUser", getAuthUser());
		result.put("categories", productCategoryService.list(getAuthUserId()));
		rendSuccessJson(result);
	}
	
	public void getStyle(){
		rendSuccessJson(weappStyleService.getAppStyle(getAuthUserId()));
	}
	
	@Clear
	public void captcha() {
		render(new Base64CaptchaRender());
	}
	
	/**轮播图*/
	public void sowingimage(){
		try {
			Page<ImageSowingResultDto> pageList = imageSowingService.list(getAuthUserId(), getPageNo(), getPageSize());
			rendSuccessJson(pageList.getList());
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
}
