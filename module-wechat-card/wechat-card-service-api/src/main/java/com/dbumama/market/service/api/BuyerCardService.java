package com.dbumama.market.service.api;

import com.dbumama.market.model.BuyerCard;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface BuyerCardService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public BuyerCard findById(Object id);


    /**
     * find all model
     *
     * @return all <BuyerCard
     */
    public List<BuyerCard> findAll();


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
    public boolean delete(BuyerCard model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(BuyerCard model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(BuyerCard model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(BuyerCard model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<BuyerCard> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<BuyerCard> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<BuyerCard> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}