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

import com.dbumama.market.service.api.PlatActivitysJoinService;
import com.dbumama.market.service.api.PlatActivitysService;
import com.dbumama.market.service.api.WxmallMsgBaseException;
import com.dbumama.market.web.core.controller.BaseAuthUserController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="activity")
public class ActivityController extends BaseAuthUserController{
	
	@RPCInject
	private PlatActivitysService platActivitysService; 
	@RPCInject
	private PlatActivitysJoinService platActivitysJoinService; 
	
	public void index(){
		setAttr("activity", platActivitysService.getOnline());
		setAttr("sellerUser", getSellerUser());
		render("activity_index.html");
	}
	
	public void addPhone(){
		try{
		platActivitysJoinService.addPhone(getSellerUser(), getPara("phone"), getPara("phoneCode"));
		renderSuccess();
		}catch(WxmallMsgBaseException e){
			renderFail(e.getMessage());
		}
	}
	
	public void join(){
		try{
			platActivitysJoinService.joinUser(getAuthUser(), getSellerUser(), getParaToLong("activityId"));
			renderSuccess();
		}catch(WxmallMsgBaseException e){
			renderFail(e.getMessage());
		}
	}
}
