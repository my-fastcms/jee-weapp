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
package com.dbumama.market.web._common;

import com.dbumama.market.web.core.config.WebConfigListener;
import com.dbumama.market.web.core.interceptor.MobileSessionInterceptor;
import com.dbumama.market.web.core.route.MobileRoutes;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import io.jboot.aop.jfinal.JfinalHandlers;

/**
 * @author wangjun
 *
 */
public class MobileWebConfigListener extends WebConfigListener{

	static final Log log = Log.getLog(MobileWebConfigListener.class);
	
	@Override
	public void onRouteConfig(Routes routes) {
		super.onRouteConfig(routes);
		routes.add(new MobileRoutes());
	}

	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		super.onInterceptorConfig(interceptors);
		interceptors.add(new MobileSessionInterceptor());
	}

	@Override
	public void onHandlerConfig(JfinalHandlers handlers) {
		super.onHandlerConfig(handlers);
	}

	@Override
	public void onStart() {
		super.onStart();
		log.info("wxmall mobile server is ready ...");
		log.info("classpath：" + PathKit.getRootClassPath());
	}
	
}
