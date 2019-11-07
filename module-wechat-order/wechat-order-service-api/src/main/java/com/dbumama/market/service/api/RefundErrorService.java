package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.RefundError;
import io.jboot.db.model.Columns;

import java.util.List;

public interface RefundErrorService  {
	
	public RefundError findByOrderId(Long orderId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public RefundError findById(Object id);


    /**
     * find all model
     *
     * @return all <RefundError
     */
    public List<RefundError> findAll();


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
    public boolean delete(RefundError model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(RefundError model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(RefundError model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(RefundError model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<RefundError> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<RefundError> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<RefundError> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}