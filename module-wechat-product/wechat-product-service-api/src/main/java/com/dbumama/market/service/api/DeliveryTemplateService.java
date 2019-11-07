package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.DeliverySet;
import com.dbumama.market.model.DeliveryTemplate;
import com.dbumama.market.service.api.DeliveryTemplateEditResultDto;
import com.dbumama.market.service.api.DeliveryTemplateException;
import com.dbumama.market.service.api.DeliveryTemplateResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface DeliveryTemplateService  {
	
	public DeliveryTemplate save(DeliveryTemplate dt, String items) throws DeliveryTemplateException;

	List<DeliverySet> getDeliverySetByTpl(Long templateId);
	  
	Page<DeliveryTemplateResultDto> list(Long appId, Integer pageNo, Integer pageSize,Integer active);
	  
	List<DeliveryTemplate> getDelivTemplateByApp(Long appId);
	
	DeliveryTemplateEditResultDto findByTemplateId(Long templateId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public DeliveryTemplate findById(Object id);


    /**
     * find all model
     *
     * @return all <DeliveryTemplate
     */
    public List<DeliveryTemplate> findAll();


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
    public boolean delete(DeliveryTemplate model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(DeliveryTemplate model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(DeliveryTemplate model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(DeliveryTemplate model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<DeliveryTemplate> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<DeliveryTemplate> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<DeliveryTemplate> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}