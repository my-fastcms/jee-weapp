package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.model.WeappTemplate;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface WeappTemplateService  {
	
	WeappTemplate findByTemplateId(Long templateId);
	
	List<WeappTemplate> findList();

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public WeappTemplate findById(Object id);


    /**
     * find all model
     *
     * @return all <WeappTemplate
     */
    public List<WeappTemplate> findAll();


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
    public boolean delete(WeappTemplate model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(WeappTemplate model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(WeappTemplate model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(WeappTemplate model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<WeappTemplate> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<WeappTemplate> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<WeappTemplate> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}