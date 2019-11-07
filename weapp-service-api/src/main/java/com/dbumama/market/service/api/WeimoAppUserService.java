package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.WeimoAppUser;
import io.jboot.db.model.Columns;

import java.util.Date;
import java.util.List;

public interface WeimoAppUserService  {

	WeimoAppUser findWeimoAppUser(Long sellerId, Long appId, String version, Date endDate);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public WeimoAppUser findById(Object id);


    /**
     * find all model
     *
     * @return all <WeimoAppUser
     */
    public List<WeimoAppUser> findAll();


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
    public boolean delete(WeimoAppUser model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(WeimoAppUser model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(WeimoAppUser model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(WeimoAppUser model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<WeimoAppUser> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<WeimoAppUser> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<WeimoAppUser> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}