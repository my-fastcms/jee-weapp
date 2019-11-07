package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.model.AuthUserTemplate;
import com.dbumama.market.model.WeappTemplate;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface AuthUserTemplateService  {
	
	WeappTemplate findWeappTemplate(Long authUserId);
	
	AuthUserTemplate getAuthUserTemplate(Long authUserId);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthUserTemplate findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthUserStyle
     */
    public List<AuthUserTemplate> findAll();


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
    public boolean delete(AuthUserTemplate model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthUserTemplate model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthUserTemplate model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthUserTemplate model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthUserTemplate> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthUserTemplate> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthUserTemplate> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}