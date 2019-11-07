package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.encrypt.SHA1;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.weixin.api.CompAccessToken;
import com.dbumama.weixin.api.ParaMap;
import com.dbumama.weixin.utils.HttpUtils;
import com.dbumama.weixin.utils.RetryUtils;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.vdurmont.emoji.EmojiParser;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Columns;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Bean
@RPCBean
public class BuyerUserServiceImpl extends WxmServiceBase<BuyerUser> implements BuyerUserService {
	
	@Inject
	private AuthUserService authUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.buyer.BuyerService#getCount()
	 */
	public Long getCount() {
		return Db.queryLong("select count(*) from " + BuyerUser.table);
	}

	@Override
	@Cacheable(name = "cacheName", key = "#(appId)-#(pageNo)", liveSeconds = 600)
	public Page<BuyerUser> list(CustomerParamDto customerParam, String appId, Integer pageNo) throws CustomerException {
		if(customerParam == null || customerParam.getAuthUserId()==null) 
			throw new CustomerException("获取客户列表缺少必要参数");
		
		QueryHelper helper = new QueryHelper("select bu.id, bu.open_id, bu.nickname, bu.headimgurl, bu.sex, bu.country, bu.province, bu.city, bu.subscribe_scene, bu.subscribe_time ", " from " + BuyerUser.table +" bu ");
		helper.addWhereLikeOr("bu.nickname", "bu.open_id", customerParam.getNameOrOpenId(), customerParam.getNameOrOpenId());
		
		//标签条件查询
		if(customerParam.getTagidList() !=null || customerParam.getTagidList() != ""){
			JSONArray tagsArr = JSONArray.parseArray(customerParam.getTagidList());
			if(customerParam.getTagsBasic().equals("1")){
				// 1为包含以下任一
				helper.addWhereFindInSetOr("bu.tagid_list", tagsArr);
			}else if(customerParam.getTagsBasic().equals("2")){
				//2 为包含以下所有
				helper.addWhereFindInSet("bu.tagid_list", tagsArr);
			}else if(customerParam.getTagsBasic().equals("3")){
				//3 为排除以下任一
				helper.addWhereNotFindInSetOr("bu.tagid_list", tagsArr);
			}else if(customerParam.getTagsBasic().equals("4")){
				//3 为排除以下任一
				helper.addWhereNotFindInSet("bu.tagid_list", tagsArr);
			}
		}
		//用户关注的渠道来源 1为包括
		if(customerParam.getsSceneBasic().equals("1")){
			helper.addWhere("bu.subscribe_scene", customerParam.getSubscribeScene());
		}else{
			helper.addWhereNOT_EQUAL("bu.subscribe_scene", customerParam.getSubscribeScene());
		}
		//用户关注 5为全部
		if(customerParam.getActive() != 5){
			helper.addWhere("bu.active", customerParam.getActive());
		}
		//用户性别 5为全部
		if(customerParam.getSex() != 5){
			helper.addWhere("bu.sex", customerParam.getSex());
		}
		//关注时间 1为包括
		if(customerParam.getFollowBasic().equals("1")){
			helper.addWhereTHEN_GE("bu.subscribe_time", customerParam.getFollowStartDate())
			.addWhereTHEN_LE("bu.subscribe_time", customerParam.getFollowEndDate());
		}else{
			helper.addWhereTHEN_LE("bu.subscribe_time", customerParam.getFollowStartDate())
			.addWhereTHEN_GE_OR("bu.subscribe_time", customerParam.getFollowEndDate());
		}
		//取消时间 1为包括
		if(customerParam.getCancelBasic().equals("1")){
			helper.addWhereTHEN_GE("bu.un_subscribe_time", customerParam.getCancelStartDate())
			.addWhereTHEN_LE("bu.un_subscribe_time", customerParam.getCancelEndDate());
		}else{
			helper.addWhereTHEN_LE("bu.un_subscribe_time", customerParam.getCancelStartDate())
			.addWhereTHEN_GE_OR("bu.un_subscribe_time", customerParam.getCancelEndDate());
		}
		
		helper.addWhere("bu.app_id", customerParam.getAuthUserId())
		.addOrderBy("desc", "bu.subscribe_time").build();
		customerParam.setPageSize(15);
		Page<BuyerUser> buyerUser = DAO.paginate(customerParam.getPageNo(), customerParam.getPageSize(), 
				helper.getSelect(), 
				helper.getSqlExceptSelect(),
				helper.getParams());
		return buyerUser;
	}

