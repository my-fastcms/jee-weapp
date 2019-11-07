package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuReplyNews;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuReplyNewsService  {
	
	List<MenuReplyNews> findNewsByConfigId(Long configId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuReplyNews findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuReplyNews
     */
    public List<MenuReplyNews> findAll();


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
    public boolean delete(MenuReplyNews model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuReplyNews model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuReplyNews model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuReplyNews model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuReplyNews> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuReplyNews> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuReplyNews> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}