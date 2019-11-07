package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.model.SellerAddr;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface SellerAddrService  {

	SellerAddr getSendAddr(Long sellerId);
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SellerAddr findById(Object id);


    /**
     * find all model
     *
     * @return all <SellerAddr
     */
    public List<SellerAddr> findAll();


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
    public boolean delete(SellerAddr model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(SellerAddr model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(SellerAddr model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SellerAddr model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<SellerAddr> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<SellerAddr> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<SellerAddr> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}