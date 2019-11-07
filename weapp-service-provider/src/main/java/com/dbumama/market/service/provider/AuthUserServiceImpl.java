package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.WeappAuditStatus;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.weixin.api.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.rpc.annotation.RPCBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Bean
@RPCBean
public class AuthUserServiceImpl extends WxmServiceBase<AuthUser> implements AuthUserService {
	private static final AuthCert authCertDao = new AuthCert().dao();
	private static final CompTicket ticketDao = new CompTicket().dao();
	private static final WeappAudit weappAuditdao = new WeappAudit().dao();
	private static final WeappTester weappTesterDao = new WeappTester().dao();
	private static final WeappAudit weappAuditDao = new WeappAudit().dao();
	private static final Employee employeeDao = new Employee().dao();
	private static final Role roleDao = new Role().dao();

	@Inject
	private ShopService shopService;
	@Inject
	private ProductService productService;
	@Inject
	private WxamsgTemplateService wxamsgTemplateService;
	@Inject
	private WeappStyleService weappStyleService;
	@Inject
	private WeappTemplateService weappTemplateService;
	@Inject
	private AuthUserStyleService authUserStyleService;
	@Inject
	private AuthUserTemplateService authUserTemplateService;
	@Inject
	private SellerUserService sellerUserService;
	@Inject
	private EmployeeService employeeService;
	@Inject
	private AuthShopOrderService authShopOrderService;
	@Inject
	private AppService appService;
	@Inject
	private AuthUserAppService authUserAppService;
	
