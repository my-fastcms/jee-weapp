package com.dbumama.market.service.api;

import java.util.Date;
import java.util.List;

import com.dbumama.market.model.PaymentRecord;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface PaymentRecordService  {
	
	public Page<PaymentRecordResDto> list(Long appId, String payee_name, Integer status, int pageNo, int pageSize, Date start_date,
			Date end_date);
	
	public void pay(Long appId,Long payee_id, String explain, String payment_money,String payee_name, String phone_code, String phone);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public PaymentRecord findById(Object id);


    /**
     * find all model
     *
     * @return all <PaymentRecord
     */
    public List<PaymentRecord> findAll();


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
    public boolean delete(PaymentRecord model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(PaymentRecord model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(PaymentRecord model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(PaymentRecord model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<PaymentRecord> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<PaymentRecord> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<PaymentRecord> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}