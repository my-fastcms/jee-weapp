package com.dbumama.market.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.service.api.DeliveryTemplateService;
import com.dbumama.market.service.api.ProductAllResultDto;
import com.dbumama.market.service.api.ProductCategoryService;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductParamDto;
import com.dbumama.market.service.api.ProductService;
import com.dbumama.market.service.api.ProductSubmitParamDto;
import com.dbumama.market.service.api.SpecificationParamDto;
import com.dbumama.market.service.api.SpecificationService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jboot.web.validate.ValidateRenderType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RequestMapping(value="product", viewPath="product")
@RequiresPermissions(value="/product")
@Api(description = "商品相关接口文档", basePath = "/product", tags = "商品")
public class ProductController extends BaseAppAdminController {
	
	@RPCInject
	private ProductCategoryService productCategoryService;
	@RPCInject
	private SpecificationService specificationService;
	@RPCInject
	private ProductService productService;
	@RPCInject
	private DeliveryTemplateService deliveryTemplateService;
	
	public void index() {
		List<ProductCategory> productCategory = productCategoryService.list(getAuthUserId());
		setAttr("productCategory", productCategory);
		render("pd_index.html");
	}
	
	@ApiOperation(value = "在售商品列表", httpMethod = "GET", notes = "product list")
	public void list(){
		ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
		productParamDto.setPageSize(getPageSize());
		productParamDto.setIsMarketable(1);
		productParamDto.setProductIds(getPara("productIds"));
		productParamDto.setCategoryId(getParaToLong("categoryId"));
		productParamDto.setName(getPara("name"));
		try {
			rendSuccessJson(productService.list(productParamDto));			
		} catch (ProductException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void warehouseIndex() {
		List<ProductCategory> productCategory = productCategoryService.list(getAuthUserId());
		setAttr("productCategory", productCategory);
		render("pd_warehouse.html");
	}
	
	@ApiOperation(value = "仓库商品列表", httpMethod = "GET", notes = "product list")
	public void warehouse(){
		ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
		productParamDto.setIsMarketable(0);
		productParamDto.setCategoryId(getParaToLong("categoryId"));
		productParamDto.setName(getPara("name"));
		try {
			rendSuccessJson(productService.list(productParamDto));			
		} catch (ProductException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void shouqin(){
		List<ProductCategory> productCategory = productCategoryService.list(getAuthUserId());
		setAttr("productCategory", productCategory);
		render("pd_shouqin.html");
	}
	
	@ApiOperation(value = "已售罄商品列表", httpMethod = "GET", notes = "product list")
	public void shouqinList(){
		ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
		productParamDto.setIsMarketable(1);
		productParamDto.setCategoryId(getParaToLong("categoryId"));
		productParamDto.setName(getPara("name"));
		productParamDto.setSaleOver("1");
		try {
			rendSuccessJson(productService.list(productParamDto));			
		} catch (ProductException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void add() {
		List<ProductCategory> productCategory = productCategoryService.list(getAuthUserId());
		setAttr("productCategory", productCategory);
		SpecificationParamDto specificationParamDto = new SpecificationParamDto();
		specificationParamDto.setAuthUserId(getAuthUserId());
		setAttr("specificationResultDto", specificationService.findAll(specificationParamDto));
		render("pd_edit.html");
	}
	
	public void edit() {
		List<ProductCategory> productCategory = productCategoryService.list(getAuthUserId());
		setAttr("productCategory", productCategory);
		
		Product product=productService.findById(getParaToLong(0));
		setAttr("product", product);
		
		if(product != null){
			setAttr("productDeliveryTemplate", deliveryTemplateService.findById(product.getDeliveryTemplateId()));
			
			SpecificationParamDto specificationParamDto = new SpecificationParamDto();
			specificationParamDto.setAuthUserId(getAuthUserId());
			setAttr("specificationResultDto", specificationService.findAll(specificationParamDto));

			ProductParamDto productParamDto=new ProductParamDto(getAuthUserId(), product.getId());
			
			ProductAllResultDto allResultDto = productService.findAllResultDto(productParamDto);
			setAttr("productAllResultDto", allResultDto);
		}
		render("pd_edit.html");
	}
	
	/**
	 * 保存
	 * 
	 */
	@Before(POST.class)
	@EmptyValidate(value = {
        @Form(name = "productCategoryId", message = "请选择商品分类"),
        @Form(name = "is_unified_spec", message = "请选择规格类型"),
        @Form(name = "name", message = "请输入商品名称"),
        @Form(name = "image", message = "请上传商品列表图"),
        @Form(name = "introduction", message = "请编写商品详情"),
        @Form(name = "imgList", message = "请上传商品详情商品轮播图"),
        @Form(name = "delivery_type", message = "请选择运费设置"),
	}, renderType = ValidateRenderType.JSON)
	public void save(Long id, Long productCategoryId, String name, String image, String introduction) {
		String priceAndStock = getPara("stocks");
		String[] imgList = getParaValues("imgList");
		Long[] specificationIds = getParaValuesToLong("specificationIds");
		String[] specificationValues = getParaValues("specificationValues");
		Boolean isMarketable = getParaToBoolean("isMarketable", false);
		Boolean isPurchaseLimitation = getParaToBoolean("isPurchaseLimitation", false);
		Boolean isVirtualGoods = getParaToBoolean("isVirtualGoods", false);
		Boolean isPickUp = getParaToBoolean("isPickUp", false);
		Boolean isCityDis = getParaToBoolean("isCityDis", false);
		Boolean isUnifiedSpec = getParaToBoolean("is_unified_spec", false);
		String purchaseCount = getPara("purchaseCount");
		String price = getPara("price");
		String stock = getPara("stock");
		String marketPrice = getPara("market_price");
		Integer deliveryType = getParaToInt("delivery_type", 0);
		BigDecimal deliveryFees = new BigDecimal(getPara("delivery_fees", "0"));
		BigDecimal deliveryWeight = new BigDecimal(getPara("delivery_weight", "0"));
		Long deliveryTemplateId = getParaToLong("delivery_template_id");
		
		ProductSubmitParamDto productParamDto = new ProductSubmitParamDto(id, getAuthUserId(), imgList, productCategoryId, name, image, introduction);
		productParamDto.setImages(imgList);
		productParamDto.setSpecificationIds(specificationIds);
		productParamDto.setSpecificationValues(specificationValues);
		productParamDto.setIsMarketable(isMarketable);
		productParamDto.setIsVirtualGoods(isVirtualGoods);
		productParamDto.setIsPickUp(isPickUp);
		productParamDto.setIsCityDis(isCityDis);
		productParamDto.setIsPurchaseLimitation(isPurchaseLimitation);
		productParamDto.setPurchaseCount(purchaseCount);
		productParamDto.setPriceAndStock(priceAndStock);
		productParamDto.setPrice(price);
		productParamDto.setMarketPrice(marketPrice);
		productParamDto.setStock(stock);
		productParamDto.setIsUnifiedSpec(isUnifiedSpec);
		productParamDto.setDeliveryType(deliveryType);
        productParamDto.setDeliveryFees(deliveryFees);
        productParamDto.setDeliveryTemplateId(deliveryTemplateId);
        productParamDto.setDeliveryWeight(deliveryWeight);
        try {
			productService.saveOrUpdate(productParamDto);
			if(productParamDto.getIsMarketable()){
				redirect("/product/index");
			}else {
				redirect("/product/warehouseIndex");
			}
		} catch (ProductException e) {
			setAttr("error", e.getMessage());
			render("/product/pd_error.html");
		}
	}
	
	/**
	 * 导入外部商品
	 */
	public void importin(){
		render("/product/pd_import.html");
	}
	
	/**
	 * 下架该商品
	 */
   public void offShelve(){
	   String ids = getPara("ids");
	   for(String id : ids.split("-")){
		   Product product=productService.findById(Long.valueOf(id));
		   product.setIsMarketable(false);
		   productService.update(product);
	   }
	   rendSuccessJson("操作成功！");
   }
   
   /**
	 * 上架该商品
	 */
   public void shelve(){
	   String ids = getPara("ids");
	   for(String id : ids.split("-")){
		   Product product=productService.findById(Long.valueOf(id));
		   product.setIsMarketable(true);
		   productService.update(product);
	   }
	   rendSuccessJson("操作成功！");
   }
   
   public void listUrl(){
	   render("pd_list_url.html");
   }

}
