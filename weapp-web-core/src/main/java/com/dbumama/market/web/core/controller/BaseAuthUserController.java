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
package com.dbumama.market.web.core.controller;

import org.apache.shiro.SecurityUtils;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.web.core.interceptor.AuthUserInterceptor;
import com.jfinal.aop.Before;

/**
 * @author wangjun
 * 2019年6月6日
 */
@Before(AuthUserInterceptor.class)
public class BaseAuthUserController extends BaseAdminController{

	protected AuthUser getAuthUser(){
		return (AuthUser) SecurityUtils.getSubject().getSession().getAttribute(WeappConstants.WEB_WEAPP_IN_SESSION);
	}
	
	protected Long getAuthUserId(){
		return getAuthUser() == null ? null : getAuthUser().getId();
	}
	
	protected String getAppId(){
		return getAuthUser() == null ? null : getAuthUser().getAppId();
	}
	
}
