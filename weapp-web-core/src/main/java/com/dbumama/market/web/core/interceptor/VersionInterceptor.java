/**
 * 文件名:VersionInterceptor.java
 * 版本信息:1.0
 * 日期:2015-8-26
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.core.interceptor;

import org.apache.log4j.Logger;

import com.dbumama.market.web.core.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-8-26
 */
@Deprecated
public class VersionInterceptor implements Interceptor{

	public Logger logger = Logger.getLogger(getClass());
	
	final static String exclusions [] = {"/login", "/logout", "/pay", "/pay/create", "/weixin/msg", "/wx/message",
			"/register", "/doRegister", "/sendCode"};
	
	@Override
	public void intercept(Invocation ai) {
		Controller controller = ai.getController();
		/*if(controller instanceof BaseController == false)
			throw new RuntimeException("must extends BaseController");*/
		
		final String uri = controller.getRequest().getRequestURI();
		for(String exclu : exclusions){
			if(uri.contains(exclu)){
				ai.invoke();
				return;
			}
		}
		
		if(controller instanceof BaseController == true){
		}
		
		ai.invoke();
	}

}
