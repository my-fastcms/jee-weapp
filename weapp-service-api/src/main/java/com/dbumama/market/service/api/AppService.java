package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.App;
import com.dbumama.market.model.AuthUser;

import io.jboot.db.model.Columns;

import java.util.List;

public interface AppService  {
	
	public void save(Long id, Integer app_category, Integer app_type, String app_image, String app_name, 
							String imgList,String app_desc,String app_content);
	
	List<App> findApps();
	
	List<App> findApps(AuthUser authUser);
	
	List<App> hotApps();
	
	List<App> newApps();

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public App findById(Object id);


    /**
     * find all model
     *
     * @return all <App
     */
    public List<App> findAll();


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
    public boolean delete(App model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(App model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(App model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(App model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<App> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<App> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<App> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}