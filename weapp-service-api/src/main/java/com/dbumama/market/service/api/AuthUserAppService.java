package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthUserApp;
import io.jboot.db.model.Columns;

import java.util.List;

public interface AuthUserAppService  {
	
	/**
	* @Title: list
	* @Description: 查询该公众号未到期的插件信息
	 */
	List<AuthUserApp> list(Long authUserId);
	
	AuthUserApp findByApp(Long authUserId, Long appId);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthUserApp findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthUserApp
     */
    public List<AuthUserApp> findAll();


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
    public boolean delete(AuthUserApp model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthUserApp model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthUserApp model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthUserApp model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthUserApp> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthUserApp> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthUserApp> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}