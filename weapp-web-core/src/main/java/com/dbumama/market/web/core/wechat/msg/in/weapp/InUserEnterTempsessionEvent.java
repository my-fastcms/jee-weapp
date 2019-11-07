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
package com.dbumama.market.web.core.wechat.msg.in.weapp;

import com.dbumama.market.web.core.wechat.msg.in.event.EventInMsg;

/**
 * @author wangjun
 * 2019年6月23日
 */
@SuppressWarnings("serial")
public class InUserEnterTempsessionEvent extends EventInMsg {

	private String sessionFrom;
	
	/**
	 * @return the sessionFrom
	 */
	public String getSessionFrom() {
		return sessionFrom;
	}

	/**
	 * @param sessionFrom the sessionFrom to set
	 */
	public void setSessionFrom(String sessionFrom) {
		this.sessionFrom = sessionFrom;
	}

	/**
	 * @param toUserName
	 * @param fromUserName
	 * @param createTime
	 * @param event
	 */
	public InUserEnterTempsessionEvent(String toUserName, String fromUserName, Integer createTime, String event) {
		super(toUserName, fromUserName, createTime, event);
	}


}
