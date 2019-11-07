package com.dbumama.market.service.provider;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.Employee;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.model.UserCode;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.weixin.api.CompUserApi;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

@Bean
@RPCBean
public class SellerUserServiceImpl extends WxmServiceBase<SellerUser> implements SellerUserService {

	@Inject
	private PhoneCodeService phoneCodeService;
	@Inject
	private EmployeeService employeeService;
//	@Inject
//	private AuthUserService authUserService;
	
	@Override
	public SellerUser findByPhone(String phone) {
		return DAO.findFirst("select * from "+SellerUser.table+" where phone = ?", phone);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#findByOpenid(java.lang.String)
	 */
	@Override
	public SellerUser findByOpenid(String openid) {
		return DAO.findFirst("select * from "+SellerUser.table+" where open_id = ?", openid);
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#findByKdtId(java.lang.String)
	 */
	@Override
	public SellerUser findByKdtId(String kdtid, String phone) {
		return DAO.findFirst("select * from "+SellerUser.table+" where kdt_id = ? and phone=? ", kdtid, phone);
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#findByWeimoPid(java.lang.String)
	 */
	@Override
	public SellerUser findByWeimoPid(String pid) {
		return DAO.findFirst("select * from " + SellerUser.table + " where weimo_pid=? ", pid);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.SellerUserService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<SellerUser> list(Long sellerId, Integer pageNo, Integer pageSize) {
		return DAO.paginate(pageNo, pageSize, "select * ", " from " + SellerUser.table + " where seller_id=? ", sellerId);
	}
	
	@Override
	public Page<SellerUser> list(String phone,Integer active, Integer pageNo, Integer pageSize) {
		String select = "SELECT * ";
		String sqlExceptSelect = " from " + SellerUser.table ;//order by updated desc
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("active", active).addWhereLike("phone", phone)
		.addOrderBy("desc", "login_time").build();
		return DAO.paginate(pageNo, pageSize, select, helper.getSqlExceptSelect(), helper.getParams());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.SellerUserService#getCount()
	 */
	@Override
	public Long getCount() {
		return Db.queryLong("select count(*) from " + SellerUser.table);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.SellerUserService#register(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void register(String phone, String password, String confirmPwd, String captchaToken, String code, String incode) throws WxmallBaseException{
		if(StrKit.isBlank(phone)){
			throw new WxmallBaseException("手机号码不能为空");
		}
		if(StrKit.isBlank(password)){
			throw new WxmallBaseException("密码不能为空");
		}
		if(StrKit.isBlank(confirmPwd)){
			throw new WxmallBaseException("确认密码不能为空");
		}
		if(StrKit.isBlank(code)){
			throw new WxmallBaseException("手机验证码不能为空");
		}
		
		if(!password.equals(confirmPwd)){
			throw new WxmallBaseException("两次输入的密码不一样");
		}
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, code);
		if(userCode == null){
			throw new WxmallBaseException("手机验证码错误");
		}
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()){
			throw new WxmallBaseException("验证码已过期");
		}
		
		if(StrKit.notBlank(incode)){
			SellerUser seller = getSellerByIncode(incode);
			if(seller == null) {
				throw new WxmallBaseException("邀请码无效，请填写正确的邀请码，或者不填");
			}			
		}
		
		SellerUser sellerUser = new SellerUser();
		sellerUser.setPhone(phone);
		sellerUser.setPassword(DigestUtils.md5Hex(password));
		sellerUser.setActive(1);
		sellerUser.setCreated(new Date());
		sellerUser.setUpdated(new Date());
		if(StrKit.notBlank(incode)) sellerUser.setShareInviteCode(incode);
		sellerUser.save();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.SellerUserService#resetPwd(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void resetPwd(String phone, String password, String confirmPwd, String captchaToken, String code)
			throws WxmallBaseException {
		if(StrKit.isBlank(phone)){
			throw new WxmallBaseException("手机号码不能为空");
		}
		if(StrKit.isBlank(password)){
			throw new WxmallBaseException("密码不能为空");
		}
		if(StrKit.isBlank(confirmPwd)){
			throw new WxmallBaseException("确认密码不能为空");
		}
		if(StrKit.isBlank(code)){
			throw new WxmallBaseException("手机验证码不能为空");
		}
		
		if(!password.equals(confirmPwd)){
			throw new WxmallBaseException("两次输入的密码不一样");
		}
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, code);
		if(userCode == null){
			throw new WxmallBaseException("手机验证码错误");
		}
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()){
			throw new WxmallBaseException("验证码已过期");
		}
		
		SellerUser sellerUser = findByPhone(phone);
		if(sellerUser == null)
			throw new WxmallBaseException("账号"+phone+"未注册");
		
		if(sellerUser.getActive() != 1) throw new WxmallBaseException("账号" + phone + "被系统锁定，请联系管理员解锁");
		
		String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
        String encryptPassword = hash.toHex();
		sellerUser.setPhone(phone).setPassword(encryptPassword).setSalt(salt).setActive(1).setUpdated(new Date());
		sellerUser.update();
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#getSellerByIncode(java.lang.String)
	 */
	@Override
	public SellerUser getSellerByIncode(String incode) {
		return DAO.findFirst("select * from " + SellerUser.table + " where my_invite_code=? ", incode);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.SellerUserService#getPageSellerByIncode(java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public Page<SellerUser> getPageSellerByIncode(Integer pageNo, Integer pageSize, String shareCode, String phone) {
		if(StrKit.isBlank(shareCode)) {
			return new Page<SellerUser>(new ArrayList<SellerUser>(), pageNo, pageSize, 0, 0);
		}
		
		String select = "SELECT * ";
		String sqlExceptSelect = " from " + SellerUser.table ;//order by updated desc
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("share_invite_code", shareCode).addWhereLike("phone", phone)
		.addOrderBy("desc", "login_time").build();
		return DAO.paginate(pageNo, pageSize, select, helper.getSqlExceptSelect(), helper.getParams());
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#setPwd(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SellerUser setPwd(Long sellerId, String code, String password, String confirmPwd) throws UserException {
		if(sellerId == null) throw new UserException("sellerId is null");
		if(StrKit.isBlank(code)) throw new UserException("短信验证码不能为空");
		if(StrKit.isBlank(password)) throw new UserException("密码不能为空");
		if(StrKit.isBlank(confirmPwd)) throw new UserException("确认密码不能为空");
		if(!password.trim().equals(confirmPwd.trim())) throw new UserException("两次输入的密码不一致");
		if(password.length()<10) throw new UserException("密码长度不可少于10位，且不可过于简单");
		
		SellerUser seller = findById(sellerId);
		if(seller == null) throw new UserException("账户不存在");
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(seller.getPhone(), code.trim());
		if(userCode == null) throw new UserException("手机验证码错误");
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()) throw new UserException("验证码已过期");
		
		String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
        String encryptPassword = hash.toHex();
		
		seller.setPassword(encryptPassword).setSalt(salt);
		update(seller);
		return seller;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.SellerUserService#setTradePwd(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SellerUser setTradePwd(Long sellerId, String code, String password, String confirmPwd) throws UserException {
		if(sellerId == null) throw new UserException("sellerId is null");
		if(StrKit.isBlank(code)) throw new UserException("短信验证码不能为空");
		if(StrKit.isBlank(password)) throw new UserException("交易密码不能为空");
		if(StrKit.isBlank(confirmPwd)) throw new UserException("交易确认密码不能为空");
		if(!password.trim().equals(confirmPwd.trim())) throw new UserException("两次输入的密码不一致");
		if(password.length()<10) throw new UserException("密码长度不可少于10位，且不可过于简单");
		
		SellerUser seller = findById(sellerId);
		if(seller == null) throw new UserException("账户不存在");
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(seller.getPhone(), code.trim());
		if(userCode == null) throw new UserException("手机验证码错误");
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()) throw new UserException("验证码已过期");
		
		seller.setTradePassword(DigestUtils.md5Hex(password));
		update(seller);
		return seller;
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#setUserPhone(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SellerUser setUserPhone(Long sellerId, String phone, String code, String password, String confirmPwd)
			throws UserException {
		if(sellerId == null) throw new UserException("sellerId is null");
		if(StrKit.isBlank(phone)) throw new UserException("手机号码不能为空");
		if(StrKit.isBlank(code)) throw new UserException("短信验证码不能为空");
		if(StrKit.isBlank(password)) throw new UserException("登录密码不能为空");
		if(StrKit.isBlank(confirmPwd)) throw new UserException("确认密码不能为空");
		if(!password.trim().equals(confirmPwd.trim())) throw new UserException("两次输入的密码不一致");
		if(password.length()<10) throw new UserException("密码长度不可少于10位，且不可过于简单");
		
		SellerUser seller = findById(sellerId);
		if(seller == null) throw new UserException("账户不存在");
		if(StrKit.notBlank(seller.getPhone())) throw new UserException("已绑定手机号，无须重复绑定");
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, code.trim());
		if(userCode == null) throw new UserException("手机验证码错误");
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()) throw new UserException("验证码已过期");

		String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
        String encryptPassword = hash.toHex();
        
        seller.setPhone(phone).setPassword(encryptPassword).setSalt(salt);
		
		seller.update();
		return seller;
	}
	
