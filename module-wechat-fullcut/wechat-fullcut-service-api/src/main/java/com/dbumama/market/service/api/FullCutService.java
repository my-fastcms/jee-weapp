package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.FullCut;
import com.dbumama.market.model.FullCutSet;
import com.dbumama.market.model.Product;

import io.jboot.db.model.Columns;

import java.util.List;

public interface FullCutService  {

	public FullCut save(FullCutParamDto paramDto)throws UmpException;
	
	public Page<ProductResultDto> getProducts4FullCutPage(ProductParamDto productParamDto) throws ProductException;
    
	public Page<FullCut> list(FullcutPageParamDto promotionParam) throws UmpException;
	
	public List<ProdFullCutResultDto> getProductFullCut(Product product) throws UmpException;
	
	List<FullCutSet> getFullCutSetsByFullCut(Long fullCutId); 
	
	FullCutResultDto getFullCutInfo(Long id);
	
	public List<Record> getFullCutMini(Long authUserId,
                                       int pageNo, int pageSize);
	
	/**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FullCut findById(Object id);


    /**
     * find all model
     *
     * @return all <FullCut
     */
    public List<FullCut> findAll();


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
    public boolean delete(FullCut model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(FullCut model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(FullCut model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FullCut model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<FullCut> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<FullCut> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<FullCut> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}