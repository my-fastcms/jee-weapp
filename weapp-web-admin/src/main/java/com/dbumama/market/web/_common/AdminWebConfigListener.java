package com.dbumama.market.web._common;

import com.dbumama.market.web.core.config.WebConfigListener;
import com.dbumama.market.web.core.handler.WeappSessionHandler;
import com.dbumama.market.web.core.interceptor.CSRFInterceptor;
import com.dbumama.market.web.core.menu.AdminMenuManager;
import com.dbumama.market.web.core.route.AdminRoutes;
import com.dbumama.market.web.core.ueditor.UeditorConfigKit;
import com.dbumama.market.web.core.ueditor.manager.AliyunossFileManager;
import com.dbumama.market.web.core.wechat.WechatComponentManager;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

public class AdminWebConfigListener extends WebConfigListener {
	
	static final Log log = Log.getLog(AdminWebConfigListener.class);
	
	@Override
	public void onRouteConfig(Routes routes) {
		super.onRouteConfig(routes);
		routes.add(new AdminRoutes());
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		super.onInterceptorConfig(interceptors);
		
		interceptors.add(new CSRFInterceptor());
	}

	@Override
	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
		super.onFixedInterceptorConfig(fixedInterceptors);
	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {
		super.onHandlerConfig(handlers);
		handlers.add(new WeappSessionHandler());
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.config.WebConfigListener#onEngineConfig(com.jfinal.template.Engine)
	 */
	@Override
	public void onEngineConfig(Engine engine) {
		super.onEngineConfig(engine);
		
		final String appMode = JbootConfigManager.me().getConfigValue("jboot.app.mode");
		
		if(appMode == null || !"product".equals(appMode))
			engine.setBaseTemplatePath(PathKit.getRootClassPath() + "/webapp/WEB-INF/template");
		else
			engine.setBaseTemplatePath(PathKit.getWebRootPath() + "/WEB-INF/template");
		
//		engine.addSharedFunction("/_includes/_layout.html");
	}

	@Override
	public void onStart() {
		
		super.onStart();
		
		AdminMenuManager.me.init();
		
		WechatComponentManager.me.init();
		
		UeditorConfigKit.setFileManager(new AliyunossFileManager());
		
		log.info("wxmall admin server is ready ...");
		log.info("classpath：" + PathKit.getRootClassPath());
		log.info("web root path:" + PathKit.getWebRootPath());
	}
	
}
