/**
 * 文件名:BaseController.java
 * 版本信息:1.0
 * 日期:2015-5-9
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.core.controller;

import com.dbumama.market.WeappConstants;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.service.api.AuthUserService;
import com.jfinal.core.JFinal;
import io.jboot.Jboot;
import org.apache.log4j.Logger;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-5-9
 */
public abstract class BaseMobileController extends BaseController{
	
	public static final String ORDER_SESSION_KEY = "orders"; //购物车临时订单数据
	protected static final String CACHENAME_COMP_TIKET = "comp_tiket_cache";
	protected static final String CACHENAME_COMP_TIKET_KEY = "key_comp_tiket";

	private AuthUserService authUserService = Jboot.service(AuthUserService.class);
	
	public Logger log = Logger.getLogger(getClass());

	protected final static String SYSTEM_ERROR_MSG = "系统错误";
	
	protected BuyerUser getBuyerUser(){
		return (BuyerUser)getSession().getAttribute(WeappConstants.BUYER_USER_IN_SESSION);
	}
	
	protected Long getBuyerId(){
		return getBuyerUser() == null ? null : getBuyerUser().getId();
	}
	
	protected String getOpenId(){
		return getBuyerUser() == null ? null : getBuyerUser().getOpenId();
	}
	
	protected String getAppId(){
		if(JFinal.me().getConstants().getDevMode()){
			return WeappConstants.MOBILE_TEST_AUTH_APPID;
		}
		String serverName = getRequest().getServerName();
		serverName = serverName.substring(0, serverName.indexOf("."));
		return serverName;
	}
	
	protected AuthUser getAuthUser(){
		return authUserService.getAuthUserByAppId(getAppId());
	}
	
	protected Long getAuthUserId(){
		return getAuthUser() == null ? null : getAuthUser().getId();
	}
	
}
