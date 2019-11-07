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
public class Content {
	private String value;
	private String color;

	public static Content Builder() {
		return new Content();
	}

	public Content build() {
		if (StrKit.isBlank(value)) {
			throw new IllegalStateException("value is null");
		}
		if (StrKit.isBlank(color)) {
			throw new IllegalStateException("color is null");
		}
		return new Content(value, color);
	}

	private Content() {
	}

	private Content(String value, String color) {
		this.value = value;
		this.color = color;
	}

	public String getValue() {
		return value;
	}

	public Content setValue(String value) {
		this.value = value;
		return this;
	}

	public String getColor() {
		return color;
	}

	public Content setColor(String color) {
		this.color = color;
		return this;
	}
}
