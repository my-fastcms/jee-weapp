package com.dbumama.market.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.service.api.ProductCategoryService;
import com.dbumama.market.service.api.ProductService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value = "category", viewPath = "category")
@RequiresPermissions(value="/category")
public class CategoryController extends BaseAppAdminController {
	
	@RPCInject
	private ProductService productService;
	@RPCInject
	private ProductCategoryService productCategoryService;
	
	public void index() {
		render("category_index.html");
	}
	
	public void list(){
		rendSuccessJson(productCategoryService.page(getAuthUserId(), getPageNo(), getPageSize()));
	}
	
	public void add(){
		if(getParaToLong(0) != null){
			ProductCategory productCategory=productCategoryService.findById(getParaToLong(0));
			setAttr("productCategory", productCategory);
		}
		render("category_add.html");
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "name", message = "请输入分类名称"),
	})
	public void save(final Long id, final String name, final String img_path, final Integer orders){
		ProductCategory category = productCategoryService.findById(id);
		if(category == null){
			category = new ProductCategory();
			category.setAppId(getAuthUser().getId());
		}
		category.setName(name).setImgPath(img_path).setOrders(orders);
		
		try {
			productCategoryService.saveOrUpdate(category);
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson("保存失败");
		}
		
	}
	
	public void del(){
		String ids = getPara("ids");
		
		Long count = productService.getProductCountByCategroyId(Long.valueOf(ids));
		if(count != null && count>0) {
			rendFailedJson("分类已有商品使用，不能删除！");
			return;
		}
		
		ProductCategory category = productCategoryService.findById(Long.valueOf(ids));
		category.setActive(0);
		if(productCategoryService.update(category)){
			rendSuccessJson("操作成功！");			
		}else{
			rendFailedJson("删除失败");
		}
		
	}
	
}
