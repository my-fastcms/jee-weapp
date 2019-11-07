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
public class SubscribeInfo {

	private String touser;
	private String template_id;
	private String url;
	private String scene;
	private String title;
	private Miniprogram miniprogram;
	private Data data;

	public static SubscribeInfo Builder() {
		return new SubscribeInfo();
	}

	public SubscribeInfo build() {
		if (StrKit.isBlank(touser)) {
			throw new IllegalStateException("touser is null");
		}
		if (StrKit.isBlank(template_id)) {
			throw new IllegalStateException("template_id is null");
		}
		if (StrKit.isBlank(url)) {
			throw new IllegalStateException("url is null");
		}
		if (StrKit.isBlank(scene)) {
			throw new IllegalStateException("scene is null");
		}
		if (StrKit.isBlank(title)) {
			throw new IllegalStateException("title is null");
		}
		if (!StrKit.notNull(data)) {
			throw new IllegalStateException("data is null");
		}
		return new SubscribeInfo(touser, template_id, url, scene, title, miniprogram, data);
	}

	private SubscribeInfo() {

	}

	private SubscribeInfo(String touser, String template_id, String url, String scene, String title, Miniprogram miniprogram, Data data) {
		this.touser = touser;
		this.template_id = template_id;
		this.url = url;
		this.scene = scene;
		this.title = title;
		this.miniprogram = miniprogram;
		this.data = data;
	}

	public String getTouser() {
		return touser;
	}

	public SubscribeInfo setTouser(String touser) {
		this.touser = touser;
		return this;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public SubscribeInfo setTemplate_id(String template_id) {
		this.template_id = template_id;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public SubscribeInfo setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getScene() {
		return scene;
	}

	public SubscribeInfo setScene(String scene) {
		this.scene = scene;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public SubscribeInfo setTitle(String title) {
		this.title = title;
		return this;
	}
	/**
	 * @return the miniprogram
	 */
	public Miniprogram getMiniprogram() {
		return miniprogram;
	}

	/**
	 * @param miniprogram the miniprogram to set
	 */
	public SubscribeInfo setMiniprogram(Miniprogram miniprogram) {
		this.miniprogram = miniprogram;
		return this;
	}

	public Data getData() {
		return data;
	}

	public SubscribeInfo setData(Data data) {
		this.data = data;
		return this;
	}
	
}
