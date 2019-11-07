package com.dbumama.market.service.api;

import com.dbumama.market.model.*;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.List;

public interface CashbackService  {
	
	public void save(Cashback cashback, String productIds, Long appId) throws UmpException;
	
	public Page<CashbackResultDto> list(CashbackParamDto promotionParam) throws UmpException;
	
	public Page<ProductResultDto> getProducts4CashbackPage(ProductParamDto productParamDto) throws ProductException;
	
	public Cashback getProductCashSet(ProductParamDto productParamDto) throws ProductException;
	
	public ProdCashbackResultDto getProductCashBack(Product product) throws ProductException;
	
	/**
	 * 给用户返现
	 * @param buyer
	 * @param order
	 * @param product
	 * @param cashback
	 * @throws UmpException
	 */
	public BigDecimal cash2Buyer(BuyerUser buyer, Order order, List<OrderItem> orderItems, Product product, ProdCashbackResultDto cashback) throws UmpException;
	
	List<CashbackProduct> getCashbackProducts(Long cashbackId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Cashback findById(Object id);


    /**
     * find all model
     *
     * @return all <Cashback
     */
    public List<Cashback> findAll();


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
    public boolean delete(Cashback model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Cashback model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Cashback model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Cashback model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Cashback> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Cashback> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Cashback> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}