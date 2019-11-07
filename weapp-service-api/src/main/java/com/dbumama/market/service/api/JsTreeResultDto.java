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

import java.util.Map;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * @author wangjun
 * 2017年8月28日
 */
@SuppressWarnings("serial")
public class JsTreeResultDto extends AbstractResultDto {

	private Long id;
	private Long parent;
	private String text;
	private Object children;
	private Map<String, Object> state;	//json 格式 {"opened":true,"disabled":true}
	private String icon;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getParent() {
		return parent;
	}
	public void setParent(Long parent) {
		this.parent = parent;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Object getChildren() {
		return children;
	}
	public void setChildren(Object children) {
		this.children = children;
	}
	/**
	 * @return the state
	 */
	public Map<String, Object> getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(Map<String, Object> state) {
		this.state = state;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
}

