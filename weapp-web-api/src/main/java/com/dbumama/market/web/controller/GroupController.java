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

import com.dbumama.market.service.api.MultiGroupService;
import com.dbumama.market.service.api.ProductService;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.jfinal.aop.Before;

import com.jfinal.plugin.activerecord.Record;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

/**
 * @author wangjun
 * 2018年5月9日
 */
@RequestMapping(value = "group")
public class GroupController extends BaseApiController{
	
	@RPCInject
	private ProductService productService;
	@RPCInject
	private MultiGroupService multiGroupService;
	
	@Before(ApiSessionInterceptor.class)
	public void detail(){
		rendSuccessJson(multiGroupService.getGroupAndJoinUserInfos(getJSONParaToLong("groupId"), getBuyerId()));
	}

	public void list(){
		List<Record> productResultDtos = multiGroupService.getMultiGroupMini(getAuthUserId(),getParaToLong("id"), getPageNo(), getPageSize());
		rendSuccessJson(productResultDtos);
	}
	
}
