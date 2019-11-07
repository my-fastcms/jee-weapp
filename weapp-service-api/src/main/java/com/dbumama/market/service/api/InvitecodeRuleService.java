package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.InvitecodeRule;
import com.dbumama.market.model.SellerUser;

import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.List;

public interface InvitecodeRuleService  {
	
	InvitecodeRule findRule();
	
	String genInCode(SellerUser sellerUser) throws Exception;
	
	/**
	 * 获取通过邀请码赚取的累计佣金
	 * @param sellerId
	 * @return
	 * @throws WxmallBaseException
	 */
	BigDecimal getCodeCash(Long sellerId) throws WxmallBaseException;
	
	/**
	 * 获取用户可提现佣金
	 * @param sellerId
	 * @return
	 */
	BigDecimal getCodeCangetCash(Long sellerId);

	/**
	 * @param sellerId	提现用户
	 * @param tradePwd	交易密码
	 * @param wantCash	需要提现金额
	 * @throws WxmallBaseException
	 */
	void applycash(Long sellerId, String tradePwd, String wantCash, String cashAccount) throws WxmallBaseException;
	
	/**
	 * 系统管理后台统计有邀请码的用户
	 * @param pageNo
	 * @param pageSize
	 * @param phone
	 * @return
	 */
	Page<Record> getPlatIncodeUser(Integer pageNo, Integer pageSize, String phone);
	
	/**
	 * 系统管理后台查询申请提现的记录
	 * @param pageNo
	 * @param pageSize
	 * @param phone
	 * @return
	 */
	Page<Record> getPlatCashRcd(Integer pageNo, Integer pageSize, String phone, Integer status);
	
	/**
	 * 获取当前用户的提现明细记录
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @return
	 */
	Page<Record> getUserCashRcd(Integer pageNo, Integer pageSize, Long sellerId, Integer status);
	
	void confirmCash(Long cashId) throws WxmallBaseException;
	
	void cancelCash(Long cashId, String reason) throws WxmallBaseException;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public InvitecodeRule findById(Object id);


    /**
     * find all model
     *
     * @return all <InvitecodeRule
     */
    public List<InvitecodeRule> findAll();


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
    public boolean delete(InvitecodeRule model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(InvitecodeRule model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(InvitecodeRule model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(InvitecodeRule model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<InvitecodeRule> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<InvitecodeRule> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<InvitecodeRule> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}