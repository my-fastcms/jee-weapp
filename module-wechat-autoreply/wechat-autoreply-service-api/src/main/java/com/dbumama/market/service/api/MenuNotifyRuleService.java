package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuNotifyRule;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuNotifyRuleService  {
	
	MenuNotifyRule findByMenuKey(Long shopId, String menuKey);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuNotifyRule findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuNotifyRule
     */
    public List<MenuNotifyRule> findAll();


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
    public boolean delete(MenuNotifyRule model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuNotifyRule model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuNotifyRule model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuNotifyRule model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuNotifyRule> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuNotifyRule> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuNotifyRule> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}