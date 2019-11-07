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
package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuReplyConfig;
import com.dbumama.market.model.MenuReplyNews;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangjun
 * @date 2018年7月21日
 */
@SuppressWarnings("serial")
public class MenuReplyConfigResDto implements Serializable{

	private MenuReplyConfig menuReplyConfig;
	private List<MenuReplyNews> replyNews;
	
	//orther config 
	/**
	 * @return the menuReplyConfig
	 */
	public MenuReplyConfig getMenuReplyConfig() {
		return menuReplyConfig;
	}
	/**
	 * @param menuReplyConfig the menuReplyConfig to set
	 */
	public void setMenuReplyConfig(MenuReplyConfig menuReplyConfig) {
		this.menuReplyConfig = menuReplyConfig;
	}
	/**
	 * @return the replyNews
	 */
	public List<MenuReplyNews> getReplyNews() {
		return replyNews;
	}
	/**
	 * @param replyNews the replyNews to set
	 */
	public void setReplyNews(List<MenuReplyNews> replyNews) {
		this.replyNews = replyNews;
	}
	
}
