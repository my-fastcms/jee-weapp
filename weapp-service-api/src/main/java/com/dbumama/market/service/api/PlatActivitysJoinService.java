package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.PlatActivitysJoin;
import com.dbumama.market.model.SellerUser;

import io.jboot.db.model.Columns;

import java.util.List;

public interface PlatActivitysJoinService  {
	
	public void joinUser(AuthUser authUser, SellerUser sellerUser, Long activityId);
	
	public void addPhone(SellerUser sellerUser, String phone, String phoneCode);
	
	public PlatActivitysJoin findIsJoin(Long  appId, Long activityId);
	
	public Page<Record> lookList(Long activityId, String nickName, int pageNo, int pageSize);
	
	public void stopAct(Long activityId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public PlatActivitysJoin findById(Object id);


    /**
     * find all model
     *
     * @return all <PlatActivitysJoin
     */
    public List<PlatActivitysJoin> findAll();


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
    public boolean delete(PlatActivitysJoin model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(PlatActivitysJoin model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(PlatActivitysJoin model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(PlatActivitysJoin model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<PlatActivitysJoin> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<PlatActivitysJoin> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<PlatActivitysJoin> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}