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

import java.util.LinkedHashMap;
import java.util.List;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.AuthUserStyleService;
import com.dbumama.market.service.api.WeappStyleService;
import com.dbumama.market.service.api.WeappTemplateService;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.dbumama.weixin.api.CompWxaCodeApi;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * 微信小程序
 * @author wangjun
 * 2017年9月25日
 */
@RequestMapping(value="weapp/sets", viewPath="weapp")
public class WeappSettingController extends BaseAuthUserController {
	
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private WeappTemplateService weappTemplateService;
	@RPCInject
	private WeappStyleService weappStyleService;
	@RPCInject
	private AuthUserStyleService authUserStyleService;
	
	public void index(){
		render("st_xcx_index.html");
	}
	
	/**
	 * 选择模板
	 */
	public void step1(Long id){
		setAttr("selTpl", weappTemplateService.findById(id));
		setAttr("styles", weappStyleService.findAll());
		setAttr("currStyle", authUserStyleService.getAuthUserStyle(getAuthUserId()));
		setAttr("templates", weappTemplateService.findList());
		setAttr("authUser", getAuthUser());
		render("step1.html");	
	}
	
	/**
	 * 选择模板后即上传代码
	 */
	public void commitCode(){
		try {
			authUserService.uploadWeappCode(getAuthUserId(), getParaToInt("templateId"), getParaToInt("styleId"));
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 设置体验者
	 */
	public void step2(){
		render("step2.html");
	}
	
	public void tobindtest(){
		render("bindtest.html");
	}
	
	public void bindtest(){
		try {
			authUserService.bindtest(getAuthUserId(), getPara("wxuser"));
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void unbindtest(){
		try {
			authUserService.unbindtest(getAuthUserId(), getPara("wxuser"));
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void listTester(){
		rendSuccessJson(authUserService.listTester(getAuthUser().getAppId()));
	}
	
	/**
	 * 体验新版本
	 */
	public void step3(){
		String qrcode = authUserService.getWeappTestQrcode(getAuthUserId());
		setAttr("qrcode", qrcode);
		render("step3.html");
	}
	
	/**
	 * 提交到微信审核
	 */
	public void step4(){
		//获取授权小程序可选类目
		ApiResult categoryRes = CompWxaCodeApi.getCategory(authUserService.getAccessToken(getAuthUser()));
		log.debug("=====categoryRes Json:" + categoryRes.getJson());
		@SuppressWarnings("unchecked")
		List<LinkedHashMap<String, Object>> categories = categoryRes.getList("category_list");
		setAttr("categories", categories);
		//获取小程序页面配置
		ApiResult getPageRes = CompWxaCodeApi.getPage(authUserService.getAccessToken(getAuthUser()));
		log.debug("=====getPageRes Json:" + getPageRes.getJson());
		@SuppressWarnings("unchecked")
		List<String> pageList = getPageRes.getList("page_list");
		setAttr("pageList", pageList);
		render("step4.html");
	}
	
	public void commitAudit(){
		try {
			authUserService.commitAudit(getAuthUserId(), getPara("item_list"));
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void listAudits(){
		try {
			rendSuccessJson(authUserService.page(getAuthUserId(), getPageNo(), getPageSize()));
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 发布
	 */
	public void publish(){
		try {
			authUserService.release(getAuthUserId());
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void color(){
		render("wxapp_color_index.html");
	}
	
}
