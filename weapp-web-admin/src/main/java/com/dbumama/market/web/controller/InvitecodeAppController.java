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
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.jfinal.captcha.CaptchaRender;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2017年11月10日
 */
@RequestMapping(value = "invitecode/app", viewPath="invitecodeapp")
public class InvitecodeAppController extends BaseAdminController{

	@RPCInject
	private InvitecodeRuleService invitecodeRuleService;
	@RPCInject
	private SellerUserService sellerUserService;
	
	public void index(){
		setAttr("seller", sellerUserService.findById(getSellerId()));
		setAttr("first", getPara("first"));
		render("incode_index_app.html");
	}
	
	public void gen(){
		try {
			rendSuccessJson(invitecodeRuleService.genInCode(getSellerUser()));
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void stat(){
		render("incode_stat_app.html");
	}
	
	public void statlist(){
		rendSuccessJson(sellerUserService.getPageSellerByIncode(getParaToInt("page"), getPageSize(), getSellerUser().getMyInviteCode(), getPara("phone")));
	}
	
	public void mission(){
		setAttr("totalMission", invitecodeRuleService.getCodeCash(getSellerId()));
		setAttr("mission", invitecodeRuleService.getCodeCangetCash(getSellerId()));
		render("incode_mission_app.html");
	}
	
	public void cash(){
		setAttr("mission", invitecodeRuleService.getCodeCangetCash(getSellerId()));
		render("incode_cash_app.html");
	}
	
	public void applycash(){
		final String captchaToken = getPara("captchaToken");
		final String tradePwd = getPara("trade_pwd");
		final String wantCash = getPara("want_cash");
		final String cashAccount = getPara("cash_account");
		//check 验证码
		if(!CaptchaRender.validate(this, captchaToken)){
			rendFailedJson("验证码错误");
			return;
		}
		try {
			invitecodeRuleService.applycash(getSellerId(), tradePwd, wantCash, cashAccount);
			rendSuccessJson();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void cashrcd(){
		render("incode_mission_rcd_app.html");
	}
	
	public void tradepwd(){
		render("incode_tradepwd_app.html");
	}
	
	public void cashrcdlist(){
		rendSuccessJson(invitecodeRuleService.getUserCashRcd(getParaToInt("page"), getPageSize(), getSellerId(), getParaToInt("status")));
	}
	
}
