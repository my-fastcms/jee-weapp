package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.WeappStyleTabbarItem;
import io.jboot.db.model.Columns;

import java.util.List;

public interface WeappStyleTabbarItemService  {
	
	List<WeappStyleTabbarItem> getStyleItems(Long styleId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public WeappStyleTabbarItem findById(Object id);


    /**
     * find all model
     *
     * @return all <WeappStyleTabbarItem
     */
    public List<WeappStyleTabbarItem> findAll();


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
    public boolean delete(WeappStyleTabbarItem model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(WeappStyleTabbarItem model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(WeappStyleTabbarItem model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(WeappStyleTabbarItem model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<WeappStyleTabbarItem> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<WeappStyleTabbarItem> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<WeappStyleTabbarItem> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}