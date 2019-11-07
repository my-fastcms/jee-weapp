package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.PlatUser;
import io.jboot.db.model.Columns;

import java.util.List;

public interface PlatUserService  {
	
	PlatUser findByAccount(final String account);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public PlatUser findById(Object id);


    /**
     * find all model
     *
     * @return all <PlatUser
     */
    public List<PlatUser> findAll();


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
    public boolean delete(PlatUser model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(PlatUser model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(PlatUser model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(PlatUser model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<PlatUser> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<PlatUser> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<PlatUser> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}