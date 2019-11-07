package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthShopOrder;
import io.jboot.db.model.Columns;

import java.util.List;

public interface AuthShopOrderService  {
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthShopOrder findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthShopOrder
     */
    public List<AuthShopOrder> findAll();


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
    public boolean delete(AuthShopOrder model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthShopOrder model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthShopOrder model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthShopOrder model);

	/**
	 * @param columns
	 * @return
	 */
	public List<AuthShopOrder> findByColumns(Columns columns);

    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthShopOrder> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthShopOrder> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthShopOrder> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}