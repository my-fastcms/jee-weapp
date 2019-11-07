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

import com.dbumama.market.model.App;
import com.dbumama.market.service.api.AppService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BasePlatController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="app")
public class AppController extends BasePlatController{
	
	@RPCInject
	private AppService appService;
	
	public void index(){
		render("app_index.html");
	}
	
	public void getApps(){
		rendSuccessJson(appService.findApps());
	}
	
	public void add(){
		final Long id = getParaToLong("id");
		App app = appService.findById(id);
		
		if(app == null){
			render("app_add.html");
			return;
		}
		
		String appImage = app.getAppShowImages();
		if(StrKit.notBlank(appImage)){
			String[] images = appImage.split(",");
			setAttr("images", images);
		}
		setAttr("app", app);
		render("app_add.html");
	}
	
	@Before(POST.class)
	public void save(){
		try{
			appService.save(getParaToLong("id"), getParaToInt("app_category"), getParaToInt("app_type"),getPara("app_image"),
								getPara("app_name"), getPara("imgList"),getPara("app_desc"),getPara("app_content"));
			renderSuccess();
		}catch(WxmallBaseException e){
			rendFailedJson(e.getMessage());
		}
	}
}
