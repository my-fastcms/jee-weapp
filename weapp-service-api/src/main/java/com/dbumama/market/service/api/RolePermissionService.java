package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.RolePermission;
import io.jboot.db.model.Columns;

import java.util.List;

public interface RolePermissionService  {
	
	List<RolePermission> getPermissionByRole(Long roleId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public RolePermission findById(Object id);


    /**
     * find all model
     *
     * @return all <RolePermission
     */
    public List<RolePermission> findAll();


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
    public boolean delete(RolePermission model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(RolePermission model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(RolePermission model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(RolePermission model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<RolePermission> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<RolePermission> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<RolePermission> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}