/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.web.core.config;

import com.dbumama.market.WeappConstants;
import com.dbumama.market.web.core.interceptor.Wxm18nInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

/**
 * @author wangjun
 *
 */
public abstract class WebConfigListener extends JbootAppListenerBase {
	
	static final Log log = Log.getLog(WebConfigListener.class);
	
	@Override
	public void onConstantConfig(Constants me) {
		super.onConstantConfig(me);
		
		me.setDevMode(JFinal.me().getConstants().getDevMode());
		me.setBaseUploadPath("upload/image");
		me.setEncoding("UTF-8");
		me.setI18nDefaultBaseName("i18n");
		me.setI18nDefaultLocale("zh_CN");
		me.setError401View("/404.html");
		me.setError403View("/403.html");
		me.setError404View("/404.html");
		me.setError500View("/500.html");
	}

	@Override
	public void onRouteConfig(Routes routes) {
		super.onRouteConfig(routes);
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		super.onInterceptorConfig(interceptors);
		interceptors.add(new Wxm18nInterceptor());
		interceptors.add(new SessionInViewInterceptor());
	}

	@Override
	public void onPluginConfig(JfinalPlugins plugins) {
		super.onPluginConfig(plugins);
	}

	@Override
	public void onEngineConfig(Engine engine) {
		super.onEngineConfig(engine);
		engine.setBaseTemplatePath(PathKit.getWebRootPath() + "/WEB-INF/template");
		engine.setDatePattern("yyyy-MM-dd HH:mm:ss");

		engine.addSharedObject("env", JbootConfigManager.me().getConfigValue("jboot.app.mode"));
		engine.addSharedObject("webctx", JFinal.me().getContextPath());
		engine.addSharedObject("locale", "zh_CN");
		engine.addSharedObject("img_domain", WeappConstants.IMAGE_DOMAIN);
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
	}
	
	@Override
    public void onInit() {
		//Aop.getAopFactory().setInjectDepth(7);
    }
	
}
