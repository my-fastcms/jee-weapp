package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuReplyRcd;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuReplyRcdService  {
	
	MenuReplyRcd findRcdByOpenId(Long replyCfigId, String openId);
	
	List<MenuReplyRcd> findReplRcdsByCfgId(Long configId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuReplyRcd findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuReplyRcd
     */
    public List<MenuReplyRcd> findAll();


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
    public boolean delete(MenuReplyRcd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuReplyRcd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuReplyRcd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuReplyRcd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuReplyRcd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuReplyRcd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuReplyRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}