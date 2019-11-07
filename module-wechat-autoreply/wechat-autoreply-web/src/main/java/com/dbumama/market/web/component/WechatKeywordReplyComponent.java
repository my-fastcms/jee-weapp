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
package com.dbumama.market.web.component;

import com.dbumama.market.service.api.KeywordsService;
import com.dbumama.market.web.core.wechat.MsgCompController;
import com.dbumama.market.web.core.wechat.WechatComponent;
import com.dbumama.market.web.core.wechat.msg.in.InMsg;
import com.dbumama.market.web.core.wechat.msg.in.InTextMsg;
import io.jboot.components.rpc.annotation.RPCInject;

/**
 * 关键词回复
 * @author wangjun
 * 2019年6月9日
 */
public class WechatKeywordReplyComponent implements WechatComponent{
	
	@RPCInject
	private KeywordsService keywordsService;

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.WechatComponent#check(com.jfinal.weixin.sdk.msg.in.InMsg, com.dbumama.market.web.core.wechat.MsgCompController)
	 */
	@Override
	public boolean check(InMsg inMsg, MsgCompController msgController) {
		return inMsg instanceof InTextMsg && msgController.getAuthUser() !=null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.WechatComponent#process(com.jfinal.weixin.sdk.msg.in.InMsg, com.dbumama.market.web.core.wechat.MsgCompController)
	 */
	@Override
	public void process(InMsg inMsg, MsgCompController msgController) {
		
		InTextMsg inTextMsg = (InTextMsg) inMsg;
		
		final String keywordsText = inTextMsg.getContent();
		final String openid = inTextMsg.getFromUserName();
		
		//关键字回复逻辑 begin =========================================================================================
		keywordsService.reply(msgController.getAuthUser(), keywordsText, openid);	
	}

}
