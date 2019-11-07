package com.dbumama.market.web._common;

import com.dbumama.market.web.core.config.WebConfigListener;
import com.dbumama.market.web.core.handler.ShiroHandler;
import com.dbumama.market.web.core.route.PlatRoutes;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.JbootApplication;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

public class PlatWebConfigListener extends WebConfigListener {
	
	static final Log log = Log.getLog(PlatWebConfigListener.class);
	
	public static void main(String [] args){
        JbootApplication.run(args);
    }

	@Override
	public void onConstantConfig(Constants me) {
		super.onConstantConfig(me);		
	}

	@Override
	public void onRouteConfig(Routes routes) {
		super.onRouteConfig(routes);
		routes.add(new PlatRoutes());
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		super.onInterceptorConfig(interceptors);
	}

	@Override
	public void onPluginConfig(JfinalPlugins plugins) {
		super.onPluginConfig(plugins);
	}

	@Override
	public void onEngineConfig(Engine engine) {
		super.onEngineConfig(engine);
	}

	@Override
	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
		super.onFixedInterceptorConfig(fixedInterceptors);
	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {
		super.onHandlerConfig(handlers);
		handlers.add(new ShiroHandler());
	}

	@Override
	public void onStart() {
		super.onStart();
		log.info("wxmall plat server is ready ...");
		log.info("classpath：" + PathKit.getRootClassPath());
	}
	
}