	@Override
	@Before(Tx.class)
	@CacheEvict(name = "authUserListcacheName", key = "#(sellerId)")
	public AuthUser bind(Long sellerId, String authCode) throws WxmallBaseException {
		if(sellerId == null || StrKit.isBlank(authCode)) throw new WxmallBaseException("绑定授权公众号/小程序缺少必要参数");
		
		SellerUser seller = sellerUserService.findById(sellerId);
		if(seller == null) throw new WxmallBaseException("bind authUser error, seller is null");
		
		//根据authCode调用接口拿授权微信公众号相关信息
		//1.获取公众号授权信息
		ApiResult result = QueryCompUserAuthApi.queryAuth(getCompAccessToken(), authCode);
		if(!result.isSucceed()) throw new WxmallBaseException("获取授权信息失败:" + result.getErrorMsg() + ";appId:" + Jboot.config(ApiConfig.class).getAppId());
		
		JSONObject jsonObject = JSONObject.parseObject(result.getJson());
		jsonObject = jsonObject.getJSONObject("authorization_info");
		
		if(StrKit.isBlank(jsonObject.getString("authorizer_appid"))){
			throw new WxmallBaseException("获取授权信息失败, appid is null");
		}
		
		//2.根据授权信息获取公众号详细信息，并检查公众号类型，只有认证的微信公众号才能绑定进来
//		service_type_info	授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
//		verify_type_info	授权方认证类型，
		//-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，
		//3代表已资质认证通过但还未通过名称认证，
		//4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，
		//5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
		ApiResult infoResult = GetCompAuthInfoApi.getAuthInfo(getCompAccessToken(), jsonObject.getString("authorizer_appid"));
		JSONObject infoJsonObject = JSONObject.parseObject(infoResult.getJson());
		JSONObject authorInfoJsonObject = JSON.parseObject(infoJsonObject.getString("authorizer_info"));
		JSONObject authorizationInfoJsonObject = JSON.parseObject(infoJsonObject.getString("authorization_info"));
		
		if(authorInfoJsonObject == null) throw new WxmallBaseException("authorInfoJsonObject is null");
		if(authorizationInfoJsonObject == null) throw new WxmallBaseException("authorizationInfoJsonObject is null");
		
		
		//设置公众号授权信息
		AuthUser authUser = DAO.findFirst(
				"select * from " + AuthUser.table + " where app_id=? ", jsonObject.getString("authorizer_appid"));
		
		if(authUser != null && authUser.getSellerId().intValue() != sellerId.intValue()){
			SellerUser oldUser = sellerUserService.findById(authUser.getSellerId());
			if(StrKit.notBlank(oldUser.getPhone()))
				throw new WxmallBaseException("该公众号/小程序 ["+authUser.getNickName()+"]已经在手机号为["+oldUser.getPhone()+"]的账户上绑定");
			else if(StrKit.notBlank(oldUser.getNick()))
				throw new WxmallBaseException("该公众号/小程序 ["+authUser.getNickName()+"]已经在昵称为["+oldUser.getNick()+"]的账户上绑定");
			else
				throw new WxmallBaseException("该公众号/小程序 ["+authUser.getNickName()+"]已经在其他账户上绑定");
		}
		
		if(authUser == null){
			authUser = new AuthUser();
			authUser.setSellerId(sellerId);
			authUser.setAppId(jsonObject.getString("authorizer_appid"));
			authUser.setCreated(new Date());
		}
		authUser.setAuthorizerAccessToken(jsonObject.getString("authorizer_access_token"));
		authUser.setAuthorizerRefreshToken(jsonObject.getString("authorizer_refresh_token"));
		authUser.setExpiresIn(jsonObject.getInteger("expires_in"));
		authUser.setSelectFuncInfo(jsonObject.getString("func_info"));
		authUser.setActive(1);
		authUser.setUpdated(new Date());
		authUser.setRefreshTime(new Date());
		//设置公众号详细信息
		if(authorizationInfoJsonObject != null){
			authUser.setFuncInfo(authorizationInfoJsonObject.getString("func_info"));			
		}
		authUser.setBusinessInfo(authorInfoJsonObject.getString("business_info"));
		authUser.setHeadImg(authorInfoJsonObject.getString("head_img"));
		authUser.setNickName(authorInfoJsonObject.getString("nick_name"));
		authUser.setServiceTypeInfo(authorInfoJsonObject.getString("service_type_info"));
		JSONObject serviceTypeJson = JSONObject.parseObject(authUser.getServiceTypeInfo());
		authUser.setServiceType(serviceTypeJson.getInteger("id"));
		authUser.setVerifyTypeInfo(authorInfoJsonObject.getString("verify_type_info"));
		JSONObject verifyTypeJson = JSONObject.parseObject(authUser.getVerifyTypeInfo());
		authUser.setVerifyType(verifyTypeJson.getInteger("id"));
		authUser.setUserName(authorInfoJsonObject.getString("user_name"));
		authUser.setQrcodeUrl(authorInfoJsonObject.getString("qrcode_url"));
		authUser.setAlias(authorInfoJsonObject.getString("alias"));
		authUser.setPrincipalName(authorInfoJsonObject.getString("principal_name"));
		if(StrKit.notBlank(authorInfoJsonObject.getString("MiniProgramInfo"))){
			authUser.setMiniprograminfo(authorInfoJsonObject.getString("MiniProgramInfo"));			
		}
		
		if(authUser.getVerifyType() !=0) throw new WxmallBaseException("公众号/小程序没有通过微信认证，不可绑定");
		
		try {
			saveOrUpdate(authUser);
			
			//获取系统设置的默认超级管理员角色，不存在就不为用户授权
			Role role = roleDao.findFirst("select * from " + Role.table + " where is_default=1");
			if(role != null){
				//创建当前绑定小程序的用户为超级管理员角色
				Employee employee = employeeDao.findFirst("select * from " + Employee.table + " where user_id=? and app_id=? ", seller.getId(), authUser.getId());
				
				if(employee == null){
					employee = new Employee();
					employee.setSellerId(sellerId).setAppId(authUser.getId()).setUserId(sellerId)
					.setActive(true).setRoleId(role.getId()).setCreated(new Date()).setUpdated(new Date());
				}
				
				employee.setEmplName(StrKit.isBlank(seller.getNick()) ? "超级管理员" : seller.getNick()).setIsOwner(true)
				.setPhone(StrKit.isBlank(seller.getPhone()) ? "" : seller.getPhone());
				employeeService.saveOrUpdate(employee);
			}
			
		} catch (Exception e) {
			throw new WxmallBaseException(e.getMessage());
		}
		
		if(StrKit.notBlank(authUser.getMiniprograminfo())){
			//授权小程序，设置小程序配置信息，域名以及小程序模板消息
			setWeappConfig(authUser);
		}
		
		return authUser;
	}
	
//	private void openApp(SellerUser seller, AuthUser authUser) throws ParseException{
//		//如果是从有赞过来的用户，直接全部授权
//		if(StrKit.notBlank(seller.getKdtId())){
//			//说明是有赞服务市场订购的用户
//			//检查是否有订购记录未处理的，如果有，就授权给当前绑定的公众号
//			
//			List<AuthShopOrder> authShopOrders = authShopOrderService.findByColumns(Columns.create("kdt_id", seller.getKdtId()).add("active", 1));
//			if(authShopOrders != null && authShopOrders.size()>0){
//				Integer days = 0;
//				//汇总天数
//				for(AuthShopOrder authShopOrder : authShopOrders){
//					days += authShopOrder.getSkuInterval();
//				}
//				
//				if(days >0){
//					//给当前绑定的公众号开通应用
//					List<App> apps = appService.findApps();
//					for(App app : apps){
//						if(!app.getIsfree()){
//							//付费的才处理
//							AuthUserApp authUserApp = authUserAppService.findByApp(authUser.getId(), app.getId());
//							
//							if(authUserApp == null){
//								authUserApp = new AuthUserApp();
//								authUserApp.setAuthUserId(authUser.getId())
//								.setAppId(app.getId())
//								.setStartDate(new Date())
//								.setEndDate(DateTimeUtil.FORMAT_YYYY_MM_DD.parse(DateTimeUtil.getNextDateStringAddDay(days)))
//								.setActive(true).setCreated(new Date()).setUpdated(new Date());
//								authUserAppService.save(authUserApp);
//							}else{
//								
//								Date endDate = authUserApp.getEndDate();
//								if(endDate.before(new Date())){
//									//过期
//									String endDateStr = DateTimeUtil.getNextDateStringAddDay(days);
//									endDate = DateTimeUtil.FORMAT_YYYY_MM_DD.parse(endDateStr);
//								}else{
//									//未过期
//									endDate = DateTimeUtil.FORMAT_YYYY_MM_DD.parse(DateTimeUtil.getNextDateStringAddDay(DateTimeUtil.FORMAT_YYYY_MM_DD.format(authUserApp.getEndDate()), days));
//								}
//								
//								authUserApp.setEndDate(endDate).setUpdated(new Date());
//								authUserAppService.update(authUserApp);
//							}
//						}
//					}
//					
//					for(AuthShopOrder authShopOrder : authShopOrders){
//						authShopOrder.setActive(false);
//						authShopOrder.setUpdated(new Date());
//						authShopOrderService.update(authShopOrder);
//					}
//				}
//			}
//		}
//	}
	
