package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.WeappStyle;
import com.dbumama.market.service.api.WeappStyleResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface WeappStyleService  {
	
	WeappStyleResultDto getAppStyle(Long authUserId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public WeappStyle findById(Object id);


    /**
     * find all model
     *
     * @return all <WeappStyle
     */
    public List<WeappStyle> findAll();


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
    public boolean delete(WeappStyle model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(WeappStyle model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(WeappStyle model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(WeappStyle model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<WeappStyle> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<WeappStyle> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<WeappStyle> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}