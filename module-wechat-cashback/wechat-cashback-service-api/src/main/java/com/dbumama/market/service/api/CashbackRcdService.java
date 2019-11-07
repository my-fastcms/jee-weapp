package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.CashbackRcd;
import io.jboot.db.model.Columns;

import java.util.List;

public interface CashbackRcdService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public CashbackRcd findById(Object id);


    /**
     * find all model
     *
     * @return all <CashbackRcd
     */
    public List<CashbackRcd> findAll();


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
    public boolean delete(CashbackRcd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(CashbackRcd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(CashbackRcd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(CashbackRcd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<CashbackRcd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<CashbackRcd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<CashbackRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}