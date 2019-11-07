package com.dbumama.market.web.core.handler;

import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.utils.SignKit;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.dbumama.weixin.api.CompJsTicket;
import com.dbumama.weixin.api.CompJsTicketApi;
import com.dbumama.weixin.api.CompJsTicketApi.JsApiType;
import io.jboot.Jboot;
import io.jboot.utils.RequestUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.TreeMap;

/**
 * 准备调用微信JS SDK接口需要的参数
 * wjun_java@163.com
 * 2015年12月15日
 */
@Deprecated
public class WeixinJssdkHandler extends Handler {
	
	public Logger log = Logger.getLogger(getClass());
	
	private AuthUserService authUserService = Jboot.service(AuthUserService.class);
	/**
	 * 需要准备分享参数的url
	 */
	final static String inclusions [] = {"/lottery/draw/", "/product/detail/", "/product/join/", "/jifen/poster/", "/task/poster/", "/assisfree/", "/assisfree/user/"};
	
	/* (non-Javadoc)
	 * @see com.jfinal.handler.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean[])
	 */
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if(RequestUtil.isAjaxRequest(request) || RequestUtil.isMultipartRequest(request)){
			next.handle(target, request, response, isHandled);
			return;
		}
		
		if(target.contains("/resources/") || target.contains(".css") || target.contains(".js")){
			return;
		}
		
		if(check(target)){
			String mainUrl = "http://" + request.getServerName();
	        String rurl = mainUrl + request.getServletPath();
	        if (request.getQueryString() != null) {
	            rurl += "?" + request.getQueryString();
	        }
			String nonceStr = SignKit.genRandomString32();
			if(authUserService.getAuthUserByAppId(getAppId(request)) != null){
				CompJsTicket compJsTicket = CompJsTicketApi.getTicket(JsApiType.jsapi, getAppId(request), authUserService.getAccessToken(authUserService.getAuthUserByAppId(getAppId(request))));
				if(compJsTicket != null && compJsTicket.isSucceed()){
					final String jsapiTicket = compJsTicket.getTicket();
					final String timestamp = String.valueOf(new Date().getTime());

					// 准备调用支付js接口的参数
					TreeMap<String, Object> params = new TreeMap<String, Object>();
					params.put("jsapi_ticket", jsapiTicket);
					params.put("noncestr", nonceStr);
					params.put("timestamp", timestamp);
					params.put("url", rurl);
					
					request.setAttribute("signature", SignKit.signSHA1(params));
					request.setAttribute("nonceStr", nonceStr);
					request.setAttribute("timestamp", timestamp);
					request.setAttribute("appId", getAppId(request));
					request.setAttribute("requestUrl", rurl);
				}
			}
		}
		
		next.handle(target, request, response, isHandled);
	}
	
	boolean check(String target){
		for(String inclus : inclusions){
			if(inclus.equals(target) || inclus.contains(target)){
				return true;
			}
		}
		return false;
	}
	
	protected String getAppId(HttpServletRequest request){
		if(JFinal.me().getConstants().getDevMode()){
			return WeappConstants.WECHAT_LOGIN_APPID;
		}
		String serverName = request.getServerName();
		serverName = serverName.substring(0, serverName.indexOf("."));
		return serverName;
	}

}
