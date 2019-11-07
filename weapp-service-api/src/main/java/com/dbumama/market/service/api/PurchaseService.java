package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Purchase;
import com.dbumama.market.model.PurchaseOrder;
import com.dbumama.market.service.api.PurchaseOrderResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface PurchaseService  {

	public Boolean whetherPay(Long appId,Long authUserId);

	List<Purchase> find();
	PurchaseOrder getPurchaseOrder(String outTradeNo);
	
	String createOrder(Long sellerId, Long porderId, String payType, Long purchaseId, Long appId) throws WxmallBaseException;
	
	public List<Purchase> findByAppId(Object id);

	/**
	 * 订单支付完成后回调方法
	 * @param outTradeNo
	 */
	void callBack(String outTradeNo, String tradeNo);
	
	/**
	 * 获取用户未支付订单，根据模板来
	 * @param sellerId
	 * @return
	 */
	List<PurchaseOrderResultDto> getUnpayPurchaseOrdersBySellerAndModule(Long sellerId);
	
	/**
	 * 获取当前模块用户未支付订单
	 * @param sellerId
	 * @param moduleId
	 * @return
	 */
	PurchaseOrder getUnpayPurchaseOrderByModule(Long sellerId, Long appId);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Purchase findById(Object id);


    /**
     * find all model
     *
     * @return all <Purchase
     */
    public List<Purchase> findAll();


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
    public boolean delete(Purchase model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Purchase model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Purchase model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Purchase model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Purchase> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Purchase> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Purchase> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}