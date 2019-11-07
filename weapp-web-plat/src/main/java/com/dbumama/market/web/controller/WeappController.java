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

import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.web.core.controller.BaseController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2017年11月8日
 */
@RequestMapping(value="weapp")
public class WeappController extends BaseController{
	
	@RPCInject
	private AuthUserService authUserService;
	
	public void index(){
		render("weapp_index.html");
	}
	
	public void list(){
		rendSuccessJson(authUserService.list(getPageNo(), getPageSize(), getParaToInt("active"), getPara("qname")));
	}
	
}
