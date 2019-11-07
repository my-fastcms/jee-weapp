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

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.WxamsgTemplate;
import com.dbumama.market.service.api.UserException;
import com.dbumama.market.service.api.WxamsgTemplateService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2018年5月31日
 */
@RequestMapping(value = "wxamsgtpl")
@RequiresPermissions(value="/wxamsgtpl")
public class WxamsgTemplateController extends BaseAppAdminController {

	@RPCInject
	private WxamsgTemplateService wxamsgTemplateService;
	
	public void index(){
		setAttr("msgTemplates", wxamsgTemplateService.findByAppId(getAuthUserId()));
		render("index.html");
	}
	
	public void del(){
		WxamsgTemplate msgtpl = wxamsgTemplateService.findById(getParaToLong("id"));
		if(msgtpl != null && msgtpl.getActive() == true){
			msgtpl.setActive(false);
			wxamsgTemplateService.update(msgtpl);
		}
		rendSuccessJson();
	}
	
	public void synOnline(){
		try {
			wxamsgTemplateService.synOnline(getAuthUserId());
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		}
		rendSuccessJson();
	}
	
	public void undel(){
		WxamsgTemplate msgtpl = wxamsgTemplateService.findById(getParaToLong("id"));
		if(msgtpl != null && msgtpl.getActive() == false){
			msgtpl.setActive(true);
			wxamsgTemplateService.update(msgtpl);
		}
		rendSuccessJson();
	}
	
}
