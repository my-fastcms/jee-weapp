package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuNotifyConfig;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuNotifyConfigService  {
	
	List<MenuNotifyConfig> findByMenuKey(Long shopId, String menuKey);
	
	List<MenuNotifyConfig> findByOpenId(Long shopId, String openId);
	
	void save(Long shopId, String menuKey, String openids, String menuNotifyConfig) throws Exception;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuNotifyConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuNotifyConfig
     */
    public List<MenuNotifyConfig> findAll();


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
    public boolean delete(MenuNotifyConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuNotifyConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuNotifyConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuNotifyConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuNotifyConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuNotifyConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuNotifyConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}