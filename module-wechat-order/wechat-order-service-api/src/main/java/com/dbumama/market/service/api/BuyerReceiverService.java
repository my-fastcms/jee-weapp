package com.dbumama.market.service.api;

import com.dbumama.market.model.BuyerReceiver;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface BuyerReceiverService  {
	
	/**
	 * 获取买家收货地址
	 * @param buyerId
	 * @return
	 */
	List<BuyerReceiver> getBuyerReceiver(Long buyerId);
	
	/**
	 * 保存收货地址
	 * @param submitParam
	 * @throws UserException
	 */
	BuyerReceiver save(BuyerReceiverSubmitParamDto submitParam) throws UserException;
	
	/**
	 * 获取用户默认收获地址
	 * @param buyerId
	 * @return
	 */
	BuyerReceiver getDefaultReceiver(Long buyerId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public BuyerReceiver findById(Object id);


    /**
     * find all model
     *
     * @return all <BuyerReceiver
     */
    public List<BuyerReceiver> findAll();


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
    public boolean delete(BuyerReceiver model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(BuyerReceiver model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(BuyerReceiver model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(BuyerReceiver model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<BuyerReceiver> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<BuyerReceiver> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<BuyerReceiver> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}