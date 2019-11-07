package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ProductSpecItem;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductSpecItemService  {
	
	public List<ProductSpecItem> findByProductId(Long productId);
	
	/**
	 * 
	* @Title: getProductSpecItemByPIDAndSFV
	* @Description: 用于获取多规格下的产品在该规格下的库存数
	* @param  proudctId   商品Id
	* @param sfv 	             商品规格值
	* @return Integer       返回库存数量
	* @throws
	 */
	public ProductSpecItem getProductSpecItemByPIDAndSFV(Long proudctId,String sfv);
	
	/**
	 * 通过商品id和规格字段获取该商品规格的明细
	 */
	public ProductSpecItem getProductSpecItem(Long proudctId,String sfv);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductSpecItem findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductSpecItem
     */
    public List<ProductSpecItem> findAll();


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
    public boolean delete(ProductSpecItem model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductSpecItem model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductSpecItem model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductSpecItem model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductSpecItem> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductSpecItem> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductSpecItem> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}