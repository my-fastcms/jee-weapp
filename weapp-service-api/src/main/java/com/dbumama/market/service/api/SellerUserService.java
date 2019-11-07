package com.dbumama.market.service.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.SellerUser;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.List;

public interface SellerUserService  {
	
	SellerUser findByIdLock(Long id);
	SellerUser findByPhone(String phone);
	SellerUser findByOpenid(String openid);
	SellerUser findByWeimoPid(String pid);
	SellerUser findByKdtId(String kdtid, String phone); 
	
	Page<SellerUser> list(Long sellerId, Integer pageNo, Integer pageSize);
	Page<SellerUser> list(String phone, Integer active, Integer pageNo, Integer pageSize);
	Long getCount();
	void register(final String phone, final String password, final String confirmPwd, final String captchaToken, final String code, final String incode) throws WxmallBaseException;
	void resetPwd(final String phone, final String password, final String confirmPwd, final String captchaToken, final String code) throws WxmallBaseException;
	SellerUser getSellerByIncode(String incode);
	Page<SellerUser> getPageSellerByIncode(Integer pageNo, Integer pageSize, String shareCode, String phone);
	
	ApiResult getUserInfoByScence(String scenceId);
	
	/**
	 * 获取用户账户余额
	 * @param sellerId
	 * @return
	 */
	BigDecimal getBalance(Long sellerId, Long appId);
	
	/**
	 * 处理用户扫描二维码登录事件
	 * @param openId
	 * @param eventKey
	 * @throws UserException
	 */
	void processInQrCode(String openId, String eventKey, String accessToken) throws UserException;
	
//	/**
//	 * 处理用户扫码登录过程中，关注公众号事件
//	 * @param openId
//	 * @throws UserException
//	 */
//	void processInFollow(String openId, AuthUser authUser) throws UserException;
	
	/**
	 * 设置登录密码
	 * @param sellerId
	 * @param code
	 * @param password
	 * @param confirmPwd
	 * @return
	 * @throws UserException
	 */
	SellerUser setPwd(final Long sellerId, final String code, final String password, final String confirmPwd) throws UserException;
	
	/**
	 * 设置交易密码
	 * @param sellerId
	 * @param code
	 * @param password
	 * @param confirmPwd
	 * @throws UserException
	 */
	SellerUser setTradePwd(final Long sellerId, final String code, final String password, final String confirmPwd) throws UserException;
	
	/**
	 * 微信扫码登录之后，绑定用户手机号码
	 * @param sellerId
	 * @param phone
	 * @param code
	 * @param password
	 * @param confirmPwd
	 * @return
	 * @throws UserException
	 */
	SellerUser setUserPhone(final Long sellerId, final String phone, final String code, final String password, final String confirmPwd) throws UserException;


    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SellerUser findById(Object id);


    /**
     * find all model
     *
     * @return all <SellerUser
     */
    public List<SellerUser> findAll();


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
    public boolean delete(SellerUser model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(SellerUser model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(SellerUser model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SellerUser model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<SellerUser> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<SellerUser> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<SellerUser> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}