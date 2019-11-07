package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.SelleruserBalanceRcd;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SelleruserBalanceRcdService  {
	
	Page<SelleruserBalanceRcd> list(Long sellerId, Integer pageNo, Integer pageSize);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SelleruserBalanceRcd findById(Object id);


    /**
     * find all model
     *
     * @return all <SelleruserBalanceRcd
     */
    public List<SelleruserBalanceRcd> findAll();


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
    public boolean delete(SelleruserBalanceRcd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(SelleruserBalanceRcd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(SelleruserBalanceRcd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SelleruserBalanceRcd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<SelleruserBalanceRcd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<SelleruserBalanceRcd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<SelleruserBalanceRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}