	private int cacheSeconds = 600; //缓存时间，10分钟后清除缓存

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#getUserInfoByScence(java.lang.String)
	 */
	@Override
	public ApiResult getUserInfoByScence(String scenceId) {
		return Jboot.getCache().get(WeappConstants.WECHAT_LOGIN_CACHE, scenceId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#processInQrCode(java.lang.String, java.lang.String)
	 */
	@Override
	public void processInQrCode(String openId, String eventKey, String accessToken) throws UserException {
		//获取用户信息
		ApiResult userInfo = CompUserApi.getUserInfo(accessToken, openId);
		if(userInfo.isSucceed()){
			//获取用户信息失败，说明用户需要关注公众号
			Jboot.getCache().put(WeappConstants.WECHAT_LOGIN_CACHE, eventKey, userInfo, cacheSeconds);//10分钟后清除缓存值
		}else{
			//移除用户之前扫码的场景值
			Jboot.getCache().remove(WeappConstants.WECHAT_LOGIN_CACHE, openId);
			//缓存用户当前扫码的场景值
			Jboot.getCache().put(WeappConstants.WECHAT_LOGIN_CACHE, openId, eventKey, cacheSeconds);
		}		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#processInFollow(java.lang.String)
	 */
//	@Override
//	public void processInFollow(String openId, AuthUser authUser) throws UserException {
//		
//		final String accessToken = authUserService.getAccessToken(authUser);
//		
//		//获取用户信息
//		ApiResult userInfo = CompUserApi.getUserInfo(accessToken, openId);
//		
//		if(!userInfo.isSucceed()){
//			throw new UserException("get user Info error; error: " + userInfo.getErrorMsg());
//		}
//		
//		String eventKey = Jboot.getCache().get(CacheKey.WECHAT_LOGIN_CACHE, openId);
//		
//		if(StrKit.isBlank(eventKey)){
//			throw new UserException("eventKey is null; error: " + userInfo.getErrorMsg());
//		}
//		
//		//缓存用户信息到对应的场景值
//		Jboot.getCache().put(CacheKey.WECHAT_LOGIN_CACHE, eventKey, userInfo, cacheSeconds);		
//	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#getBalance(java.lang.Long)
	 */
	@Override
	public BigDecimal getBalance(Long sellerId, Long appId) {
		
		SellerUser seller = findById(sellerId);
		if(seller == null) return null;
		
		Employee employee = employeeService.findBySellerAndAuthUser(sellerId, appId);
		
		if(employee.getIsOwner() != null && employee.getIsOwner() == true){
			return seller.getBalance();
		}
		
		seller = findById(employee.getSellerId());
		
		return seller == null ? null : seller.getBalance();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerUserService#findByIdLock(java.lang.Long)
	 */
	@Override
	public SellerUser findByIdLock(Long id) {
		return DAO.findFirst("select * from " + SellerUser.table + " where id= ? for update ", id);
	}

}