package com.dbumama.market.service.api;

import java.util.List;
import java.util.Map;

import com.dbumama.market.model.Menu;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface MenuService  {

	public static final String FWH_MENU_KEY = "fwh";
	public static final String DYH_MENU_KEY = "dyh";
	public static final String XCX_MENU_KEY = "xcx";

	//List<MenuItem> getMenus(int authUserType);

	List<MenuItem> getDyMenus();
	List<MenuItem> getFwMenus();
	List<MenuItem> getXcxMenus();
	
	public Menu findByIdCache(Object id);
	
	/**
	 * @param sellerId
	 * @param authUserId
	 * @param menuId
	 * @return
	 */
	List<Menu> getChildren(Long sellerId, Long authUserId, Long menuId);
	
	/**
	 * @param sellerId 登陆用户
	 * @param authUserId 授权公众号或小程序
	 * @param appId 使用的应用
	 * @return
	 */
	List<Menu> getMenus(Long sellerId, Long authUserId, Long appId);
	
	/**
	 * app关联的父菜单
	 * @param appId
	 * @return
	 */
	Menu getMenuByApp(Long appId);
	
	List<JsTreeResultDto> getJsTreeMenus(Long roleId, Boolean readonly);
	
	List<JsTreeResultDto> getJsTreeChildren(Long menuId, Long roleId, Boolean readonly);
	
	/**
	 * 获取用户所有角色对应的菜单，功能权限
	 * 小程序 + 用户作为key  权限集合为值
	 * 
	 * shiro使用
	 * @param sellerId
	 * @return
	 */
	/*HashMap<String, List<Menu>> getMenusPerms(Long sellerId);*/
	
	/**
	 * 获取当前小程序所有菜单，功能权限
	 * @param sellerId
	 * @param appId
	 * @return
	 */
	List<Menu> getMenusPerms(Long sellerId, Long appId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Menu findById(Object id);


    /**
     * find all model
     *
     * @return all <Menu
     */
    public List<Menu> findAll();


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
    public boolean delete(Menu model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Menu model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Menu model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Menu model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Menu> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Menu> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Menu> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}