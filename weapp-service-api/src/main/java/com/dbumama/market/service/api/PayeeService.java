package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.Payee;
import io.jboot.db.model.Columns;
import java.util.List;

public interface PayeeService  {
	
	public Payee findByOpenId(Long appId, String openId);
	
	public void updatePayee(String payeeList);
	
	public void deletePayee(Long id);
	
	public List<Record> list(Long appId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Payee findById(Object id);


    /**
     * find all model
     *
     * @return all <Payee
     */
    public List<Payee> findAll();


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
    public boolean delete(Payee model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Payee model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Payee model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Payee model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Payee> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Payee> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Payee> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}