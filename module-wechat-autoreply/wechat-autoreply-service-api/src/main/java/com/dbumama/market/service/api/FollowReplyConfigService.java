package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.FollowReplyConfig;
import com.dbumama.market.service.api.FollowReplyConfigResDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface FollowReplyConfigService  {

	public void save(Long shopId, String followConfig, Boolean enableConfig) throws WxmallBaseException;
	
	public List<FollowReplyConfig> findConfigByFollowId(Long followId);
	
	public List<FollowReplyConfigResDto> findFollowReplyConfig(Long followId);
	
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FollowReplyConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <FollowReplyConfig
     */
    public List<FollowReplyConfig> findAll();


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
    public boolean delete(FollowReplyConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(FollowReplyConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(FollowReplyConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FollowReplyConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<FollowReplyConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<FollowReplyConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<FollowReplyConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}