package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Menu;
import com.dbumama.market.model.Role;

import io.jboot.db.model.Columns;

import java.util.List;

public interface RoleService  {
	
	/**
	 * 获取用户拥有的所有角色（权限分配，一个授权小程序对应一个角色，分配有多个小程序的话，会有多个角色）
	 * @param sellerId
	 * @return
	 */
	Role getRoleBySeller(Long sellerId, Long appId);
	
	/**
	 * 获取角色菜单权限
	 * @param roleId
	 * @return
	 */
	List<Menu> getPermissionByRole(Long roleId, Long appId);
	
	/**
	 * 获取角色子菜单权限
	 * @param roleId
	 * @return
	 */
	List<Menu> getPermissionChildrenByRole(Long roleId, Long menuId);
	
	/**
	 * 获取当前用户下可用的角色列表（用户创建的角色，不是分配的有权限的角色）
	 * @param sellerId
	 * @return
	 */
	List<Role> getRolesBySeller(Long sellerId);
	
	Page<Role> list(Long sellerId, Integer pageNo, Integer pageSize, Integer active);
	
	public void save(Long sellerId, Long appId, Long id, String roleName, String roleDesc, String menuids, Integer active) throws WxmallBaseException;
	

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Role findById(Object id);


    /**
     * find all model
     *
     * @return all <Role
     */
    public List<Role> findAll();


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
    public boolean delete(Role model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Role model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Role model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Role model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Role> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Role> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Role> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}