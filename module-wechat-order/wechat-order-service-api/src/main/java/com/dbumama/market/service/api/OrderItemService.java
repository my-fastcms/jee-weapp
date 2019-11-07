package com.dbumama.market.service.api;

import com.dbumama.market.model.OrderItem;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface OrderItemService  {

    /**
     * 查询该用户已经购买了多少件该商品
     * @param id
     * @param authUserId
     * @param pId
     * @return
     */
	Integer getPurchaseCount(Long id, Long authUserId, Long pId);
	
	List<OrderItem> getOrderItems(Long orderId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public OrderItem findById(Object id);


    /**
     * find all model
     *
     * @return all <OrderItem
     */
    public List<OrderItem> findAll();


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
    public boolean delete(OrderItem model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(OrderItem model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(OrderItem model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(OrderItem model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<OrderItem> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<OrderItem> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<OrderItem> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}