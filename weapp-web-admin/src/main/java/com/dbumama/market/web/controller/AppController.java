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

import com.dbumama.market.model.AuthUserApp;
import com.dbumama.market.model.AuthUserTemplate;
import com.dbumama.market.model.PlatActivitys;
import com.dbumama.market.model.WeappTemplate;
import com.dbumama.market.service.api.AppService;
import com.dbumama.market.service.api.AuthUserAppService;
import com.dbumama.market.service.api.AuthUserTemplateService;
import com.dbumama.market.service.api.PlatActivitysJoinService;
import com.dbumama.market.service.api.PlatActivitysService;
import com.dbumama.market.service.api.WeappTemplateService;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2018年9月1日
 */
@RequestMapping(value="app")
public class AppController extends BaseAuthUserController{

	@RPCInject
	private AppService appService;
	@RPCInject
	private PlatActivitysService platActivitysService; 
	@RPCInject
	private PlatActivitysJoinService platActivitysJoinService; 
	@RPCInject
	private AuthUserAppService authUserAppService;
	@RPCInject
	private AuthUserTemplateService authUserTemplateService;
	@RPCInject
	private WeappTemplateService weappTemplateService;
	
	public void center(){
		PlatActivitys platActivitys = platActivitysService.getOnline();
		setAttr("activity", platActivitys);
		if(platActivitys != null)
		setAttr("isJoin", platActivitysJoinService.findIsJoin(getAuthUserId(), platActivitys.getId()));
		
		if(StrKit.notBlank(getAuthUser().getMiniprograminfo())){
			//小程序
			AuthUserTemplate userTemplate = authUserTemplateService.getAuthUserTemplate(getAuthUserId());
			if(userTemplate != null){
				WeappTemplate template = weappTemplateService.findById(userTemplate.getTemplateId());
				setAttr("template", template);				
			}
			List<WeappTemplate> templates = weappTemplateService.findAll();
			setAttr("templates", templates);
			render("/weapp/weapp_index.html");
		}else{
			//公众号
			render("app_index.html");			
		}
		
	}
	
	public void getApps(){
		rendSuccessJson(appService.findApps(getAuthUser()));
	}
	
	public void getAppEndDate(){
		List<AuthUserApp> userApps = authUserAppService.list(getAuthUserId()); 
		rendSuccessJson(userApps);
	}
}
