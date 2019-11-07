package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductImage;
import com.dbumama.market.service.api.ProductAllResultDto;
import com.dbumama.market.service.api.ProductDetailResultDto;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductMobileParamDto;
import com.dbumama.market.service.api.ProductMobileResultDto;
import com.dbumama.market.service.api.ProductParamDto;
import com.dbumama.market.service.api.ProductResultDto;
import com.dbumama.market.service.api.ProductSpecPriceResultDto;
import com.dbumama.market.service.api.ProductSubmitParamDto;

import io.jboot.db.model.Columns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ProductService  {
	
	public List<ProductResultDto> getMarketableByIds(Long appId, String ids) throws ProductException;

	public void saveOrUpdate(ProductSubmitParamDto productSubmitParamDto) throws ProductException;

	public Page<ProductResultDto> list(ProductParamDto productParamDto) throws ProductException;

	public ProductAllResultDto findAllResultDto(ProductParamDto productParamDto);

	/**
	 * 微信端商品详情页数据接口
	 * @return
	 */
	public ProductDetailResultDto getMobieDetail(Long productId);

	/**
	 * 微信端获取商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public List<ProductMobileResultDto> findProducts4Mobile(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 微信端获取打折商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public Page<ProductMobileResultDto> getMobilePromotionProduct(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 微信端获取拼团商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public Page<ProductMobileResultDto> getMobileGroupProduct(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 微信端获取热卖商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public List<ProductMobileResultDto> getHotProduct(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 微信端获取最新上架商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public List<ProductMobileResultDto> getNewProduct(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 微信端获取推荐商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public List<ProductMobileResultDto> getRecommendProduct(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 微信端获取首页商品列表
	 * 
	 * @param mobileParamDto
	 * @return
	 * @throws ProductException
	 */
	public List<ProductMobileResultDto> getIndexProduct(ProductMobileParamDto mobileParamDto) throws ProductException;

	/**
	 * 获取商品不同规格对应的不同价格以及库存，物流重量 如果商品设置有限时打折，拼团等活动，获取对应的打折价格 key 为规格id值，比如 1,4,8
	 * ProductSpecPriceResultDto 为该规格对应的价格以及库存
	 * 
	 * @param productId
	 * @return
	 * @throws ProductException
	 */
	public HashMap<String, ProductSpecPriceResultDto> getProductSpecPrice(Long productId) throws ProductException;

	/**
	 * 批量获取已选择的商品
	 * @return
	 */
	public List<ProductResultDto> getProducts(String productIds) throws ProductException;

	public Long getProductCountByCategroyId(Long categroyId);

	/**
	 * 获取小程序上架商品数
	 * 
	 * @param authUserId
	 * @return
	 */
	public Long getWeappPorudctCount(Long authUserId);

	/**
	 * 导入外部商品
	 * 
	 * @param product
	 * @param productImages
	 */
	public void importProduct(Product product, ArrayList<ProductImage> productImages) throws ProductException;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Product findById(Object id);


    /**
     * find all model
     *
     * @return all <Product
     */
    public List<Product> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Product model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Product model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Product model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Product model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Product> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Product> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Product> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}