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

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.service.api.MarketcodeApplyService;
import com.dbumama.market.service.api.MarketcodeException;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2019年8月1日
 */
@RequestMapping(value = "marketcode")
public class MarketcodeController extends BaseApiController{

	@RPCInject
	private MarketcodeApplyService marketcodeApplyService;
	
	@Before({POST.class, ApiSessionInterceptor.class})
	public void process(){
		
		final String codeTicket = getJSONPara("codeTicket");
		
		try {
			//用户扫码，通过小程序codeTicket换取扫码信息
			ApiResult result = marketcodeApplyService.ticket2code(getAuthUserId(), getBuyerOpenId(), codeTicket);
			
			if(!result.isSucceed() || StrKit.isBlank(result.getStr("code"))){
				rendFailedJson(result.getErrorMsg());			
			}else{
				//获取二维码信息
				final String code = result.getStr("code");//原始码
				final int applicationId = result.getInt("application_id");
				final String isvApplicationId = result.getStr("isv_application_id");
	//			final String activityName = result.getStr("activity_name");
	//			final String productBrand = result.getStr("product_brand");
	//			final String productTitle = result.getStr("product_title");
				final String wxaAppid = result.getStr("wxa_appid");
	//			final String wxaPath = result.getStr("wxa_path");
	//			final int codeStart = result.getInt("code_start");
	//			final int codeEnd = result.getInt("code_end");
				
				//一物一码扫码入口
				//1.如果是积分小程序
				//2.如果是抽奖小程序
				//3.如果是分销小程序
				//4.如果是直接发红包小程序
				//不同的小程序，对应不同的逻辑
				
				//获取一物一码小程序扫码配置规则
				//根据规则进行对应的逻辑处理
				marketcodeApplyService.process(isvApplicationId, code, applicationId, wxaAppid, getBuyerOpenId());
				rendSuccessJson();
			}
		} catch (MarketcodeException e) {
			rendFailedJson(e.getMessage());
		}
		
	} 
	
}
