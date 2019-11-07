package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.WxamsgTemplate;
import io.jboot.db.model.Columns;

import java.util.List;

public interface WxamsgTemplateService  {
	
	void synOnline(Long appId);

	List<WxamsgTemplate> findByAppId(Long appId);
	
	String getTemplateId(Long appId, Integer msgType);
	
	WxamsgTemplate getTemplate(Long appId, Integer msgType);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public WxamsgTemplate findById(Object id);


    /**
     * find all model
     *
     * @return all <WxamsgTemplate
     */
    public List<WxamsgTemplate> findAll();


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
    public boolean delete(WxamsgTemplate model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(WxamsgTemplate model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(WxamsgTemplate model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(WxamsgTemplate model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<WxamsgTemplate> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<WxamsgTemplate> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<WxamsgTemplate> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}