	private void setWeappConfig(AuthUser authUser){
		//设置新的服务器域名
//		String reqdomain = wechatConfig.getRequestdomain();
//		if(StrKit.isBlank(reqdomain)) throw new WxmallBaseException("未设置requestdomain域名");
//		List<String> requestdomainList = new ArrayList<>();
//		for(String req : reqdomain.split(",")) requestdomainList.add(req.trim());
//		String wsreqdomain = wechatConfig.getWsrequestdomain();
//		if(StrKit.isBlank(wsreqdomain)) throw new WxmallBaseException("未设置wsrequestdomain域名");
//		List<String> wsrequestdomainList = new ArrayList<String>();
//		for(String wsreq : wsreqdomain.split(",")) wsrequestdomainList.add(wsreq.trim());
//		String uploaddomain = wechatConfig.getUploaddomain();
//		if(StrKit.isBlank(uploaddomain)) throw new WxmallBaseException("未设置uploaddomain域名");
//		List<String> uploaddomainList = new ArrayList<String>();
//		for(String uploadreq : uploaddomain.split(",")) uploaddomainList.add(uploadreq.trim());
//		String downloaddomain = wechatConfig.getDownloaddomain();
//		if(StrKit.isBlank(downloaddomain)) throw new WxmallBaseException("未设置downloaddomain域名");
//		List<String> downdomainList = new ArrayList<String>();
//		for(String downloadreq : downloaddomain.split(",")) downdomainList.add(downloadreq.trim());
		
		//如果是小程序，需要对小程序域名配置进行处理
		JSONObject addDomainJson = new JSONObject();
		addDomainJson.put("action", "set");
		addDomainJson.put("requestdomain", Arrays.asList("https://api.dbumama.com"));
		addDomainJson.put("wsrequestdomain", Arrays.asList("wss://api.dbumama.com"));
		addDomainJson.put("uploaddomain", Arrays.asList("https://api.dbumama.com"));
		addDomainJson.put("downloaddomain", Arrays.asList("https://api.dbumama.com"));
		CompWxaDomainApi.domain(authUser.getAuthorizerAccessToken(), addDomainJson.toString());
//		ApiResult domainRes = CompWxaDomainApi.domain(authUser.getAuthorizerAccessToken(), addDomainJson.toString());
//		if(!domainRes.isSucceed()) {
//			if(domainRes.getErrorCode() != null && domainRes.getErrorCode().intValue() == 61007){
//				throw new WxmallBaseException(domainRes.getErrorCode(), "小程序["+authUser.getNickName()+"]已授权到其他第三方平台，请到微信小程序后台取消其他授权后再扫码授权;&nbsp;<a href='https://mp.weixin.qq.com/' target='_blank'>去取消授权</a>");
//			}else{
//				throw new WxmallBaseException(domainRes.getErrorCode(), "设置小程序["+authUser.getNickName()+"]服务器域名出错：" + domainRes.getErrorMsg());	
//			}
//		}
		
		/*List<WxamsgTemplate> wxamsgTpls = wxamsgTemplateService.findByAppId(authUser.getId());
		if(wxamsgTpls == null || wxamsgTpls.size()<=0){
			//自动添加模板消息配置
			//拼团待成团提醒
			setMessageTemplate(authUser, "AT0911", MsgTplType.grouping.ordinal(), new Integer []{1,10,3,14,4,6,8,7});
			//拼团成功消息配置
			setMessageTemplate(authUser, "AT0051", MsgTplType.groupsuccess.ordinal(), new Integer []{6,2,12,3,15,8,9,13,21,19});
			//拼团结果通知消息设置(失败通知)
			setMessageTemplate(authUser, "AT1814", MsgTplType.groupfail.ordinal(), new Integer []{1,2,3,4});
			//支付成功消息配置
			setMessageTemplate(authUser, "AT0009", MsgTplType.paied.ordinal(), new Integer []{10,33,3,6,9,7,8,34,32});
			//支付失败消息配置
			setMessageTemplate(authUser, "AT0010", MsgTplType.payfail.ordinal(), new Integer []{1,4,18,19,21,23});
			//订单待支付消息配置（开启催付，就必须关闭订单关闭消息提醒）
			setMessageTemplate(authUser, "AT0525", MsgTplType.unpay.ordinal(), new Integer []{1,13,4,9,20});
			//订单创建成功消息配置（取消）
			setMessageTemplate(authUser, "AT0210", MsgTplType.created.ordinal(), new Integer []{1,2,40,49,10,5});
			//订单超时关闭消息提醒配置（开启订单关闭消息提醒，就必须关闭订单催付消息提醒）
			setMessageTemplate(authUser, "AT1410", MsgTplType.closed.ordinal(), new Integer []{3,4,6,10,11,8});
			//订单发货消息提醒配置
			setMessageTemplate(authUser, "AT0007", MsgTplType.shiped.ordinal(), new Integer []{7,77,3,2,23,26});
			//预约课程成功消息配置
			setMessageTemplate(authUser, "AT0060", MsgTplType.appointcoursesuccess.ordinal(), new Integer []{1,11});
			//预约课程失败消息配置
			setMessageTemplate(authUser, "AT2372", MsgTplType.appointcoursefail.ordinal(), new Integer []{1,2,4});
			//预约考试成功消息配置
			setMessageTemplate(authUser, "AT2095", MsgTplType.appointexamsuccess.ordinal(), new Integer []{1,3});
			//预约考试失败消息配置
			setMessageTemplate(authUser, "AT0737", MsgTplType.appointexamfail.ordinal(), new Integer []{1,3});
		}*/
	}
	
//	private void setMessageTemplate(AuthUser authUser, String id, Integer type, Integer [] indexs) throws WxmallBaseException{
//		JSONObject groupingJson = new JSONObject();
//		groupingJson.put("id", id);
//		JSONArray groupingArray = new JSONArray();
//		for(int i=0; i<indexs.length; i++){
//			groupingArray.add(indexs[i]);
//		}
//		groupingJson.put("keyword_id_list", groupingArray);
//		ApiResult groupingRes = CompWxaTemplateApi.addTemplate(authUser.getAuthorizerAccessToken(), groupingJson.toString());
//		if(groupingRes.isSucceed()){ 
//			WxamsgTemplate wxamsgTpl = new WxamsgTemplate();
//			wxamsgTpl.setAppId(authUser.getId()).setTemplateKuId(id).setTemplateId(groupingRes.get("template_id")).setTemplateType(type).setActive(true);
//			wxamsgTemplateService.save(wxamsgTpl);
//		}
//	}
	
