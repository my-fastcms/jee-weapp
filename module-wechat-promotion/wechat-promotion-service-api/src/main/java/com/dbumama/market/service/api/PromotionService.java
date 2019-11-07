package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductSpecItem;
import com.dbumama.market.model.Promotion;
import com.dbumama.market.model.PromotionSet;
import com.dbumama.market.service.api.ProdPromotionResultDto;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductParamDto;
import com.dbumama.market.service.api.ProductResultDto;
import com.dbumama.market.service.api.PromotionParamDto;
import com.dbumama.market.service.api.PromotionResultDto;
import com.dbumama.market.service.api.PromotionSetResultDto;
import com.dbumama.market.service.api.UmpException;

import io.jboot.db.model.Columns;

import java.util.List;

public interface PromotionService  {
	
	public List<Record> getPromotionServiceMini(Long authUserId,
                                                int pageNo, int pageSize);

	/**
	 * 保存限时打折活动
	 * @param promotion
	 * @param promotionSetItems
	 * @throws UmpException
	 */
	public void save(Promotion promotion, Long appId, String promotionSetItems) throws UmpException;
	
	/**
	 * 获取未参与打折的商品列表
	 * @param productParamDto
	 * @return
	 * @throws ProductException
	 */
	public Page<ProductResultDto> getProductsNoPromotionPage(ProductParamDto productParamDto) throws ProductException;
	
	/**
	 * 获取现实打折活动信息，包括打折设置明细数据
	 * @param id
	 * @return
	 * @throws UmpException
	 */
	public PromotionResultDto getPromotionInfo(Long id) throws UmpException;
	
	/**
	 * 获取现实打折活动信息，包括打折设置明细数据
	 * @param promotion
	 * @return
	 * @throws UmpException
	 */
	public PromotionResultDto getPromotionInfo(Promotion promotion) throws UmpException;
	
	/**
	 * 获取用户设置的限时打折活动列表
	 * @param promotionDto
	 * @return
	 */
	public Page<PromotionResultDto> list(PromotionParamDto promotionParam) throws UmpException;
	
	/**
	 * 根据商品获取商品的限时打折数据
	 * @param productId
	 * @return
	 * @throws UmpException
	 */
	public ProdPromotionResultDto getProductPromotion(Product product) throws UmpException;
	
	/**
	 * 获取商品限时打折的价格
	 * 多规格返回折后价格区间
	 * @param product
	 * @return
	 * @throws UmpException
	 */
	public String getProductPromotionPriceSection(Product product, PromotionSetResultDto promotionSetParam) throws UmpException;
	
	/**
	 * 获取商品有效时间范围内的打折配置
	 * @param product
	 * @return
	 * @throws UmpException
	 */
	public PromotionSet getProductPromotionSet(Product product) throws UmpException;
	
	/**
	 * 获取商品限时打折的折扣价(商品有多规格)
	 * @param product
	 * @return
	 * @throws UmpException
	 */
	public String getProductPromotionPrice(Product product, ProductSpecItem productSpecItem) throws UmpException;
	
	/**
	 * 获取商品限时打折的折扣价(商品统一规格)
	 * @param product
	 * @return
	 * @throws UmpException
	 */
	public String getProductPromotionPrice(Product product) throws UmpException;
	
	/**
	 * 通过promotionId获取商品信息 
	 */
	public Page<ProductResultDto> getProductsPromotionPage(ProductParamDto productParamDto) throws ProductException;
	
	/**
	 * 微页面获取打折商品
	 * @param promotion
	 * @return
	 * @throws UmpException
	 */
	public PromotionResultDto getDiscountInfo(Promotion promotion) throws UmpException;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Promotion findById(Object id);


    /**
     * find all model
     *
     * @return all <Promotion
     */
    public List<Promotion> findAll();


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
    public boolean delete(Promotion model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Promotion model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Promotion model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Promotion model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Promotion> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Promotion> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Promotion> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}