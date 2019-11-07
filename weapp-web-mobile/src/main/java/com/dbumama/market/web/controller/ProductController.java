package com.dbumama.market.web.controller;

import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseMobileController;
import com.dbumama.market.web.core.interceptor.WechatJssdkInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


/**
 * Created by dextrys on 2016/1/8.
 */
@RequestMapping(value="product")
public class ProductController extends BaseMobileController {
	
	@RPCInject
	CartService cartService;
	@RPCInject
	private ProductService productService;
	@RPCInject
	private ProductCategoryService productCategoryService;
	@RPCInject
	private ProductSpecService productSpecificationService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private ProductReviewService productReviewService;
	
	public void index(){
		List<ProductCategory> productCategorys = productCategoryService.list(getAuthUserId());
		String keyword=getPara("keyword");
		setAttr("productCategory", productCategorys);
		Long productCategoryId = getParaToLong("categId");
		setAttr("categId", productCategoryId);
		setAttr("keyword", keyword);
		render("/product/index.html");
	}
		
	public void list(){
		Long productCategoryId = getParaToLong("categId");
		String keyword=getPara("keyword");
		String orderBy = getPara("orderBy");
		String orderType = getPara("orderType");
		BigDecimal startPrice = new BigDecimal(getPara("startPrice","0"));
		BigDecimal endPrice = new BigDecimal(getPara("endPrice","0"));
		ProductMobileParamDto mobileParamDto = new ProductMobileParamDto(getAuthUserId(), getPageNo());
		mobileParamDto.setCategId(productCategoryId);
		mobileParamDto.setStartPrice(startPrice);
		mobileParamDto.setEndPrice(endPrice);
		mobileParamDto.setKeyword(keyword);
		mobileParamDto.setOrderBy(orderBy);
		mobileParamDto.setOrderType(orderType);
		List<ProductMobileResultDto> productResultDtos = productService.findProducts4Mobile(mobileParamDto);
		rendSuccessJson(productResultDtos);
	}

