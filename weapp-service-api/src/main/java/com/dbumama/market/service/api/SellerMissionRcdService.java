package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.SellerMissionRcd;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SellerMissionRcdService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SellerMissionRcd findById(Object id);


    /**
     * find all model
     *
     * @return all <SellerMissionRcd
     */
    public List<SellerMissionRcd> findAll();


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
    public boolean delete(SellerMissionRcd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(SellerMissionRcd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(SellerMissionRcd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SellerMissionRcd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<SellerMissionRcd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<SellerMissionRcd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<SellerMissionRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}