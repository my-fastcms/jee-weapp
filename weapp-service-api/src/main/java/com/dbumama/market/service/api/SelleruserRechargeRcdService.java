package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.SelleruserRechargeRcd;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SelleruserRechargeRcdService  {
	
	SelleruserRechargeRcd findByTradeNo(String tradeNo);
	
	Page<SelleruserRechargeRcd> list(Long sellerId, Integer pageNo, Integer pageSize);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SelleruserRechargeRcd findById(Object id);


    /**
     * find all model
     *
     * @return all <SelleruserRechargeRcd
     */
    public List<SelleruserRechargeRcd> findAll();


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
    public boolean delete(SelleruserRechargeRcd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(SelleruserRechargeRcd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(SelleruserRechargeRcd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SelleruserRechargeRcd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<SelleruserRechargeRcd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<SelleruserRechargeRcd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<SelleruserRechargeRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}