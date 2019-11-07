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

import java.util.List;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * @author wangjun
 * 2018年6月11日
 */
@SuppressWarnings("serial")
public class WeappStyleResultDto extends AbstractResultDto{

	private String navbarBgcolor;
	private String otherBgcolor;
	private String tabbarColor;
	private String tabbarSelectedColor;
	private String tabbarBgColor;
	private String showCompensate;
	private String showDeliveryTime;
	private String showSafeguard;
	
	private List<WeappStyleTabbarItemResultDto> tabbarItems;
	
	public String getShowCompensate() {
		return showCompensate;
	}

	public void setShowCompensate(String showCompensate) {
		this.showCompensate = showCompensate;
	}

	public String getShowDeliveryTime() {
		return showDeliveryTime;
	}

	public void setShowDeliveryTime(String showDeliveryTime) {
		this.showDeliveryTime = showDeliveryTime;
	}

	public String getShowSafeguard() {
		return showSafeguard;
	}

	public void setShowSafeguard(String showSafeguard) {
		this.showSafeguard = showSafeguard;
	}

	/**
	 * @return the tabbarItems
	 */
	public List<WeappStyleTabbarItemResultDto> getTabbarItems() {
		return tabbarItems;
	}

	/**
	 * @param tabbarItems the tabbarItems to set
	 */
	public void setTabbarItems(List<WeappStyleTabbarItemResultDto> tabbarItems) {
		this.tabbarItems = tabbarItems;
	}

	/**
	 * @return the navbarBgcolor
	 */
	public String getNavbarBgcolor() {
		return navbarBgcolor;
	}

	/**
	 * @param navbarBgcolor the navbarBgcolor to set
	 */
	public void setNavbarBgcolor(String navbarBgcolor) {
		this.navbarBgcolor = navbarBgcolor;
	}

	/**
	 * @return the otherBgcolor
	 */
	public String getOtherBgcolor() {
		return otherBgcolor;
	}

	/**
	 * @param otherBgcolor the otherBgcolor to set
	 */
	public void setOtherBgcolor(String otherBgcolor) {
		this.otherBgcolor = otherBgcolor;
	}

	/**
	 * @return the tabbarColor
	 */
	public String getTabbarColor() {
		return tabbarColor;
	}

	/**
	 * @param tabbarColor the tabbarColor to set
	 */
	public void setTabbarColor(String tabbarColor) {
		this.tabbarColor = tabbarColor;
	}

	/**
	 * @return the tabbarSelectedColor
	 */
	public String getTabbarSelectedColor() {
		return tabbarSelectedColor;
	}

	/**
	 * @param tabbarSelectedColor the tabbarSelectedColor to set
	 */
	public void setTabbarSelectedColor(String tabbarSelectedColor) {
		this.tabbarSelectedColor = tabbarSelectedColor;
	}

	/**
	 * @return the tabbarBgColor
	 */
	public String getTabbarBgColor() {
		return tabbarBgColor;
	}

	/**
	 * @param tabbarBgColor the tabbarBgColor to set
	 */
	public void setTabbarBgColor(String tabbarBgColor) {
		this.tabbarBgColor = tabbarBgColor;
	}

	
}
