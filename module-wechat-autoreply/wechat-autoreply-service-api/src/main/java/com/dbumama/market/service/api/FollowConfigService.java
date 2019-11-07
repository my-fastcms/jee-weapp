package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.FollowConfig;
import io.jboot.db.model.Columns;

import java.util.List;

public interface FollowConfigService  {
	
	public FollowConfig findByAppId(Long shopId);
	
	void reply(AuthUser authUser, String openid);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FollowConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <FollowConfig
     */
    public List<FollowConfig> findAll();


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
    public boolean delete(FollowConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(FollowConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(FollowConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FollowConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<FollowConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<FollowConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<FollowConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}