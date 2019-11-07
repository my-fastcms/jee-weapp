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

import com.dbumama.market.service.api.AuthWeimoService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2018年11月5日
 */
@RequestMapping(value = "authweimo", viewPath="authshop")
public class WeimoController extends BaseAppAdminController{

	@RPCInject
	private AuthWeimoService authWeimoService;
	
	public void index(){
		render("auth_weimo_index.html");
	}
	
	public void list(){
		renderSuccess(authWeimoService.list(getSellerId(), getAuthUserId(), getPageNo(), getPageSize()));
	}
	
}