	@Before(WechatJssdkInterceptor.class)
    public void detail() {
    	try {
    		ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getParaToLong("id"));
        	if(authUserService.getAuthUserByAppId(getAppId()) != null){
        		productParamDto.setAppId(authUserService.getAuthUserByAppId(getAppId()).getAppId());
        	}
    		ProductDetailResultDto productDetail = productService.getMobieDetail(getParaToLong("id"));
    		setAttr("productDetail", productDetail);
    		
            Page<Record> reviews = productReviewService.getProductReviews(1, 10, productParamDto.getProductId());
            
            if(reviews.getList() != null && reviews.getList().size()>0)
            	setAttr("reviews", reviews.getList());
            
            setAttr("appUser", authUserService.getAuthUserByAppId(getAppId()));
            
//            if(productDetail.getGroupInfo() == null){
//            	setAttr("cartCount", cartService.getCartItemCountByBuyer(getBuyerId()));
//            	render("/product/detail.html");
//            	return;
//            }

        	if(getParaToLong("groupId") != null){
        		//读取拼团成员等信息
//            	GroupingResultDto groupingInfo = grouponService.getGroupAndJoinUserInfos(getParaToLong("groupId"), getBuyerId());
//            	setAttr("groupingInfo", groupingInfo);	      		
        	}
        	//是否需要关注开团
//        	MultiGroup group = grouponService.getProductMultiGroup(productDetail.getProduct());
//        	if(group == null) throw new Exception("拼团信息获取出错");
//        	setAttr("multiGroup", group);
//        	if(group.getNeedFollows() != null && group.getNeedFollows()){
//        		if(getBuyerUser() == null ||
//        				getBuyerUser().getActive() == null ||
//        				getBuyerUser().getActive().intValue() == 0 ||
//        				getBuyerUser().getSubscribe() == null ||
//        				getBuyerUser().getSubscribe().intValue() == 0){
//        			setAttr("followsQrcode",group.getFollowsUrl());
//        		}
//        	}
        	//正在进行中的拼团
//        	List<GroupingResultDto> groupings = grouponService.getGroupsByProduct(productDetail.getProduct());
//        	setAttr("groupings", groupings);
        	render("/product/g_detail.html");
        
		} catch (Exception e) {
			setAttr("error", "获取商品详情出错:" + e.getMessage());
			render("/product/detail_error.html");
		}
    }
	
	/**
	* @Title: assisfreeDetail
	* @Description: 跳转到属于助力免单商品详情页
	 */
	@Before(WechatJssdkInterceptor.class)
    public void assisfreeDetail() {
    	try {
    		ProductDetailResultDto productDetail = productService.getMobieDetail(getParaToLong("id"));
    		
    		setAttr("assisfreeId", getParaToLong("assisfreeId"));
    		setAttr("openid", getOpenId());
    		setAttr("id", getParaToLong("id"));
    		setAttr("productDetail", productDetail);
            
        	render("/product/assisfree_detail.html");            	
		} catch (Exception e) {
			setAttr("error", "获取商品详情出错:" + e.getMessage());
			render("/product/detail_error.html");
		}
    }
	
	/**
	 * @Title: taskDetail
	 * @Description: 跳转到属于任务活动商品详情页
	 */
	@Before(WechatJssdkInterceptor.class)
	public void taskDetail() {
		try {
			ProductDetailResultDto productDetail = productService.getMobieDetail(getParaToLong("id"));
			
			setAttr("id", getParaToLong("id"));
			setAttr("awardSendId", getParaToLong("awardSendId"));
			setAttr("productDetail", productDetail);
			
			render("/product/task_detail.html");            	
		} catch (Exception e) {
			setAttr("error", "获取商品详情出错:" + e.getMessage());
			render("/product/detail_error.html");
		}
	}
    
    /**
     * 参团购买
     */
	@Before(WechatJssdkInterceptor.class)
    public void join(){
    	Product product = productService.findById(getParaToLong("productId"));
    	setAttr("product", product);
//    	ProdGroupResultDto groupInfo=grouponService.getProductGroup(product);
//    	setAttr("groupInfo", groupInfo);
//    	GroupingResultDto groupingInfo = grouponService.getGroupAndJoinUserInfos(getParaToLong("groupId"), getBuyerId());
//    	setAttr("groupingInfo", groupingInfo);   
    	render("/product/g_join.html");
    }
    
    public void stocks(){
    	String productId=getPara("productId");
    	try {
    		HashMap<String, ProductSpecPriceResultDto> data = productService.getProductSpecPrice(Long.valueOf(productId));
    		rendSuccessJson(data);
		} catch (ProductException e) {
			rendFailedJson(e.getMessage());
		}
    }
    
    public void getPromotionProductList(){
    	ProductMobileParamDto mobileParamDto = new ProductMobileParamDto(getAuthUserId(), getPageNo());
    	try {
    		Page<ProductMobileResultDto> products = productService.getMobilePromotionProduct(mobileParamDto);
    		rendSuccessJson(products.getList());
		} catch (ProductException e) {
			rendFailedJson(e.getMessage());
		}
    }
    
    /**
     * 获取打折商品列表
     */
    public void getPromotionProduct(){
    	render("/product/promotion_index.html");
    }
    
    /**
     * 获取拼团商品列表
     */
    public void getGroupProduct(){
    	render("/groups/prod_group_index.html");
    }
    
    public void getGroupProductList(){
    	ProductMobileParamDto mobileParamDto = new ProductMobileParamDto(getAuthUserId(), getPageNo());
    	try {
    		Page<ProductMobileResultDto> products = productService.getMobileGroupProduct(mobileParamDto);
    		rendSuccessJson(products.getList());
		} catch (ProductException e) {
			rendFailedJson(e.getMessage());
		}
    }
    
    /**
    * @Title: getMostReviews
    * @Description: 获取更多的评论
    * @return void    返回类型
    * @throws
     */
    public void getMostReviews(){
        Page<Record> reviews = productReviewService.getProductReviews(getParaToInt("pageNo"), 10, getParaToLong("productId"));
        if(reviews.getList() != null && reviews.getList().size()>0){
        	rendSuccessJson(reviews.getList());
        }else{
        	rendFailedJson("没有更多评论了");
        }
    }
    
}
