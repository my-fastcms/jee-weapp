package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.BuyerProd;
import com.dbumama.market.service.api.BuyerProdItemResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface BuyerProdService  {

	public void add(Long buyerId, Long productId) throws WxmallBaseException;
	
	public List<BuyerProdItemResultDto> getProdByBuyer(Long buyerId) throws WxmallBaseException;
	
	public Long getProdCountByBuyer(Long buyerId) throws WxmallBaseException;
	
	/**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public BuyerProd findById(Object id);


    /**
     * find all model
     *
     * @return all <BuyerProd
     */
    public List<BuyerProd> findAll();


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
    public boolean delete(BuyerProd model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(BuyerProd model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(BuyerProd model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(BuyerProd model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<BuyerProd> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<BuyerProd> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<BuyerProd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}