/**
 * 文件名:BaseInterceptor.java
 * 版本信息:1.0
 * 日期:2015-5-10
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.model.WeimoApp;
import com.dbumama.market.model.WeimoAppUser;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.service.api.WeimoAppService;
import com.dbumama.market.service.api.WeimoAppUserService;
import com.dbumama.market.utils.SignKit;
import com.dbumama.market.web.core.shiro.WeimoLoginToken;
import com.dbumama.market.web.core.utils.IpKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.HttpKit;
import io.jboot.Jboot;
import io.jboot.web.session.JbootSessionConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微盟后台管理授权类
 * @author wangjun
 * 2018年11月30日
 */
public class WeimoLoginInterceptor implements Interceptor {
	
	public Logger logger = Logger.getLogger(getClass());
	
    final static String OAUTH_TOKEN_URL = "https://dopen.weimob.com/fuwu/b/oauth2/token";
    
    JbootSessionConfig jbootSessionConfig = Jboot.config(JbootSessionConfig.class);
    
    private SellerUserService sellerUserService = Jboot.service(SellerUserService.class);
    
    private WeimoAppService weimoAppService = Jboot.service(WeimoAppService.class);
    private WeimoAppUserService weimoAppUserService = Jboot.service(WeimoAppUserService.class);
    
	@Override
	public void intercept(Invocation ai) {
		
		Controller controller = ai.getController();
		HttpServletRequest request = controller.getRequest();
		
		//处理授权回调的情况不检查session
		final String state = request.getParameter("state");
		
		final String sign = getSign(state);
		final String endTime = getEndDate(state);
		final String version = getVersion(state);
		List<WeimoApp> weimoApps = weimoAppService.findList();
		
		WeimoApp currAuthApp = null;
		for(WeimoApp weimoApp : weimoApps){
			String _sign;
			try {
				_sign = SignKit.md5(weimoApp.getAppSecret() + endTime + version);
				if(_sign.equalsIgnoreCase(sign)){
					currAuthApp = weimoApp;
					break;
				}
			} catch (IOException e) {
			}
		}
		
		if(currAuthApp == null) throw new RuntimeException("currAuthApp is null");
		
		final String code = request.getParameter("code");
        final String error = request.getParameter("error");
        final String error_description = request.getParameter("error");
        if (StringUtils.isNotBlank(error)) throw new RuntimeException("授权失败：" + error_description);
        
        final String result = post(genOauthMap(code, getRedirectUrl(request), currAuthApp));
        if (StringUtils.isBlank(result)) throw new RuntimeException("授权失败,授权返回result is null");
        
        JSONObject accessTokenJson = JSON.parseObject(result);
        if(accessTokenJson.getInteger("error") != null) throw new RuntimeException("授权失败," + accessTokenJson.getString("error_description"));
        
        final String accessToken = accessTokenJson.getString("access_token");
        final String getUserUrl = "http://dopen.weimob.com/api/1_0/open/usercenter/getWeimobUserInfo?accesstoken="+accessToken;
        String userJson = HttpKit.get(getUserUrl);
        JSONObject respJson = JSONObject.parseObject(userJson);
        JSONObject errorJson = respJson.getJSONObject("code");
        
        if(errorJson != null && !"success".equals(errorJson.getString("errmsg"))){
        	throw new RuntimeException("get user info error,error msg:" + errorJson.getString("errmsg"));
        }
        
        JSONObject userDataJson = respJson.getJSONObject("data");
        final String pid = userDataJson.getString("pid");
        final String name = userDataJson.getString("name");
        final String avatarUrl = userDataJson.getString("avatarUrl");
        
        //final String token_type = accessTokenJson.getString("token_type");
//        final Integer expires_in = accessTokenJson.getInteger("expires_in");
//        final String refresh_token = accessTokenJson.getString("refresh_token");
//        final Integer refresh_token_expires_in = accessTokenJson.getInteger("refresh_token_expires_in");
        //final String scope = accessTokenJson.getString("scope");
//        final String business_id = accessTokenJson.getString("business_id");	//微盟商户id
//        final String public_account_id = accessTokenJson.getString("public_account_id");	//微盟商户的公众号id
        
        SellerUser seller = sellerUserService.findByWeimoPid(pid);
		
		if(seller == null){
			seller = new SellerUser();
			seller.setWeimoPid(pid).setCreated(new Date()).setActive(1);
		}
		
		seller.setNick(name).setHeaderImg(avatarUrl).setAccessToken(accessToken).setUpdated(new Date());
		sellerUserService.saveOrUpdate(seller);
		
		//新增用户保存后，再查一次就有sellerId了，嘿嘿。。。
		seller = sellerUserService.findByWeimoPid(pid);
		
		WeimoAppUser weimoAppUser = weimoAppUserService.findWeimoAppUser(seller.getId(), currAuthApp.getAppId(), version, new Date(Long.valueOf(endTime)));
		if(weimoAppUser == null){
			//记录用户应用订购信息
			weimoAppUser = new WeimoAppUser();
			weimoAppUser.setSellerId(seller.getId()).setAppId(currAuthApp.getAppId()).setVersion(version).setEndDate(new Date(Long.valueOf(endTime))).setActive(true).setCreated(new Date()).setUpdated(new Date());
			weimoAppUserService.save(weimoAppUser);
		}
		
		//直接做登录
		WeimoLoginToken token = new WeimoLoginToken(seller.getWeimoPid(), seller.getAccessToken());
        Subject subject = SecurityUtils.getSubject();
        token.setRememberMe(false);
        subject.login(token);
        if (subject.isAuthenticated()) {
       
        	controller.setCookie(jbootSessionConfig.getCookieName(), (String)subject.getSession().getId(), jbootSessionConfig.getCookieMaxAge());
            if (controller.getParaToBoolean("rememberMe") != null && controller.getParaToBoolean("rememberMe")) {
            	token.setRememberMe(true);
            	controller.setCookie("loginName", seller.getNick(), 60 * 60 * 24 * 7);
            } else {
            	controller.removeCookie("loginName");
            }
            seller.setLoginIp(IpKit.getRealIpV2(controller.getRequest())).setLoginTime(new Date());
            seller.setUpdated(new Date());
            sellerUserService.saveOrUpdate(seller);
            controller.redirect("/index");
        }
        
	}
	
	private String getSign(String state){
		String [] states = state.split(";");
		return states == null || states.length !=3 ? null : states[0].split(":")[1];
	}
	
	private String getEndDate(String state){
		String [] states = state.split(";");
		return states == null || states.length !=3 ? null : states[1].split(":")[1];
	}
	
	private String getVersion(String state){
		String [] states = state.split(";");
		return states == null || states.length !=3 ? null : states[2].split(":")[1];
	}
	
	private String getRedirectUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + JFinal.me().getContextPath() + "/weimologin";
    }

    private String post(Map<String, String> map) {
    	return HttpKit.post(OAUTH_TOKEN_URL, map, "");
    }

    Map<String, String> genOauthMap(String code, String rUrl, WeimoApp weimoApp) {
        Map<String, String> props = new HashMap<String, String>();
        props.put("code", code);
        props.put("client_id", weimoApp.getAppClientid());
        props.put("client_secret", weimoApp.getAppSecret());
        props.put("redirect_uri", rUrl);
        //props.put("view", "web");
        props.put("grant_type", "authorization_code");
        return props;
    }

}
