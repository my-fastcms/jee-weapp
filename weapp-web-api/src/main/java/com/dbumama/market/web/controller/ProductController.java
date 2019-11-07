package com.dbumama.market.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseApiController;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "product")
public class ProductController extends BaseApiController{

	@RPCInject
	private ProductService productService;
	@RPCInject
	private MultiGroupService grouponService;
	@RPCInject
	CartService cartService;
	@RPCInject
	private ProductReviewService productReviewService;
	
	public void list(){
		Long productCategoryId = getJSONParaToLong("categId");
		String keyword = getJSONPara("keyword");
		ProductMobileParamDto mobileParamDto = new ProductMobileParamDto(getAuthUserId(), getPageNo());
		mobileParamDto.setCategId(productCategoryId);
		mobileParamDto.setKeyword(keyword);
		List<ProductMobileResultDto> productResultDtos = productService.findProducts4Mobile(mobileParamDto);
		rendSuccessJson(productResultDtos);
	}

	public void detail(){
		try {
			JSONObject result = getJSONRespones();
			result.put("productDetail", productService.getMobieDetail(getJSONParaToLong("id")));
	        result.put("cartCount", cartService.getCartItemCountByBuyer(getBuyerId()));
	        rendSuccessJson(result);
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void getReviewsByPage(){
		try{
	        rendSuccessJson(productReviewService.getProductReviews(getPageNo(), getPageSize(), getJSONParaToLong("productId")));
		}catch(Exception e){
			e.printStackTrace();
			rendFailedJson(e.getMessage());
		}
	}
	
}
