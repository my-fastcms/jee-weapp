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

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.Keywords;
import com.dbumama.market.service.api.KeywordsService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Ret;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

/**
 * @author wangjun
 * @date 2018年8月9日
 */
@RequestMapping(value = "keywords")
@RequiresPermissions(value={"/keywords", "/keywords/reply"}, logical = Logical.OR)
public class KeywordsController extends BaseAppAdminController{

	@RPCInject
	private KeywordsService keywordsService;
	
	
	public void reply(){
		render("keywords_reply_index.html");
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "keywordsConfig", message = "请设置菜单回复消息"),
        @Form(name = "keywordsText", message = "请设置关键字"),
	})
	public void save(String keywordsConfig, String keywordsText, Long keywordsId, Boolean enableKeywords, Integer autoTagid, Integer cancelTagid){
		try {
			Keywords keywords = keywordsService.save(getAuthUserId(), keywordsText, keywordsConfig, keywordsId, enableKeywords, autoTagid, cancelTagid);
			renderJson(Ret.ok().set("data", keywords));
			
		} catch (Exception e) {
			renderFail(e.getMessage());
		}
	}
	
	
	public void list(){
		List<Keywords> keywords = keywordsService.findByAppId(getAuthUserId());
		renderJson(Ret.ok().set("data", keywords));
	}
	
}
