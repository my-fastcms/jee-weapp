package com.dbumama.market.service.api;

import java.io.File;
import java.util.List;

import com.dbumama.market.model.AuthCert;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface AuthCertService  {
	
	public void startUse(Long id, String appId);
	
	public void UpdateDefault(Long id, String pay_mch_id, String pay_secret_key, File file);
	
	public void saveOrUpdate(Long id,String appId, String pay_mch_id, String pay_secret_key,File File);
	
	/**
	 * @Title: findUse
	 * @Description: 查询商家使用的支付配置
	 */
	public AuthCert findUse(String appId);
	
	/**
	* @Title: findDefault
	* @Description: 查询默认的支付配置
	 */
	public AuthCert findDefault();
	
	public List<AuthCert> findByAppId(String appId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthCert findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthCert
     */
    public List<AuthCert> findAll();


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
    public boolean delete(AuthCert model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthCert model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthCert model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthCert model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthCert> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthCert> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthCert> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}