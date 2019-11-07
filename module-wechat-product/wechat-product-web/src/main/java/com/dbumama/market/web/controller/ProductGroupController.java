package com.dbumama.market.web.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.ProductGroupSet;
import com.dbumama.market.service.api.ProductGroupService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "group", viewPath = "group")
@RequiresPermissions(value="/group")
public class ProductGroupController extends BaseAppAdminController {
	@RPCInject
	private ProductGroupService productGroupService;
	public void index() {
		render("group_index.html");
	}
	
	public void list(){
		rendSuccessJson(productGroupService.page(getAuthUserId(), getPageNo(), getPageSize()));
	}
	
	public void add(){
		if(getParaToLong("pid") != null){
			setAttr("group",productGroupService.findById(getParaToLong("pid")));
			List<ProductGroupSet> groupProducts = productGroupService.getProductGroupSetsByGroupId(getParaToLong("pid"));
			setAttr("groupProducts", groupProducts);
		}
		render("group_add.html");
	}
	
	public void save(){
		/*try {
			final String productIds = getPara("product_ids");
			ProductGroup productGroup=getModel();
			productGroupService.save(productGroup, productIds, getSellerId());
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}*/
	}	
	
}
