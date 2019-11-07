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

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.Keywords;
import com.dbumama.market.service.api.KeywordsReplyConfigResDto;
import com.dbumama.market.service.api.KeywordsReplyConfigService;
import com.dbumama.market.service.api.KeywordsService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.kit.Ret;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * @date 2018年7月16日
 */
@RequestMapping(value = "keywords/reply", viewPath="keywords")
@RequiresPermissions(value="/keywords/reply")
public class KeywordsReplyController extends BaseAppAdminController{
	
	@RPCInject
	private KeywordsReplyConfigService keywordsReplyConfigService;
	
	@RPCInject
	private KeywordsService keywordsService;

	public void list(){
		Long keywordsId = getParaToLong("keywordsId");
		Keywords keywords = keywordsService.findById(keywordsId);
		List<KeywordsReplyConfigResDto> replyConfigs = keywordsReplyConfigService.findAllKeywordsReplyConfig(keywordsId);
		renderJson(Ret.ok().set("data", replyConfigs).set("keywords" , keywords));
	}
	
}
