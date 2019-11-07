package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthWeimo;
import io.jboot.db.model.Columns;

import java.util.List;

public interface AuthWeimoService  {

	AuthWeimo findByPid(String pid);
	
	Page<AuthWeimo> list(Long sellerId, Long appId, Integer pageNo, Integer pageSize);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthWeimo findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthWeimo
     */
    public List<AuthWeimo> findAll();


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
    public boolean delete(AuthWeimo model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthWeimo model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthWeimo model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthWeimo model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthWeimo> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthWeimo> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthWeimo> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}