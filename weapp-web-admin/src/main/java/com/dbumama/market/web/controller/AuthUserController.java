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
package com.dbumama.market.web.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.web.core.controller.BaseAdminController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2018年8月3日
 */
@RequestMapping(value = "authuser", viewPath = "weapp")
public class AuthUserController extends BaseAdminController{

	@RPCInject
	private AuthUserService authUserService;
	
	public void index(){
		List<AuthUser> authapps = authUserService.getSellerAllAuthUsers(getSellerId());
		setAttr("authapps", authapps);
		render("auth_user_index.html");
	}
	
	public void set(){
		AuthUser authUser = authUserService.findById(getParaToLong("weappId"));
		SecurityUtils.getSubject().getSession().setAttribute(WeappConstants.WEB_WEAPP_IN_SESSION, authUser);
		redirect("/app/center");
	}
}
