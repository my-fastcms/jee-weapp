package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuNotifyRcd;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuNotifyRcdService  {
	
	MenuNotifyRcd findByNotifyer(Long notifyerId, String openId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuNotifyRcd findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuNotifyRcd
     */
    public List<MenuNotifyRcd> findAll();


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
    public boolean delete(MenuNotifyRcd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuNotifyRcd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuNotifyRcd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuNotifyRcd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuNotifyRcd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuNotifyRcd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuNotifyRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}