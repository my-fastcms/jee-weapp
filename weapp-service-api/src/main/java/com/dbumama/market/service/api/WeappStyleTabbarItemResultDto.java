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

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * @author wangjun
 * 2018年6月11日
 */
@SuppressWarnings("serial")
public class WeappStyleTabbarItemResultDto extends AbstractResultDto{
	private Integer tabbarIndex;
	private String tabbarTitle;
	private String tabbarIconPath;
	private String tabbarSelectedIconpath;
	/**
	 * @return the tabbarIndex
	 */
	public Integer getTabbarIndex() {
		return tabbarIndex;
	}
	/**
	 * @param tabbarIndex the tabbarIndex to set
	 */
	public void setTabbarIndex(Integer tabbarIndex) {
		this.tabbarIndex = tabbarIndex;
	}
	/**
	 * @return the tabbarTitle
	 */
	public String getTabbarTitle() {
		return tabbarTitle;
	}
	/**
	 * @param tabbarTitle the tabbarTitle to set
	 */
	public void setTabbarTitle(String tabbarTitle) {
		this.tabbarTitle = tabbarTitle;
	}
	/**
	 * @return the tabbarIconPath
	 */
	public String getTabbarIconPath() {
		return tabbarIconPath;
	}
	/**
	 * @param tabbarIconPath the tabbarIconPath to set
	 */
	public void setTabbarIconPath(String tabbarIconPath) {
		this.tabbarIconPath = tabbarIconPath;
	}
	/**
	 * @return the tabbarSelectedIconpath
	 */
	public String getTabbarSelectedIconpath() {
		return tabbarSelectedIconpath;
	}
	/**
	 * @param tabbarSelectedIconpath the tabbarSelectedIconpath to set
	 */
	public void setTabbarSelectedIconpath(String tabbarSelectedIconpath) {
		this.tabbarSelectedIconpath = tabbarSelectedIconpath;
	}
}
