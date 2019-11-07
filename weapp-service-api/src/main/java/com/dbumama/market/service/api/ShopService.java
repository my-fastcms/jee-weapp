package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Shop;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ShopService  {
	
	/**
	 * 获取该公众号的门店信息列表
	 * @param buyerId
	 * @return
	 */
	List<Shop> getShopByAppId(Long appId);
	
	Shop findByApp(Long appId);
	
	Page<Shop> list(Long appId, Integer pageNo, Integer pageSize, Integer active);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Shop findById(Object id);


    /**
     * find all model
     *
     * @return all <Shop
     */
    public List<Shop> findAll();


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
    public boolean delete(Shop model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Shop model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Shop model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Shop model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Shop> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Shop> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Shop> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}