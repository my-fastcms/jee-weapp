package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.FullCutSet;
import io.jboot.db.model.Columns;

import java.util.List;

public interface FullCutSetService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FullCutSet findById(Object id);


    /**
     * find all model
     *
     * @return all <FullCutSet
     */
    public List<FullCutSet> findAll();


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
    public boolean delete(FullCutSet model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(FullCutSet model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(FullCutSet model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FullCutSet model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<FullCutSet> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<FullCutSet> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<FullCutSet> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}