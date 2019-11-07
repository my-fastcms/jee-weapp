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
package com.dbumama.market.web.controller.wechat;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.web.core.wechat.WechatComponent;
import com.dbumama.market.web.core.wechat.WechatComponentManager;
import com.jfinal.aop.Clear;
import com.jfinal.core.NotAction;

import io.jboot.utils.ClassUtil;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2019年6月8日
 */
@RequestMapping(value="wx/message")
public class WechatMsgController extends WechatCommonMsgController{

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#index()
	 */
	@Override
	@Clear
	public void index() {
		
		List<WechatComponent> wechatComps = getMatchWechatComp();
		for(WechatComponent wechatComp : wechatComps){
			wechatComp.process(getInMsg(), this);
		}
		
		super.index();
	}

	@NotAction
	List<WechatComponent> getMatchWechatComp(){
		List<Class<WechatComponent>> wechatComps = WechatComponentManager.me.getWechatComponents();
		List<WechatComponent> matchComps = new ArrayList<WechatComponent>();
		for(Class<WechatComponent> wechatCompCls : wechatComps){
			WechatComponent wechatComp = ClassUtil.newInstance(wechatCompCls);
			if(wechatComp.check(getInMsg(), this)){
				matchComps.add(wechatComp);
			}
		}
		return matchComps;
	}
	
}
