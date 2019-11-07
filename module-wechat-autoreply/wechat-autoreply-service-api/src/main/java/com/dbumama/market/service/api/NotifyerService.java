package com.dbumama.market.service.api;

import com.dbumama.market.model.Notifyer;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

import java.util.List;

public interface NotifyerService  {
	
	List<Notifyer> findAllTimeoutNotifyers();
	
	Notifyer findByOpenId(Long shopId, String openId);
	
	Notifyer findByOpenId(Long shopId, String openId, Integer active);
	
	List<Notifyer> findByShop(Long shopId);
	
	List<NotifyerResDto> findByMenuKey(Long shopId, String menuKey);
	
	List<NotifyerResDto> findByOrderNotifyConfig(Long shopId, Long configId);
	
	List<NotifyerResDto> findByProductNotifyConfig(Long shopId, Long configId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Notifyer findById(Object id);


    /**
     * find all model
     *
     * @return all <Notifyer
     */
    public List<Notifyer> findAll();


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
    public boolean delete(Notifyer model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Notifyer model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Notifyer model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Notifyer model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Notifyer> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Notifyer> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Notifyer> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}