package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.CompTicket;
import io.jboot.db.model.Columns;

import java.util.List;

public interface CompTicketService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public CompTicket findById(Object id);


    /**
     * find all model
     *
     * @return all <CompTicket
     */
    public List<CompTicket> findAll();


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
    public boolean delete(CompTicket model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(CompTicket model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(CompTicket model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(CompTicket model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<CompTicket> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<CompTicket> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<CompTicket> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}