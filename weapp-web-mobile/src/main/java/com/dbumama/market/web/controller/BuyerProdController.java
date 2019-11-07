package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.BuyerProd;
import com.dbumama.market.service.api.BuyerProdItemResultDto;
import com.dbumama.market.service.api.BuyerProdService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseMobileController;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * 
* @ClassName: BuyerProdController
* @Description: 商品收藏controller
* @author PC
* @date 2018年11月15日
*
 */
@RequestMapping(value = "buyerProd")
public class BuyerProdController extends BaseMobileController{

	@RPCInject
	BuyerProdService buyerProdService;
	
	public void index() {
		List<BuyerProdItemResultDto> prodItems = buyerProdService.getProdByBuyer(getBuyerId());
    	setAttr("items", prodItems);
	    render("/buyerprod/index.html");
	}
	
	
	public void add(){
		Long productId = getParaToLong("productId");
        
        try {
        	buyerProdService.add(getBuyerId(), productId);
			rendSuccessJson();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void delete() {
        String buyerProdIds = getPara("ids");
        if(StrKit.isBlank(buyerProdIds)){
        	rendFailedJson("请选择要删除的项");
        	return;
        }
        for(String id : buyerProdIds.split("#")){
        	BuyerProd buyerProd = buyerProdService.findById(Long.valueOf(id));
        	if(buyerProd != null){
        		buyerProdService.delete(buyerProd);
        	}
        }
        rendSuccessJson();
    }
}
