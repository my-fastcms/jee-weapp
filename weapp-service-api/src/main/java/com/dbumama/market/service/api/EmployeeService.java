package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.Employee;

import io.jboot.db.model.Columns;

import java.util.List;

public interface EmployeeService  {
	
	Employee findBySellerAndAuthUser(Long sellerId, Long appId);
	
	List<Employee> findBySeller(Long sellerId, Long appId);
	
	Page<Record> list(Long sellerId, Long appId, Long roleId, Integer active, Integer pageNo, Integer pageSize);
	
	public void save(Long sellerId, Long appId, Long id, String phone, String name, Long roleId, Integer active) throws WxmallBaseException;


    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Employee findById(Object id);


    /**
     * find all model
     *
     * @return all <Employee
     */
    public List<Employee> findAll();


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
    public boolean delete(Employee model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Employee model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Employee model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Employee model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Employee> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Employee> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Employee> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}