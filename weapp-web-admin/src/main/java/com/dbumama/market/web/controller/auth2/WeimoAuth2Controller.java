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
package com.dbumama.market.web.controller.auth2;

import com.dbumama.market.web.core.controller.BaseAdminController;
import com.dbumama.market.web.core.interceptor.WeimoAuthInterceptor;
import com.jfinal.aop.Before;

import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2018年7月4日
 */
@RequestMapping(value = "weimo/auth")
@Before(WeimoAuthInterceptor.class)
public class WeimoAuth2Controller extends BaseAdminController {

	public void index(){
		
	}
	
	public void callback(){
		
	}
	
}
