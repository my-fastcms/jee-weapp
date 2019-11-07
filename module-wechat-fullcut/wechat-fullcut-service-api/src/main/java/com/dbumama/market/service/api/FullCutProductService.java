package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.FullCutProduct;
import io.jboot.db.model.Columns;

import java.util.List;

public interface FullCutProductService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FullCutProduct findById(Object id);


    /**
     * find all model
     *
     * @return all <FullCutProduct
     */
    public List<FullCutProduct> findAll();


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
    public boolean delete(FullCutProduct model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(FullCutProduct model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(FullCutProduct model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FullCutProduct model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<FullCutProduct> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<FullCutProduct> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<FullCutProduct> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}