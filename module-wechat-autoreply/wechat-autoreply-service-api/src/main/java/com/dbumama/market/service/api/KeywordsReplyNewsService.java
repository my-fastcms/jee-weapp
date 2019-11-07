package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.KeywordsReplyNews;
import io.jboot.db.model.Columns;

import java.util.List;

public interface KeywordsReplyNewsService  {
	
	public List<KeywordsReplyNews> findNewsByConfigId(Long configId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public KeywordsReplyNews findById(Object id);


    /**
     * find all model
     *
     * @return all <KeywordsReplyNews
     */
    public List<KeywordsReplyNews> findAll();


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
    public boolean delete(KeywordsReplyNews model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(KeywordsReplyNews model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(KeywordsReplyNews model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(KeywordsReplyNews model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<KeywordsReplyNews> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<KeywordsReplyNews> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<KeywordsReplyNews> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}