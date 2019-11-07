package com.dbumama.market.service.api;

import com.dbumama.market.model.*;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface AuthUserService  {
	
	public AuthUser findByAppId(String appId);
	
	public AuthUser findByAppName(String appName);

	public AuthUser bind(Long sellerId, String code) throws WxmallBaseException;
	
	public AuthUser getAuthUserByAppId(String appId);
	
	public AuthUser getAuthUserByUserName(String userName);
	
	List<WeappAudit> getWeappAduitsByAppId(String appId);
	
	public Page<AuthUser> list(AuthUserParamDto userParam);
	
	public Page<AuthUser> list(Integer pageNo, Integer pageSize, Integer active, String nickName) throws WxmallBaseException;
	
	String getAccessToken(AuthUser authUser);
	
	String refreshAccessToken(AuthUser authUser);
	
	Boolean checkAccessToken(AuthUser authUser);
	
	String getCompAccessToken();
	
	public List<AuthUser> getSellerAuthUser(Long sellerId);
	
	public List<AuthUser> getSellerAllAuthUsers(Long sellerId);
	
	public List<AuthUser> hotAuthUsers();
	
	/**
	 * 获取支付证书
	 * @param authUser
	 * @return
	 */
	AuthCert getAuthCert(AuthUser authUser);
	
	/**
	 * 提交到微信审核小程序
	 * @param authAppId
	 * @throws WxmallBaseException
	 */
	public void commitAudit(Long authAppId, String itemList) throws WxmallBaseException;
	
	/**
	 * 上传小程序客户端代码
	 * @param authAppId
	 * @param templateId
	 * @throws WxmallBaseException
	 */
	public void uploadWeappCode(Long authAppId, Integer templateId, Integer styleId) throws WxmallBaseException;
	
	/**
	 * 绑定小程序体验者
	 * @param testUser
	 * @throws WxmallBaseException
	 */
	public void bindtest(Long authAppId, String testUser) throws WxmallBaseException;
	
	/**
	 * 解绑小程序体验者
	 * @param testUser
	 * @throws WxmallBaseException
	 */
	public void unbindtest(Long authAppId, String testUser) throws WxmallBaseException;
	
	/**
	 * 列出小程序体验者
	 * @param authAppId
	 * @throws WxmallBaseException
	 */
	public List<WeappTester> listTester(String authAppId) throws WxmallBaseException;
	
	/**
	 * 获取小程序体验二维码
	 * @param authAppId
	 * @return
	 * @throws WxmallBaseException
	 */
	public String getWeappTestQrcode(Long authAppId) throws WxmallBaseException;
	
	/**
	 * 获取小程序审核状态
	 * @param authAppId
	 * @return
	 * @throws WxmallBaseException
	 */
	public Integer getWeappAuditStatus(String authAppId) throws WxmallBaseException;
	
	/**
	 * 发布上线
	 * @param authAuthId
	 * @throws WxmallBaseException
	 */
	public void release(Long authAuthId) throws WxmallBaseException;
	
	public List<AuthUser> getSellerAuthUserWeapp(Long sellerId);
	
	CompTicket getCompTicket();
	String getCompTicketStr();
	
	public Page<WeappAudit> page(Long appId, Integer pageNo, Integer pageSize) throws WxmallBaseException;
	
	Long getWeappCount();
	
	List<AuthUser> getAll();
	
	/**
	 * 获取短链接
	 * @param url
	 * @return
	 */
	String getShortUrl(AuthUser authUser, String url);
	
//	public AuthUser findByIdCache(Object id);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AuthUser findById(Object id);


    /**
     * find all model
     *
     * @return all <AuthUser
     */
    public List<AuthUser> findAll();


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
    public boolean delete(AuthUser model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(AuthUser model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(AuthUser model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AuthUser model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<AuthUser> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<AuthUser> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<AuthUser> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}