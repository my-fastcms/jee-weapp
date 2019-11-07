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
import com.dbumama.market.model.AuthWeimo;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.AuthWeimoService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.HttpKit;
import io.jboot.Jboot;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微盟后台管理授权类
 * @author: wjun.java@gmail.com
 * @date:2015-5-10
 */
public class WeimoAuthInterceptor implements Interceptor {
	
	public Logger logger = Logger.getLogger(getClass());
	
    final static String OAUTH_TOKEN_URL = "https://dopen.weimob.com/fuwu/b/oauth2/token";
    
    private AuthWeimoService authWeimoService = Jboot.service(AuthWeimoService.class);
    
	@Override
	public void intercept(Invocation ai) {
		
		if(SecurityUtils.getSubject() == null 
				|| SecurityUtils.getSubject().getPrincipal() == null
				|| !SecurityUtils.getSubject().isAuthenticated()){
			ai.invoke();
			return;
		}
		
		SellerUser seller = (SellerUser) SecurityUtils.getSubject().getPrincipal();
		if(seller == null){
			ai.invoke();
			return;
		}
		
		Controller controller = ai.getController();
		final String uri = controller.getRequest().getRequestURI();
		HttpServletRequest request = controller.getRequest();
        if (! (JFinal.me().getContextPath() + "/weimo/auth/callback").equals(uri)) {
//        	gotoOauth(ai.getController(), wxmallConfig.getWeimoAppId());
        }else {
        	//处理授权回调的情况不检查session
        	final String rUrl = StringUtils.isBlank(request.getParameter("rUrl")) ? "/" : request.getParameter("rUrl");
            final String code = request.getParameter("code");
            final String error = request.getParameter("error");
            final String error_description = request.getParameter("error");
            if (StringUtils.isNotBlank(error)) throw new RuntimeException("授权失败：" + error_description);
            final String result = post(genOauthMap(code, getRedirectUrl(request, rUrl)));
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
            final Integer expires_in = accessTokenJson.getInteger("expires_in");
            final String refresh_token = accessTokenJson.getString("refresh_token");
            final Integer refresh_token_expires_in = accessTokenJson.getInteger("refresh_token_expires_in");
            //final String scope = accessTokenJson.getString("scope");
            final String business_id = accessTokenJson.getString("business_id");	//微盟商户id
            final String public_account_id = accessTokenJson.getString("public_account_id");	//微盟商户的公众号id
            
            AuthWeimo authWeimo = authWeimoService.findByPid(pid);
            if(authWeimo == null){
            	authWeimo = new AuthWeimo();
            	authWeimo.setSellerId(seller.getId());
            	authWeimo.setCreated(new Date());
            	authWeimo.setActive(1);
            	authWeimo.setPid(pid);
            }
            
            authWeimo.setShopName(name).setShopLogo(avatarUrl).setAccessToken(accessToken).setExpiresIn(expires_in)
            	.setRefreshToken(refresh_token).setRefreshTokenExpiresIn(refresh_token_expires_in)
            	.setBusinessId(business_id).setPublicAccountId(public_account_id).setUpdated(new Date());
            
            authWeimoService.saveOrUpdate(authWeimo);
            
            controller.redirect(rUrl);
        }
	}
	
	private String getRedirectUrl(HttpServletRequest request, String rUrl) {
        return "http://" + request.getServerName() + JFinal.me().getContextPath() + "/weimo/auth/callback" + "?rUrl=" + (StringUtils.isNotBlank(rUrl) ? rUrl : "");
    }

	private void gotoOauth(Controller controller, String appKey) {
		String rurl = controller.getRequest().getServletPath();
        if (controller.getRequest().getQueryString() != null) {
        	rurl = "?" + controller.getRequest().getQueryString();
            rurl = rurl.split("=")[1];	
        }
        final String oathUrl = "https://dopen.weimob.com/fuwu/b/oauth2/authorize?enter=wm&view=pc&response_type=code&scope=default&client_id="+appKey+"&redirect_uri="+getRedirectUrl(controller.getRequest(), rurl)+"&state=wxmall";
        controller.redirect(oathUrl);
    }
    
    private String post(Map<String, String> map) {
    	return HttpKit.post(OAUTH_TOKEN_URL, map, "");
    }

    Map<String, String> genOauthMap(String code, String rUrl) {
        Map<String, String> props = new HashMap<String, String>();
        props.put("code", code);
//        props.put("client_id", wxmallConfig.getWeimoAppId());
//        props.put("client_secret", wxmallConfig.getWeimoAppSecret());
        props.put("redirect_uri", rUrl);
        //props.put("view", "web");
        props.put("grant_type", "authorization_code");
        return props;
    }

}