	@Override
	public Page<MemberResultDto> listMembers(CustomerParamDto customerParam) throws CustomerException {
		if(customerParam == null || customerParam.getAuthUserId() == null)
			throw new CustomerException("获取客户列表缺少必要参数");
		
		List<Record> records = Db.find("select buyer_id from t_buyer_card where `status` = 1 GROUP BY buyer_id");
		List<Long> ids = new ArrayList<Long>();
		for(Record r : records){
			ids.add(r.getLong("buyer_id"));
		}
		if(ids.size() <= 0)
			return new Page<MemberResultDto>(new ArrayList<MemberResultDto>(), customerParam.getPageNo(), customerParam.getPageSize(), 1, 0);
		
		QueryHelper helper = new QueryHelper("select * ", " from " + BuyerUser.table);
		//helper.addWhereLike("nickname", customerParam.getNickName())
		helper.addWhere("active", customerParam.getActive())
		.addWhere("app_id", customerParam.getAuthUserId())
		.addWhereIn("id", ids)
		.addOrderBy("desc", "created").build();
		
		Page<BuyerUser> buyerUsers = DAO.paginate(customerParam.getPageNo(), customerParam.getPageSize(), 
				helper.getSelect(), 
				helper.getSqlExceptSelect(),
				helper.getParams());
		
		List<MemberResultDto> members = new ArrayList<MemberResultDto>();
		for(BuyerUser by : buyerUsers.getList()){
			MemberResultDto mrd = new MemberResultDto();
			members.add(mrd);
			mrd.setNickName(by.getNickname());
			mrd.setHeadImg(by.getHeadimgurl());
			mrd.setSex(by.getSex());
			mrd.setLastLoginTime(by.getLastLoginTime());
			mrd.setActive(by.getActive());
			mrd.setProvince(by.getProvince());
		}
		return new Page<MemberResultDto>(members, buyerUsers.getPageNumber(), buyerUsers.getPageSize(), buyerUsers.getTotalPage(), buyerUsers.getTotalRow());
	}
	
	final String login_url_open = "https://api.weixin.qq.com/sns/component/jscode2session";
	
	@Override
	public WeappLoginResultDto loginWeapp(String appId, String code) throws UserException {
		if(StrKit.isBlank(appId) || StrKit.isBlank(code)) throw new UserException("loginWeapp 缺少完整参数");
		
		AuthUser authUser = authUserService.findByAppId(appId);
		if(authUser == null) throw new UserException("wxapp is not exist ");
		
		//根据code拿用户授权token
    	String url = login_url_open+"?appid="+appId
    			+"&js_code="+code+"&grant_type=authorization_code"
    			+ "&component_appid="+Jboot.config(ApiConfig.class).getAppId()+"&component_access_token="+authUserService.getCompAccessToken();
    	ApiResult loginRes = null;
    	try {
    		loginRes = new ApiResult(HttpKit.get(url));
		} catch (Exception e) {
			throw new UserException(e.getMessage());
		}
         
        if(loginRes.getErrorCode() != null){
        	//如果请求失败，再拿一次comp accessToken
        	final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId())
    				.put("component_appsecret", Jboot.config(ApiConfig.class).getAppSecret()).put("component_verify_ticket", authUserService.getCompTicketStr()).getData();
    		// 最多三次请求
    		CompAccessToken compTokenresult = RetryUtils.retryOnException(3, new Callable<CompAccessToken>() {
    			
    			@Override
    			public CompAccessToken call() throws Exception {
    				String json = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/component/api_component_token", JSON.toJSONString(queryParas));
    				return new CompAccessToken(json);
    			}
    		});
    		url = login_url_open+"?appid="+appId
        			+"&js_code="+code+"&grant_type=authorization_code"
        			+ "&component_appid="+Jboot.config(ApiConfig.class).getAppId()+"&component_access_token="+compTokenresult.getAccessToken();
    		try {
        		loginRes = new ApiResult(HttpKit.get(url));
    		} catch (Exception e) {
    			throw new UserException(e.getMessage());
    		}
        }
        
