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

import com.dbumama.market.web.core.config.WxmWebConfig;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;

import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.core.listener.JbootAppListenerManager;

/**
 * @author wangjun
 * 2017年7月10日
 */
public class WebConfig extends WxmWebConfig{

	@Override
	public void configConstant(Constants constants) {
		super.configConstant(constants);
		constants.setMaxPostSize(1024 * 1024 * 2);
	}
	
	@Override
    public void configHandler(Handlers handlers) {

		super.configHandler(handlers);
		
		handlers.getHandlerList().clear();
		
        JbootAppListenerManager.me().onHandlerConfig(new JfinalHandlers(handlers));
    }
	
}
