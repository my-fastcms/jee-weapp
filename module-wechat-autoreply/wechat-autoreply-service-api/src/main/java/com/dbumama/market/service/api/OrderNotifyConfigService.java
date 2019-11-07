package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.OrderNotifyConfig;
import io.jboot.db.model.Columns;

import java.util.List;

public interface OrderNotifyConfigService  {
	
	OrderNotifyConfig findByConfigType(Long appId, String notifyType);
	
	OrderNotifyConfig save(OrderNotifyConfig config, String notifyers) throws Exception;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public OrderNotifyConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <OrderNotifyConfig
     */
    public List<OrderNotifyConfig> findAll();


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
    public boolean delete(OrderNotifyConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(OrderNotifyConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(OrderNotifyConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(OrderNotifyConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<OrderNotifyConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<OrderNotifyConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<OrderNotifyConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}