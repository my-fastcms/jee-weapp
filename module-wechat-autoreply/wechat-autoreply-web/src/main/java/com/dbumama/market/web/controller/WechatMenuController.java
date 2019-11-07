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

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.MenuEventService;
import com.dbumama.market.service.api.MenuReplyConfigResDto;
import com.dbumama.market.service.api.MenuReplyConfigService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.dbumama.weixin.api.CompMediaApi;
import com.dbumama.weixin.api.CompMenuApi;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * @date 2018年7月16日
 */
@RequestMapping(value="wechat/menu", viewPath = "menu")
public class WechatMenuController extends BaseAppAdminController{

	@RPCInject
	private AuthUserService authUserService;
	
	@RPCInject
	private MenuReplyConfigService menuReplyConfigService;
	@RPCInject
	private MenuEventService menuEventService;
	
	public void index(){
		setAttr("menuEvents", menuEventService.findAll());
		render("wechat_m_index.html");
	}
	
	public void media(String mediaid){
		//获取公众号已设置的菜单列表
		if(getAuthUser() == null){
			rendFailedJson("没有绑定公众账号");
			return;
		}
		
		ApiResult apiResult = CompMediaApi.getMaterial(authUserService.getAccessToken(getAuthUser()), mediaid);

		if(apiResult.isAccessTokenInvalid()){
			rendFailedJson("请重新绑定公众号");
			return;
		}
		if(!apiResult.isSucceed()){
			rendFailedJson("调用获取素材接口失败");
			return;
		}
		
		rendSuccessJson(apiResult);
	}
	
	public void list(){
		//获取公众号已设置的菜单列表
		if(getAuthUser() == null){
			rendFailedJson("没有绑定公众账号");
			return;
		}
		
		String authAppId = getAuthUser().getAppId();
		ApiResult apiResult = CompMenuApi.getMenu(authUserService.getAccessToken(getAuthUser()), authAppId);

		if(apiResult.isAccessTokenInvalid()){
			rendFailedJson("请重新绑定公众号");
			return;
		}
		if(!apiResult.isSucceed() && apiResult.getErrorCode() != 46003){//46003表示公众号没有创建菜单
			rendFailedJson("调用获取菜单接口失败");
			return;
		}
		
		rendSuccessJson(apiResult);
	}
	
	@Before(POST.class)
	public void save(String menus, String menuConfigMaps){
		String authAppId = getAppId();
		
		if(StrKit.isBlank(authAppId)){
			rendFailedJson("请选择公众账号");
			return;
		}

		if(StrKit.isBlank(menus)){
			rendFailedJson("菜单数据不能为空");
			return;
		}
		
		try{				
			menuReplyConfigService.save(getAuthUserId(), menuConfigMaps, authUserService.getAccessToken(getAuthUser()), authAppId, menus);
			rendSuccessJson();
		}catch(WxmallBaseException e){
			rendFailedJson(e.getMessage());
		}
		
	}
	
	public void delete(){
		String authAppId = getAppId();
		
		if(StrKit.isBlank(authAppId)){
			rendFailedJson("请选择公众账号");
			return;
		}
		ApiResult apiResult = CompMenuApi.deleteMenu(authUserService.getAccessToken(getAuthUser()), authAppId);

		if(apiResult.isAccessTokenInvalid()){
			rendFailedJson("请重新绑定公众号");
			return;
		}
		if(!apiResult.isSucceed()){
			rendFailedJson("调用删除菜单接口失败");
			return;
		}
		rendSuccessJson();
	}
	
	public void menulist(){
		String key = getPara("key");
		List<MenuReplyConfigResDto> configResDtos = menuReplyConfigService.findAllMenuReplyConfig(getAuthUserId(), key);
		renderJson(Ret.ok().set("data", configResDtos));
	}
	
}
