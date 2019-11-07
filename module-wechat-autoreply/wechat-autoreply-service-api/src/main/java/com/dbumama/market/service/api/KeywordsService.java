package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.Keywords;

import io.jboot.db.model.Columns;

import java.util.List;

public interface KeywordsService  {

	public Keywords save(Long shopId, String keywordsText, String keywordsConfig, Long keywordsId, Boolean enableKeywords, Integer autoTagid, Integer cancelTagid) throws Exception;
	
	public List<Keywords> findByAppId(Long appId);
	
	Keywords findKeywordsByText(Long appId, String keywords);
	
	void reply(AuthUser authUser, String keywords, String openid) throws WxmallBaseException;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Keywords findById(Object id);


    /**
     * find all model
     *
     * @return all <Keywords
     */
    public List<Keywords> findAll();


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
    public boolean delete(Keywords model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Keywords model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Keywords model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Keywords model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Keywords> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Keywords> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Keywords> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}