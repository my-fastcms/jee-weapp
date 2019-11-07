package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.SpecificationValue;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SpecificationValueService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SpecificationValue findById(Object id);


    /**
     * find all model
     *
     * @return all <SpecificationValue
     */
    public List<SpecificationValue> findAll();


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
    public boolean delete(SpecificationValue model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(SpecificationValue model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(SpecificationValue model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SpecificationValue model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<SpecificationValue> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<SpecificationValue> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<SpecificationValue> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}