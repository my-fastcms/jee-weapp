package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.service.api.CustomerException;
import com.dbumama.market.service.api.CustomerParamDto;
import com.dbumama.market.service.api.MemberResultDto;
import com.dbumama.market.service.api.UserException;
import com.dbumama.market.service.api.WeappLoginResultDto;
import com.dbumama.market.service.api.WeappUserCheckParamDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface BuyerUserService  {
	
	public BuyerUser getUser(Long appId, String openId);
	
	/**
	 * 小程序登陆微信服务器，获取用户详细信息，包括登陆用户的openid
	 * @param appId
	 * @param code
	 * @return
	 * @throws UserException
	 */
	public WeappLoginResultDto loginWeapp (String appId, String code) throws UserException;
	
	/**
	 * 小程序用户校验与保存
	 * @param userCheckParam
	 * @throws UserException
	 */
	public BuyerUser check(WeappUserCheckParamDto userCheckParam) throws UserException;
	
	BuyerUser findByOpenId(String openId);
	
    public BuyerUser findByIdLock(Object id);
	
	List<BuyerUser> findByNick(String nick);
	
	/**
	 * 
	 * @param openId
	 * @param appId
	 * @param flag	关注事件标志
	 * @return
	 * @throws UserException
	 */
	BuyerUser saveOrUpdate(String openId, String appId, String userInfoJson, String accessIp) throws UserException;
	
	/**
	 * 查询获取所有客户，包括会员
	 * @param customerParam
	 * @return
	 * @throws CustomerException
	 */
	public Page<BuyerUser> list(CustomerParamDto customerParam, String appId, Integer pageNo) throws CustomerException;
	
	/**
	 * 只查询出会员数据，领取过微信会员卡的用户列表，并且会员卡未过期
	 * @return
	 * @throws CustomerException
	 */
	public Page<MemberResultDto> listMembers(CustomerParamDto customerParam) throws CustomerException;
	
	public Long getCount();

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public BuyerUser findById(Object id);


    /**
     * find all model
     *
     * @return all <BuyerUser
     */
    public List<BuyerUser> findAll();


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
    public boolean delete(BuyerUser model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(BuyerUser model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(BuyerUser model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(BuyerUser model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<BuyerUser> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<BuyerUser> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<BuyerUser> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}