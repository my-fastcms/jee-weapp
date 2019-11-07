package com.dbumama.market.service.api;

import com.dbumama.market.model.InvoiceTemplate;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;
import java.util.Map;

public interface InvoiceTemplateService  {
	
	InvoiceTemplate save(InvTplSaveParamDto tplParamDto) throws OrderException;
	InvoiceTemplate getUserTpl(Long appId) throws OrderException;
	Map<String, List<PalletElementResultDto>> initPalletElement (Long appId) throws OrderException;
	Map<String, List<PalletElementResultDto>> initTableColumnElement (Long appId) throws OrderException;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public InvoiceTemplate findById(Object id);


    /**
     * find all model
     *
     * @return all <InvoiceTemplate
     */
    public List<InvoiceTemplate> findAll();


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
    public boolean delete(InvoiceTemplate model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(InvoiceTemplate model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(InvoiceTemplate model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(InvoiceTemplate model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<InvoiceTemplate> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<InvoiceTemplate> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<InvoiceTemplate> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}