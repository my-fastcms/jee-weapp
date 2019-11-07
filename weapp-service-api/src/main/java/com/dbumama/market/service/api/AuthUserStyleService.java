package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthUserStyle;
import com.dbumama.market.model.WeappStyle;

import io.jboot.db.model.Columns;

import java.util.List;

public interface AuthUserStyleService  {
	
	WeappStyle getAuthUserStyle(Long authUserId);
	
	AuthUserStyle getAuthUserStyleByAppandStyle(Long authAppId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthUserStyle findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthUserStyle
     */
    public List<AuthUserStyle> findAll();


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
    public boolean delete(AuthUserStyle model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthUserStyle model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthUserStyle model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthUserStyle model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthUserStyle> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthUserStyle> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthUserStyle> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}