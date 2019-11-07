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
package com.dbumama.market.web.controller.weapp;

import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.InvitecodeRuleService;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.dbumama.market.web.core.interceptor.ForbidInterceptor;
import com.dbumama.weixin.api.CompWeappstatApi;
import com.jfinal.aop.Clear;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2017年11月7日
 */
@RequestMapping(value="weapp/stat", viewPath="weapp")
public class WeappStatController extends BaseAppAdminController{

	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private SellerUserService sellerUserService;
	@RPCInject
	private InvitecodeRuleService invitecodeRuleService;
	
	public void index(){
		setAttr("totalMission", invitecodeRuleService.getCodeCash(getSellerId()));
		setAttr("mission", invitecodeRuleService.getCodeCangetCash(getSellerId()));
		setAttr("seller", sellerUserService.findById(getSellerId()));
		render("westat_index.html");
	}
	
	/**
	 * 概况
	 */
	@Clear(ForbidInterceptor.class)
	public void survey(){
		rendSuccessJson(CompWeappstatApi.getWeanalysisappiddailysummarytrend(
				authUserService.getAccessToken(getAuthUser()), 
				DateTimeUtil.getDateString(),
				DateTimeUtil.getDateString()));
	}
	
}
