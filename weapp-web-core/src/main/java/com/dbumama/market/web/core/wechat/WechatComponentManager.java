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
package com.dbumama.market.web.core.wechat;

import java.util.ArrayList;
import java.util.List;

import io.jboot.utils.ClassScanner;

/**
 * @author wangjun
 * 2019年6月8日
 */
public class WechatComponentManager {
	
	private WechatComponentManager(){}

	public static WechatComponentManager me = new WechatComponentManager();
	
	public static WechatComponentManager me(){
		return me;
	}
	
	private List<Class<WechatComponent>> wechatComponents = new ArrayList<Class<WechatComponent>>();

	public void init(){
		wechatComponents.addAll(ClassScanner.scanSubClass(WechatComponent.class, true));
	}
	
	/**
	 * @return the wechatComponents
	 */
	public List<Class<WechatComponent>> getWechatComponents() {
		return wechatComponents;
	}

	/**
	 * @param wechatComponents the wechatComponents to set
	 */
	public void setWechatComponents(List<Class<WechatComponent>> wechatComponents) {
		this.wechatComponents = wechatComponents;
	}
	
}
