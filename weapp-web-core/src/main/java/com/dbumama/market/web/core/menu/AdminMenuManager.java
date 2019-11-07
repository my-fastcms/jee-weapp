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
package com.dbumama.market.web.core.menu;

import com.dbumama.market.service.api.MenuItem;
import com.dbumama.market.service.api.MenuService;
import io.jboot.Jboot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangjun
 *
 * 2019年5月8日
 */
@SuppressWarnings("serial")
public class AdminMenuManager {

	MenuService menuService = Jboot.service(MenuService.class);

	private List<MenuItem> fwhMenus = new ArrayList<MenuItem>();
	private List<MenuItem> dyhMenus = new ArrayList<MenuItem>();
	private List<MenuItem> weappMenus = new ArrayList<MenuItem>();

	private AdminMenuManager() {}
	
	public static final AdminMenuManager me = new AdminMenuManager();
	
	public List<MenuItem> getFwhMenus() {
		return fwhMenus;
	}

	public void setFwhMenus(List<MenuItem> fwhMenus) {
		this.fwhMenus = fwhMenus;
	}

	public List<MenuItem> getDyhMenus() {
		return dyhMenus;
	}

	public void setDyhMenus(List<MenuItem> dyhMenus) {
		this.dyhMenus = dyhMenus;
	}

	public List<MenuItem> getWeappMenus() {
		return weappMenus;
	}

	public void setWeappMenus(List<MenuItem> weappMenus) {
		this.weappMenus = weappMenus;
	}

	public void init() {
		fwhMenus.addAll(menuService.getFwMenus());
		dyhMenus.addAll(menuService.getDyMenus());
		weappMenus.addAll(menuService.getXcxMenus());
	}

}
