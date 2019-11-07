package com.dbumama.market.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Cart;
import com.dbumama.market.service.api.CartItemResultDto;
import com.dbumama.market.service.api.CartService;
import com.dbumama.market.service.api.ProdFullCutResultDto;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "cart")
public class CartController extends BaseApiController {

    @RPCInject
    CartService cartService;

    @Before(ApiSessionInterceptor.class)
    public void list() {
        try {
            List<CartItemResultDto> cartItems = cartService.getCartsByBuyer(getBuyerId());
            List<ProdFullCutResultDto> fullCutDtos = cartService.getCartFullCat(cartItems);
            JSONObject result = new JSONObject();
            result.put("cartItems", cartItems);
            result.put("fullCuts", fullCutDtos);
            rendSuccessJson(result);
        } catch (WxmallBaseException e) {
            rendFailedJson(e.getMessage());
        }
    }

    @Before(ApiSessionInterceptor.class)
    public void addCart(){
        Long productId = getJSONParaToLong("productId");
        int quantity = getJSONParaToInteger("quantity");
        String speci = getJSONPara("speci");//规格值
        try {
            cartService.add(getBuyerId(), productId, quantity, speci);
            //取得购物车的品种数
            Long count=cartService.getCartItemCountByBuyer(getBuyerId());
            rendSuccessJson(count);
        } catch (WxmallBaseException e) {
            rendFailedJson(e.getMessage());
        }
    }
    /**
     * 取得购物车的品种数
     */
    public void getCartCount(){
        try {
            Long count=cartService.getCartItemCountByBuyer(getBuyerId());
            rendSuccessJson(count);
        } catch (WxmallBaseException e) {
            rendFailedJson(e.getMessage());
        }
    }

    @Before(ApiSessionInterceptor.class)
    public void delete() {
        String cartIds = getJSONPara("ids");
        if(StrKit.isBlank(cartIds)){
            rendFailedJson("请选择要删除的项");
            return;
        }
        for(String id : cartIds.split("#")){
            Cart citem = cartService.findById(Long.valueOf(id));
            if(citem != null){
                cartService.delete(citem);
            }
        }
        rendSuccessJson();
    }

}
