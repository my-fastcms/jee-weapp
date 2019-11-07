package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ProductGroup;
import com.dbumama.market.model.ProductGroupSet;
import com.dbumama.market.service.api.GroupException;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductGroupService  {
	
	public void save(ProductGroup productGroup,String productIds,Long appId) throws GroupException;
    /**
     * 提供分组id获取商品列表
     * @param groupId
     * @return
     * @throws ProductException
     */
    public List<ProductResultDto> getGroupProduct(Long groupId) throws ProductException;
    
    public Page<ProductGroup> page(Long appId, Integer pageNo, Integer pageSize);
    
    List<ProductGroupSet> getProductGroupSetsByGroupId(Long groupId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductGroup
     */
    public List<ProductGroup> findAll();


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
    public boolean delete(ProductGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductGroup model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductGroup> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductGroup> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductGroup> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}