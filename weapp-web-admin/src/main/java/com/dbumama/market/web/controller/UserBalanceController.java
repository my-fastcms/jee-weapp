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

import java.math.BigDecimal;

import com.dbumama.market.service.api.PayService;
import com.dbumama.market.service.api.SelleruserBalanceRcdService;
import com.dbumama.market.service.api.SelleruserRechargeRcdService;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.jfinal.core.NotAction;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.utils.RequestUtil;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2019年1月22日
 */
@RequestMapping(value = "user/balance", viewPath = "user")
public class UserBalanceController extends BaseAdminController{

	@RPCInject
	private PayService payService;
	@RPCInject
	private SelleruserRechargeRcdService selleruserRechargeRcdService;
	@RPCInject
	private SelleruserBalanceRcdService selleruserBalanceRcdService;
	
	public void index(){
		String url = payService.prepareToPay4pc(getTradeNo(), new BigDecimal(300), "红包余额充值", RequestUtil.getIpAddress(getRequest()));
		setAttr("qrcode", url);
		render("user_balance_index.html");
	}
	
	public void qrcode(BigDecimal payFee){
		String url = payService.prepareToPay4pc(getTradeNo()+"", payFee, "红包余额充值", RequestUtil.getIpAddress(getRequest()));
		renderSuccess(url);
	}
	
	public void recharge(){
		render("user_recharge_index.html");
	}
	
	public void rechlist(){
		rendSuccessJson(selleruserRechargeRcdService.list(getSellerId(), getPageNo(), getPageSize()));
	}
	
	public void rcd(){
		render("user_balance_rcd.html");
	}
	
	public void rcdlist(){
		rendSuccessJson(selleruserBalanceRcdService.list(getSellerId(), getPageNo(), getPageSize()));
	}
	
	@NotAction
	private synchronized String getTradeNo(){
		String scenceId = "e";
		String scenceIdStr = String.valueOf(System.currentTimeMillis());
		scenceId = scenceId + scenceIdStr.substring(0, 10) + "_" + getSellerId();
		return scenceId;
	}
	
}
