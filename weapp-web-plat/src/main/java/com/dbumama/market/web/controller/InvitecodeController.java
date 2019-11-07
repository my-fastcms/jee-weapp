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

import com.dbumama.market.service.api.InvitecodeRuleService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BasePlatController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2017年11月10日
 */
@RequestMapping(value="invitecode")
public class InvitecodeController extends BasePlatController {

	@RPCInject
	private InvitecodeRuleService invitecodeService;
	
	public void index(){
		render("incode_index.html");
	}

	public void list(){
		rendSuccessJson(invitecodeService.getPlatIncodeUser(getPageNo(), getPageSize(), getPara("phone")));
	}
	
	public void rule(){
		setAttr("incodeRule", invitecodeService.findRule());
		render("incode_rule.html");
	}
	
	public void saveRule(){

	}
	
	public void cash(){
		render("incode_cash.html");
	}
	
	public void cashlist(){
		rendSuccessJson(invitecodeService.getPlatCashRcd(getPageNo(), getPageSize(), getPara("phone"), getParaToInt("status")));
	}
	
	public void confirmCash(){
		try {
			invitecodeService.confirmCash(getParaToLong("id"));
			rendSuccessJson();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void cancelCash(){
		try {
			invitecodeService.cancelCash(getParaToLong("id"), getPara("content"));
			rendSuccessJson();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}