	/*@Override
	public AuthUser update(AuthUserParamDto userParam) throws WxmallBaseException {
		if(userParam == null || userParam.getAuthUserId() == null)
			throw new WxmallBaseException("更新公众号信息缺少必要参数");
		
		AuthUser authUser = findById(userParam.getAuthUserId());
		if(authUser == null) throw new WxmallBaseException("授权用户不存在");

		authUser.update();
		
		//是否有传证书文件
		AuthCert authCert = authCertDao.findFirst("select * from " + AuthCert.table 
				+ " where app_id=? ", authUser.getAppId());
		if(authCert == null){
			authCert = new AuthCert();
			authCert.setAppId(authUser.getAppId());
			authCert.setCreated(new Date());
			authCert.setUpdated(new Date());
			authCert.setActive(1);
			if(userParam.getCertFile() != null){
				try {
					authCert.setCertFile(FileUtils.toByteArray(userParam.getCertFile()));
				} catch (IOException e) {
					throw new WxmallBaseException(e.getMessage());
				}finally {
					//删除文件
					if(userParam.getCertFile() != null)
						userParam.getCertFile().delete();
				}
			}
			try {
				authCert.save();				
			} catch (Exception e) {
				throw new WxmallBaseException(e.getMessage());
			}
		}else{
			authCert.setUpdated(new Date());
			if(userParam.getCertFile() != null){
				try {
					authCert.setCertFile(FileUtils.toByteArray(userParam.getCertFile()));
				} catch (IOException e) {
					throw new WxmallBaseException(e.getMessage());
				} finally {
					//删除文件
					if(userParam.getCertFile() != null)
						userParam.getCertFile().delete();
				}	
			}
			try {
				authCert.update();
			} catch (Exception e) {
				throw new WxmallBaseException(e.getMessage());
			}
		}
		
		return authUser;
	}*/

