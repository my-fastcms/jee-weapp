package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.KeywordsReplyConfig;
import com.dbumama.market.service.api.KeywordsReplyConfigResDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface KeywordsReplyConfigService  {
	
	public List<KeywordsReplyConfig> findKeywordsByKeywordsId(Long keywordsId);
	
	public List<KeywordsReplyConfigResDto> findAllKeywordsReplyConfig(Long keywordsId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public KeywordsReplyConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <KeywordsReplyConfig
     */
    public List<KeywordsReplyConfig> findAll();


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
    public boolean delete(KeywordsReplyConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(KeywordsReplyConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(KeywordsReplyConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(KeywordsReplyConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<KeywordsReplyConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<KeywordsReplyConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<KeywordsReplyConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}