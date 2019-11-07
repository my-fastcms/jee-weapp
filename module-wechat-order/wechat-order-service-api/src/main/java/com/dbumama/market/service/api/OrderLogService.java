package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.OrderLog;
import io.jboot.db.model.Columns;

import java.util.List;

public interface OrderLogService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public OrderLog findById(Object id);


    /**
     * find all model
     *
     * @return all <OrderLog
     */
    public List<OrderLog> findAll();


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
    public boolean delete(OrderLog model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(OrderLog model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(OrderLog model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(OrderLog model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<OrderLog> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<OrderLog> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<OrderLog> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}