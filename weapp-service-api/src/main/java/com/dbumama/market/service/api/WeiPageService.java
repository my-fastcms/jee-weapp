package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.WeiPage;
import com.dbumama.market.model.WeipageCategory;
import com.dbumama.market.service.api.UmpException;

import io.jboot.db.model.Columns;

import java.io.IOException;
import java.util.List;

public interface WeiPageService  {
	
	public WeiPage getWeiPageById(Long id) throws UmpException;
	
	public String getContentById(Long id)throws UmpException;
	public WeiPage getWeiPageInfoById(Long id) throws UmpException; 
	
	public Page<WeiPage> list(Long sellerId,WeiPage weipage, Integer pageNo, Integer pageSize);
	
//	public boolean  update(WeiPage  weipage) throws UmpException;
//	public boolean  save(WeiPage  weipage) throws UmpException;
	
	public String getPageHtml(WeiPage entity, boolean publish) throws UmpException , IOException;
	
	void deleteById(Long weipageId);
	
	WeiPage findIndex(Long sellerId);
	
	public List<WeiPage> getSellerWeipage(Long sellerId);
	
	//weipage category api
	List<WeipageCategory> getSellerWeipageCategory(Long sellerId);
	WeipageCategory findCategoryById(Long categoryId);
	void deleteCategoryById(Long categoryId);
	Page<WeipageCategory> pageCategory(Long sellerId, Integer pageNo, Integer pageSize);
	Long save(WeipageCategory category);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public WeiPage findById(Object id);


    /**
     * find all model
     *
     * @return all <WeiPage
     */
    public List<WeiPage> findAll();


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
    public boolean delete(WeiPage model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(WeiPage model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(WeiPage model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(WeiPage model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<WeiPage> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<WeiPage> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<WeiPage> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}