	@Override
	public Page<AuthUser> list(AuthUserParamDto userParam) throws WxmallBaseException {
		if(userParam == null || userParam.getAuthUserId() == null) throw new WxmallBaseException("获取授权公众号列表缺少必要参数");
		String select = "select * ";
		String sqlExceptSelect = " from " + AuthUser.table + " a ";
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("a.app_id", userParam.getAuthUserId())
		.addWhere("a.active", 1);
		
		if(userParam.getServiceType() == null){
			helper.addWhereNOT_EQUAL("a.service_type", 0);
		}else{
			helper.addWhere("a.service_type", userParam.getServiceType());
		}
		
		helper.addOrderBy("desc", "a.updated").build();
		
		return DAO.paginate(userParam.getPageNo(), userParam.getPageSize(), 
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#list()
	 */
	@Override
	public Page<AuthUser> list(Integer pageNo, Integer pageSize, Integer active, String nickName) throws WxmallBaseException {
		String select = "select * ";
		String sqlExceptSelect = " from " + AuthUser.table + " a ";
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("a.active", active)
		.addWhereLike("a.nick_name", StrKit.isBlank(nickName)?null:nickName)
		.addWhere("a.service_type", 0)
		.addOrderBy("desc", "a.updated").build();
		
		return DAO.paginate(pageNo, pageSize, 
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

	@Override
	public void commitAudit(Long authAppId, String itemList) throws WxmallBaseException {
		if(authAppId == null || itemList == null) throw new WxmallBaseException("小程序提交审核，缺少必要参数");
		
		//check shop
//		Shop shop = shopService.findByApp(authAppId);
//		if(shop == null || shop.getShopSign() == null){
//			throw new WxmallBaseException("店铺不存在或者没有设置店招，不可提交审核");
//		}
//		
//		//check product
//		Long productCount = productService.getWeappPorudctCount(authAppId);
//		if(productCount == null || productCount <=0){
//			throw new WxmallBaseException("没有上传商品，不可提交审核");
//		}
		
		AuthUser authUser = findById(authAppId);
		if(authUser == null) throw new WxmallBaseException("小程序提交审核失败, authUser is null");
		
		String accessToken = getAccessToken(authUser);
		if(StrKit.isBlank(accessToken)) throw new WxmallBaseException("授权码不存在，请重新授权小程序");
		
		//上传代码
		JSONArray jsonArray = (JSONArray) JSONArray.parse(itemList);
		if(jsonArray == null || jsonArray.size()<=0){
			throw new WxmallBaseException("提交小程序审核出错，没有可提交数据");
		}
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("item_list", JSONObject.parseArray(itemList));
		ApiResult submitAuditRes = CompWxaCodeApi.submitAudit(accessToken, jsonParam.toString());
		if(!submitAuditRes.isSucceed()) throw new WxmallBaseException("小程序提交审核失败：" + submitAuditRes.getErrorMsg());
		logger.debug("=====submitAuditRes Json:" + submitAuditRes.getJson());
		Integer auditid = submitAuditRes.getInt("auditid");//小程序审核id
		WeappAudit weappAudit = new WeappAudit();
		weappAudit.setAppId(authUser.getId());
		weappAudit.setAuditid(auditid.toString());
		weappAudit.setStatus(WeappAuditStatus.auditing.ordinal());
		weappAudit.setCreated(new Date());
		weappAudit.setUpdated(new Date());
		weappAudit.save();
		
	}

	@Override
	public Integer getWeappAuditStatus(String authAppId) throws WxmallBaseException {
		if(StrKit.isBlank(authAppId)) throw new WxmallBaseException("请传入appId");
		WeappAudit authUser = weappAuditdao.findFirst("select * from " + WeappAudit.table + " where app_id=? ", authAppId);
		return authUser == null ? null : authUser.getStatus();
	}

	@Override
	@Cacheable(name = "authUserListcacheName", key = "#(sellerId)")
	public List<AuthUser> getSellerAllAuthUsers(Long sellerId) {
		SellerUser seller = sellerUserService.findById(sellerId);
		if(seller == null) return null;
		
		List<Employee> employees = employeeDao.find("select * from " + Employee.table + " where user_id = ? and active=1 ", seller.getId());
		
		List<AuthUser> authUsers = new ArrayList<AuthUser>();
		for(Employee employee : employees){
			AuthUser authUser = findById(employee.getAppId());
			if(authUser.getActive() !=null && authUser.getActive()== 1){
				authUsers.add(authUser);//取消授权的不算
			}
		}
		return authUsers;
	}

	@Override
	public List<AuthUser> getSellerAuthUser(Long sellerId) {
		
		List<AuthUser> _authUsers = getSellerAllAuthUsers(sellerId);
		
		List<AuthUser> authUsers = new ArrayList<AuthUser>();
		if(_authUsers !=null) {
			for(AuthUser authUser : _authUsers) {
				if(authUser.getActive() !=null && authUser.getActive()== 1 
						&& (authUser.getServiceType() == 1 || authUser.getServiceType() == 2)){
					authUsers.add(authUser);				
				}	
			}
		}
		
		return authUsers;
	}
	

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getSellerAuthUserWeapp(java.lang.Long)
	 */
	@Override
	public List<AuthUser> getSellerAuthUserWeapp(Long sellerId) {
		
		List<AuthUser> _authUsers = getSellerAllAuthUsers(sellerId);
		
		List<AuthUser> authUsers = new ArrayList<AuthUser>();
		
		if(_authUsers !=null) {
			for(AuthUser authUser : _authUsers) {
				if(authUser.getActive() !=null && authUser.getActive()== 1 
						&& StrKit.notBlank(authUser.getMiniprograminfo())){
					authUsers.add(authUser);				
				}	
			}
		}
		
		return authUsers;
	}
	
	@Override
	public String getAccessToken(AuthUser authUser) {
		if(authUser == null) return "";
		
		String accessToken = authUser.getAuthorizerAccessToken();
		String refreshToken = authUser.getAuthorizerRefreshToken();
		Integer expires_in = authUser.getExpiresIn();
		Long expiredTime = null;		// 正确获取到 access_token 时有值，存放过期时间
		if (expires_in != null) {
			expiredTime = authUser.getRefreshTime() == null ? authUser.getUpdated().getTime() + ((expires_in -300) * 1000) 
					: authUser.getRefreshTime().getTime() + ((expires_in -300) * 1000);
		}
			
		if(expiredTime == null || expiredTime < System.currentTimeMillis()){
			ApiResult result = RefreshTokenApi.getRefreshToken(getCompAccessToken(), authUser.getAppId(), refreshToken);
			if(result!=null && result.isSucceed()){
				accessToken = result.getStr("authorizer_access_token");
				expires_in = result.getInt("expires_in");
				refreshToken = result.getStr("authorizer_refresh_token");
				authUser.setAuthorizerAccessToken(accessToken);
				authUser.setExpiresIn(expires_in);
				authUser.setAuthorizerRefreshToken(refreshToken);
				authUser.setUpdated(new Date());
				authUser.setRefreshTime(new Date());
				authUser.update();
			}else{
				//System.out.println("============================刷新token失败,appId:" + authUser.getAppId() + ",code:" + result.getErrorCode() + ",msg:" + result.getErrorMsg());
			}
		}
		return accessToken;
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#refreshAccessToken(com.dbumama.market.model.AuthUser)
	 */
	@Override
	public String refreshAccessToken(AuthUser authUser) {
		String accessToken = authUser.getAuthorizerAccessToken();
		String refreshToken = authUser.getAuthorizerRefreshToken();
		Integer expires_in = authUser.getExpiresIn();
		ApiResult result = RefreshTokenApi.getRefreshToken(getCompAccessToken(), authUser.getAppId(), refreshToken);
		if(result == null || !result.isSucceed()){
			//System.out.println("============================刷新token失败,appId:" + authUser.getAppId() + ",code:" + result.getErrorCode() + ",msg:" + result.getErrorMsg());
			return null;
		}
		accessToken = result.getStr("authorizer_access_token");
		expires_in = result.getInt("expires_in");
		refreshToken = result.getStr("authorizer_refresh_token");
		authUser.setAuthorizerAccessToken(accessToken);
		authUser.setExpiresIn(expires_in);
		authUser.setAuthorizerRefreshToken(refreshToken);
		authUser.setUpdated(new Date());
		authUser.setRefreshTime(new Date());
		update(authUser);
		return accessToken;
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#checkAccessToken(com.dbumama.market.model.AuthUser)
	 */
	@Override
	public Boolean checkAccessToken(AuthUser authUser) {
		String refreshToken = authUser.getAuthorizerRefreshToken();
		ApiResult result = RefreshTokenApi.getRefreshToken(getCompAccessToken(), authUser.getAppId(), refreshToken);
		return result!=null && result.isSucceed();
	}

	@Override
	public AuthUser getAuthUserByAppId(String appId) {
		return DAO.findFirst("select * from " + AuthUser.table + " where app_id=?", appId);
	}
	
	@Override
	public CompTicket getCompTicket(){
		return ticketDao.findFirst("select * from " + CompTicket.table);
	}
	
	@Override
	public String getCompTicketStr(){
		return getCompTicket()== null ? "" : getCompTicket().getCompVerifyTicket();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#uploadWeappCode(java.lang.Long, java.lang.String, java.lang.Integer)
	 */
	@Override
	@Before(Tx.class)
	public void uploadWeappCode(Long authAppId, Integer tplId, Integer styleId) throws WxmallBaseException {
		if(authAppId == null) throw new WxmallBaseException("打包发布小程序，缺少必要参数");
		if(tplId == null) throw new WxmallBaseException("请选择小程序模板");
		if(styleId == null) throw new WxmallBaseException("请选择配色方案");
		
		AuthUser authUser = findById(authAppId);
		if(authUser == null) throw new WxmallBaseException("打包发布小程序失败，authUser is null");
		WeappTemplate weappTemplate = weappTemplateService.findById(tplId);
		if(weappTemplate == null) throw new WxmallBaseException("小程序模板不存在");
		
	 	WeappStyle style = weappStyleService.findById(styleId);
	 	if(style == null) throw new WxmallBaseException("打包发布小程序失败，style is null");
	 	if(StrKit.isBlank(style.getNavbarBgcolor()) || !style.getNavbarBgcolor().startsWith("#"))
	 		throw new WxmallBaseException("打包发布小程序失败，配色方案背景颜色值有误");
		
	 	AuthUserStyle authUserStyle = authUserStyleService.getAuthUserStyleByAppandStyle(authAppId);
	 	if(authUserStyle == null){
	 		authUserStyle = new AuthUserStyle();
	 		authUserStyle.setAppId(authAppId);
	 		authUserStyle.setStyleId(style.getId());
	 		authUserStyle.setCreated(new Date());
	 		authUserStyle.setUpdated(new Date());
	 		authUserStyle.setActive(true);
	 	}else{
	 		authUserStyle.setStyleId(style.getId());
	 		authUserStyle.setUpdated(new Date());
	 	}
	 	authUserStyleService.saveOrUpdate(authUserStyle);
	 	
		String accessToken = getAccessToken(authUser);
		if(StrKit.isBlank(accessToken)) throw new WxmallBaseException("授权码不存在，请重新授权小程序");
		
		//上传代码
		JSONObject commitJson = new JSONObject();
		commitJson.put("template_id", weappTemplate.getTemplateId());
		
		//自定义ext json 第三方服务需要根据该配置里面的appId在小程序客户端识别是哪个小程序调用了接口服务
		//配色方案
		//激活第三方自定义配置
		commitJson.put("ext_json", "{\"extEnable\":true, \"extAppid\":\""+authUser.getAppId()+"\", "
				+ "\"ext\":{\"appid\":\""+authUser.getAppId()+"\"},"
				+ "\"window\":{\"enablePullDownRefresh\": true, "
				+ "\"navigationBarBackgroundColor\": \""+style.getNavbarBgcolor()+"\", "
				+ "\"navigationBarTextStyle\": \"white\", "
				+ "\"navigationBarTitleText\": \""+authUser.getNickName()+"\", "
				+ "\"backgroundTextStyle\": \"dark\"}"
						+ "}");
		commitJson.put("user_version", weappTemplate.getVersion());
		commitJson.put("user_desc", weappTemplate.getVersionDesc());
		//自定义ext json 结束
		ApiResult commitRes = CompWxaCodeApi.commit(accessToken, commitJson.toString());
		if(!commitRes.isSucceed()) throw new WxmallBaseException("上传小程序代码出错：" + commitRes.getErrorMsg());
		
		//记录该小程序使用的模板
		AuthUserTemplate authUserTemplate = authUserTemplateService.getAuthUserTemplate(authAppId);
		if(authUserTemplate == null){
			authUserTemplate = new AuthUserTemplate();
			authUserTemplate.setAppId(authAppId);
			authUserTemplate.setCreated(new Date());
			authUserTemplate.setActive(true);
		}
		authUserTemplate.setTemplateId(weappTemplate.getId());
		authUserTemplate.setUpdated(new Date());
		authUserTemplateService.saveOrUpdate(authUserTemplate);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#page(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<WeappAudit> page(Long appId, Integer pageNo, Integer pageSize) throws WxmallBaseException {
		if(appId == null) throw new WxmallBaseException("获取小程序发布列表缺少必要参数");
		String select = "select * ";
		String sqlExceptSelect = " from " + WeappAudit.table + " a ";
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("a.app_id", appId);
		
		helper.addOrderBy("desc", "a.updated").build();
		
		return weappAuditdao.paginate(pageNo, pageSize, 
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#bindtest(java.lang.String)
	 */
	@Override
	public void bindtest(Long authAppId, String testUser) throws WxmallBaseException {
		if(StrKit.isBlank(testUser) || authAppId == null) throw new WxmallBaseException("请输入微信号");
		AuthUser authUser = findById(authAppId);
		JSONObject testUserJson = new JSONObject();
		testUserJson.put("wechatid", testUser);
		ApiResult bindResult = CompWxaCodeApi.bindtest(getAccessToken(authUser), testUserJson.toString());
		if(!bindResult.isSucceed()) throw new WxmallBaseException("绑定体验者失败," + bindResult.getErrorMsg());
		WeappTester tester = weappTesterDao.findFirst("select * from " + WeappTester.table + " where wx_user=? and app_id=? ", testUser, authUser.getId());
		if(tester == null){
			tester = new WeappTester();
			tester.setAppId(authUser.getId());
			tester.setWxUser(testUser);
			tester.setCreated(new Date());
			tester.setUpdated(new Date());
			tester.setActive(true);
			tester.save();
		}else{
			tester.setActive(true);
			tester.setUpdated(new Date());
			tester.update();
		}
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#unbindtest(java.lang.String)
	 */
	@Override
	public void unbindtest(Long authAppId, String testUser) throws WxmallBaseException {
		if(StrKit.isBlank(testUser) || authAppId == null) throw new WxmallBaseException("请输入微信号");
		JSONObject testUserJson = new JSONObject();
		testUserJson.put("wechatid", testUser);
		ApiResult bindResult = CompWxaCodeApi.unbindtest(getAccessToken(findById(authAppId)), testUserJson.toString());
		if(!bindResult.isSucceed()) throw new WxmallBaseException("解绑体验者失败," + bindResult.getErrorMsg());
		WeappTester tester = weappTesterDao.findFirst("select * from " + WeappTester.table + " where wx_user=? and app_id=? ", testUser, findById(authAppId).getAppId());
		if(tester != null){
			tester.setActive(false);
			tester.setUpdated(new Date());
			tester.update();
		}
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#listTester(java.lang.String)
	 */
	@Override
	public List<WeappTester> listTester(String authAppId) throws WxmallBaseException {
		return weappTesterDao.find("select * from " + WeappTester.table + " where app_id=? and active=1", authAppId);
	}

	OkHttpClient okHttpClient = new OkHttpClient();
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#getWeappTestQrcode(java.lang.String)
	 */
	@Override
	public String getWeappTestQrcode(Long authAppId) throws WxmallBaseException {
		Request request = new Request.Builder().
                url(CompWxaCodeApi.getQrcode(getAccessToken(findById(authAppId))))
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116Safari/537.36")
                .build();
		Response okResponse;
		Base64 base64 = new Base64();
		try {
			okResponse = okHttpClient.newCall(request).execute();
			return "data:image/jpg;base64," + base64.encodeAsString(okResponse.body().bytes());
		} catch (IOException e) {
			throw new WxmallBaseException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#release(java.lang.String)
	 */
	@Override
	public void release(Long authAppId) throws WxmallBaseException {
		ApiResult result = CompWxaCodeApi.release(getAccessToken(findById(authAppId)));
		if(!result.isSucceed()){
			throw new WxmallBaseException("小程序发布失败:" + result.getErrorMsg());
		}
		//查询正在审核的小程序记录
		List<WeappAudit> weappAudits = weappAuditDao.find("select * from " + WeappAudit.table + " where app_id=? and status=2", findById(authAppId).getAppId());
		if(weappAudits != null){
			for(WeappAudit weappAudit : weappAudits){
				weappAudit.setStatus(WeappAuditStatus.success.ordinal());
				weappAudit.update();				
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.authuser.AuthUserService#getWeappCount()
	 */
	@Override
	public Long getWeappCount() {
		return Db.queryLong("select count(*) from " + AuthUser.table + " where service_type=0");
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getAuthUserByUserName(java.lang.String)
	 */
	@Override
	public AuthUser getAuthUserByUserName(String userName) {
		return DAO.findFirst("select * from " + AuthUser.table + " where user_name=? ", userName);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getWeappAduitsByAppId(java.lang.String)
	 */
	@Override
	public List<WeappAudit> getWeappAduitsByAppId(String appId) {
		return weappAuditDao.find("select * from " + WeappAudit.table + " where app_id=? and status=2", appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getAuthCert(com.dbumama.market.model.AuthUser)
	 */
	@Override
	public AuthCert getAuthCert(AuthUser authUser) {
		return authCertDao.findFirst("select * from " + AuthCert.table + " where app_id=? ", authUser.getAppId());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getCompAccessToken()
	 */
	@Override
	public String getCompAccessToken() {
		return CompAccessTokenApi.getAccessTokenStr(getCompTicketStr());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getAll()
	 */
	@Override
	public List<AuthUser> getAll() {
		return DAO.find("select * from " + AuthUser.table + " where active=1");
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#getShortUrl(java.lang.String)
	 */
	@Override
	public String getShortUrl(AuthUser authUser, String url) {
		ApiResult shortUrl = CompShorturlApi.getShortUrl(url, getAccessToken(authUser));
		return shortUrl.isSucceed() ? shortUrl.get("short_url") : url;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#hotAuthUsers()
	 */
	@Override
	public List<AuthUser> hotAuthUsers() {
		return DAO.find("select * from " + AuthUser.table + " where active=1 order by created desc limit 30 ");
	}
	
	@Override
	public AuthUser findByAppId(String appId) {
		return DAO.findFirst("select * from " + AuthUser.table + " where app_id = ? and  active=1",appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserService#findByAppName(java.lang.String)
	 */
	@Override
	public AuthUser findByAppName(String appName) {
		return DAO.findFirst("select * from " + AuthUser.table + " where nick_name=? ", appName);
	}

}