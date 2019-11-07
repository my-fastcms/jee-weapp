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
package com.dbumama.market.web.core.interceptor;

import org.apache.shiro.SecurityUtils;

import com.dbumama.market.WeappConstants;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @author wangjun
 * 2018年9月5日
 */
public class AuthUserInterceptor implements Interceptor{

	/* (non-Javadoc)
	 * @see com.jfinal.aop.Interceptor#intercept(com.jfinal.aop.Invocation)
	 */
	@Override
	public void intercept(Invocation inv) {
		if(SecurityUtils.getSubject() !=null 
				&& SecurityUtils.getSubject().getSession() != null 
				&& SecurityUtils.getSubject().getSession().getAttribute(WeappConstants.WEB_WEAPP_IN_SESSION) == null){
			inv.getController().redirect("/authuser");
		}else{
			inv.invoke();
		}
	}

}
