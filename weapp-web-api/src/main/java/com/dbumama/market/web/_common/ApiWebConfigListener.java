package com.dbumama.market.web._common;

import com.dbumama.market.web.core.interceptor.BaseApiInterceptor;
import com.dbumama.market.web.core.route.ApiRoutes;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.app.JbootApplication;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

public class ApiWebConfigListener extends JbootAppListenerBase {
	
	static final Log log = Log.getLog(ApiWebConfigListener.class);
	
	public static void main(String [] args){
        JbootApplication.run(args);
    }

	@Override
	public void onRouteConfig(Routes routes) {
		super.onRouteConfig(routes);
		routes.add(new ApiRoutes());
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		super.onInterceptorConfig(interceptors);
		interceptors.add(new BaseApiInterceptor());
	}
	
	@Override
	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
		super.onFixedInterceptorConfig(fixedInterceptors);
	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {
		super.onHandlerConfig(handlers);
	}

	@Override
	public void onStart() {
		super.onStart();
		log.info("wxmall api server is ready ...");
		log.info("classpath：" + PathKit.getRootClassPath());
	}
	
}
