package com.dbumama.market.web.core.interceptor;

import com.dbumama.market.WeappConstants;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.Jboot;

import javax.servlet.http.HttpServletRequest;

/**
 * wjun_java@163.com
 * 2016年7月9日
 */
public abstract class AbstractMobileInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation ai) {
		doIntercept(ai);
	}
	
	protected abstract void doIntercept(Invocation inv);
	
	protected String getAuthAppId(HttpServletRequest request){
		if(Jboot.isDevMode()){
			return WeappConstants.MOBILE_TEST_AUTH_APPID;
		}
		return request.getServerName().substring(0, request.getServerName().indexOf("."));
	}
	
}
