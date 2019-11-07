package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ProductImage;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductImageService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductImage findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductImage
     */
    public List<ProductImage> findAll();


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
    public boolean delete(ProductImage model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductImage model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductImage model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductImage model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductImage> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductImage> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductImage> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}