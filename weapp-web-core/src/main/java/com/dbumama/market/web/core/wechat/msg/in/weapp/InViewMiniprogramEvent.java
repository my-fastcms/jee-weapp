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
 * 2019年6月21日
 */
@SuppressWarnings("serial")
public class InViewMiniprogramEvent extends EventInMsg {

	String menuId;
	String eventKey;
	
	/**
	 * @return the menuId
	 */
	public String getMenuId() {
		return menuId;
	}

	/**
	 * @param menuId the menuId to set
	 */
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	/**
	 * @return the eventKey
	 */
	public String getEventKey() {
		return eventKey;
	}

	/**
	 * @param eventKey the eventKey to set
	 */
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	/**
	 * @param toUserName
	 * @param fromUserName
	 * @param createTime
	 * @param event
	 */
	public InViewMiniprogramEvent(String toUserName, String fromUserName, Integer createTime, String event) {
		super(toUserName, fromUserName, createTime, event);
	}

}
