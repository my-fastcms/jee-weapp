package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuReplyRule;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuReplyRuleService  {
	
	MenuReplyRule findByMenuKey(Long shopId, String menuKey);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuReplyRule findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuReplyRule
     */
    public List<MenuReplyRule> findAll();


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
    public boolean delete(MenuReplyRule model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuReplyRule model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuReplyRule model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuReplyRule model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuReplyRule> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuReplyRule> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuReplyRule> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}