        //再请求一次comp accessToken，如果还获取不到token就报错
        if(loginRes.getErrorCode() != null)
        	throw new UserException("通过code获取accessToken接口：" +loginRes.getErrorMsg());
        
        WeappLoginResultDto resDto = new WeappLoginResultDto();
        resDto.setOpenid(loginRes.getStr("openid"));
        resDto.setSessionKey(loginRes.getStr("session_key"));
        
        //记住该用户微信登录的sessionKey
        BuyerUser buyer = findByOpenId(loginRes.getStr("openid"));
        if(buyer == null){
        	buyer = new BuyerUser();
        	buyer.setAppId(authUser.getId());
        	buyer.setOpenId(loginRes.getStr("openid"));
        	buyer.setCreated(new Date());
        	buyer.setActive(1);
        }
        buyer.setSessionKey(loginRes.getStr("session_key"));
        buyer.setUpdated(new Date());
        saveOrUpdate(buyer);
		return resDto;
	}

	@Override
	public BuyerUser check(WeappUserCheckParamDto userCheckParam) throws UserException {
		if(userCheckParam == null 
				|| StrKit.isBlank(userCheckParam.getSessionKey())
				|| StrKit.isBlank(userCheckParam.getSignature())
				|| StrKit.isBlank(userCheckParam.getRawData())
				|| StrKit.isBlank(userCheckParam.getEncryptedData())
				|| StrKit.isBlank(userCheckParam.getIv())
				|| StrKit.isBlank(userCheckParam.getAppId()))
			throw new UserException("校验用户合法性，请传入完整参数");
		final String sign2 = SHA1.sha1(userCheckParam.getRawData()+userCheckParam.getSessionKey());
		if(!sign2.equals(userCheckParam.getSignature())){
			throw new UserException("签名错误");
		}
		//解密用户数据，并存储到数据库
		Base64 base64 = new Base64();
		byte [] content = base64.decode(userCheckParam.getEncryptedData());
		byte [] ivbyte = base64.decode(userCheckParam.getIv());
		byte [] sessionKeyByte = base64.decode(userCheckParam.getSessionKey());
		
		Security.addProvider(new BouncyCastleProvider());
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			Key sKeySpec = new SecretKeySpec(sessionKeyByte, "AES");

			AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
	        params.init(new IvParameterSpec(ivbyte));
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);// 初始化
            byte[] resultByte = cipher.doFinal(content);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                final JSONObject json = (JSONObject) JSONObject.parse(result);
                JSONObject watermarkJson = json.getJSONObject("watermark");
                if(watermarkJson == null 
                		|| watermarkJson.getString("appid")==null 
                		|| !watermarkJson.getString("appid").equals(userCheckParam.getAppId())){
                	throw new UserException("非法用户数据");
                }
                //save or update into db
                String openId = json.getString("openId");
                BuyerUser buyer = DAO.findFirst("select * from " + BuyerUser.table + " where open_id=?", openId);
                if(buyer == null){
                	buyer = new BuyerUser();
                	buyer.setOpenId(openId);
                	buyer.setCreated(new Date());
                }
                buyer.setAppId(userCheckParam.getAuthUserId());
                buyer.setNickname(EmojiParser.removeAllEmojis(json.getString("nickName")));
                buyer.setHeadimgurl(json.getString("avatarUrl"));
                buyer.setSex(json.getInteger("gender"));
                buyer.setLanguage(json.getString("language"));
                buyer.setCity(json.getString("city"));
                buyer.setProvince(json.getString("province"));
                buyer.setCountry(json.getString("country"));
                buyer.setUnionid(json.getString("unionid"));
                buyer.setActive(1);
                buyer.setUpdated(new Date());
                buyer.saveOrUpdate();
                return buyer;
            }
		}  catch (Exception e) {
			throw new UserException(e.getMessage());
		}
		return null;	
	}

	@Override
	public BuyerUser saveOrUpdate(String openId, String appId, String userInfoJson,  String accessIp) throws UserException {
		ApiResult userInfo = new ApiResult(userInfoJson);//
    	if(!userInfo.isSucceed()) throw new UserException(userInfo.getErrorMsg());
    	BuyerUser member = DAO.findFirst("select * from t_buyer_user where open_id=? ", openId);
        if (member == null) {
        	AuthUser authUser = authUserService.getAuthUserByAppId(appId);
        	member = new BuyerUser().setCreated(new Date()).setUpdated(new Date())
        			.setActive(1).setAppId(authUser.getId())
        			.setEmail("").setOpenId(openId).setLastLoginTime(new Date())
        			.setAccessIp(accessIp).setPassword("").setNickname(EmojiParser.removeAllEmojis(StrKit.isBlank(userInfo.getStr("nickname"))?"":userInfo.getStr("nickname")))
        			.setHeadimgurl(userInfo.getStr("headimgurl"))
        			.setSex(userInfo.getInt("sex")).setScore(0).setCity(userInfo.getStr("city"))
        			.setCountry(userInfo.getStr("country")).setProvince(userInfo.getStr("province"))
        			.setLanguage(userInfo.getStr("language")).setSubscribe(userInfo.getInt("subscribe") == null ? 0 :userInfo.getInt("subscribe"))
        			.setUnionid(userInfo.getStr("unionid"))
        			.setRemark(userInfo.getStr("remark"))
        			.setGroupid(userInfo.getInt("groupid"))
        			.setSubscribeScene(userInfo.getStr("subscribe_scene"));
		        	if(userInfo.getList("tagid_list") != null && userInfo.getList("tagid_list").size()>0){
		        		StringBuilder sb = new StringBuilder();
		            	for (int i = 0; i < userInfo.getList("tagid_list").size(); i++) {
		                    sb.append(userInfo.getList("tagid_list").get(i)).append(",");
		                }
		            	member.setTagidList(sb.toString().substring(0, sb.toString().length() - 1));
		        	}
		        	if(userInfo.get("subscribe_time") == null){
		        		member.setSubscribeTime(new Date());
		        	}else{
		        		Long lt = new Long(userInfo.getLong("subscribe_time"));
		        		Date date = new Date(lt);
		        		member.setSubscribeTime(date);
		        	}
		        	member.save();
            
        } else {
        	//更新用户名和用户头像
        	if(member.getNickname() == null || !member.getNickname().equals(userInfo.getStr("nickname"))){
        		if(StrKit.notBlank(userInfo.getStr("nickname")))
        			member.setNickname(EmojiParser.removeAllEmojis(userInfo.getStr("nickname")));
        	}
        	if(member.getHeadimgurl() == null || !member.getHeadimgurl().equals(userInfo.getStr("headimgurl"))){
        		member.setHeadimgurl(userInfo.getStr("headimgurl"));
        	}	
        	if(userInfo.get("subscribe_time") == null){
        		member.setSubscribeTime(new Date());
        	}else{
        		Long lt = new Long(userInfo.getLong("subscribe_time"));
        		Date date = new Date(lt);
        		member.setSubscribeTime(date);
        	}
        	
        	member.setSubscribe(1)
        	.setActive(1)
        	.setSubscribeTime(new Date())
            .setAccessIp(accessIp)
            .setUpdated(new Date())
            .setLastLoginTime(new Date())
            .setSubscribeScene(userInfo.getStr("subscribe_scene"));
        	if(userInfo.getList("tagid_list") != null && userInfo.getList("tagid_list").size()>0){
        		StringBuilder sb = new StringBuilder();
            	for (int i = 0; i < userInfo.getList("tagid_list").size(); i++) {
                    sb.append(userInfo.getList("tagid_list").get(i)).append(",");
                }
            	member.setTagidList(sb.toString().substring(0, sb.toString().length() - 1));
        	}
            member.update();
        }    
		return member;
	}

	@Override
	public BuyerUser findByOpenId(String openId) {
		return DAO.findFirst("select * from t_buyer_user where open_id=?", openId);
	}

	@Override
	public BuyerUser getUser(Long appId, String openId) {
		return DAO.findFirst("select * from "+ BuyerUser.table+ " where open_id=? and app_id=?", openId,appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.BuyerUserService#findByNick(java.lang.String)
	 */
	@Override
	public List<BuyerUser> findByNick(String nick) {
		Columns columns = Columns.create().likeAppendPercent("nickname", nick);
		return DAO.findListByColumns(columns);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.BuyerUserService#findByIdLock(java.lang.Object)
	 */
	@Override
	public BuyerUser findByIdLock(Object id) {
		return DAO.findFirst("select * from " + BuyerUser.table + " where id = ? for update ", id);
	}

}
