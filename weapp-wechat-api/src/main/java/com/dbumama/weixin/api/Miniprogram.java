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
package com.dbumama.weixin.api;

import com.jfinal.kit.StrKit;

/**
 * @author wangjun
 * 2019年2月19日
 */
public class Miniprogram {
	public static Miniprogram Builder() {
		return new Miniprogram();
	}

	public Miniprogram build() {
		if (StrKit.isBlank(appid)) {
			throw new IllegalStateException("value is null");
		}
		if (StrKit.isBlank(pagepath)) {
			throw new IllegalStateException("color is null");
		}
		return new Miniprogram(appid, pagepath);
	}
	
	private Miniprogram(String appid, String pagepath) {
		this.appid = appid;
		this.pagepath = pagepath;
	}

	private Miniprogram() {
	}
	
	private String appid;
	private String pagepath;
	
	/**
	 * @return the appid
	 */
	public String getAppid() {
		return appid;
	}
	/**
	 * @param appid the appid to set
	 */
	public Miniprogram setAppid(String appid) {
		this.appid = appid;
		return this;
	}
	/**
	 * @return the pagepath
	 */
	public String getPagepath() {
		return pagepath;
	}
	/**
	 * @param pagepath the pagepath to set
	 */
	public Miniprogram setPagepath(String pagepath) {
		this.pagepath = pagepath;
		return this;
	}
}
