package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ProductCategory;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductCategoryService  {
	
	public List<ProductCategory> list(Long appId);
	
	Page<ProductCategory> page(Long appId, Integer pageNo, Integer pageSize);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductCategory findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductCategory
     */
    public List<ProductCategory> findAll();


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
    public boolean delete(ProductCategory model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductCategory model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductCategory model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductCategory model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductCategory> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductCategory> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductCategory> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}