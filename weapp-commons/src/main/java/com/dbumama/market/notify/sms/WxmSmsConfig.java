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
package com.dbumama.market.notify.sms;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author wangjun
 *
 */
@ConfigModel(prefix = "wxmall.sms")
public class WxmSmsConfig {

	private String smsAppProvider;
	private String smsAppKey;
	private String smsAppSecret;
	private String smsTemplateCode;
	private String smsSignName;
	private String smsProduct;
	
	public String getSmsAppProvider() {
		return smsAppProvider;
	}
	public void setSmsAppProvider(String smsAppProvider) {
		this.smsAppProvider = smsAppProvider;
	}
	public String getSmsAppKey() {
		return smsAppKey;
	}
	public void setSmsAppKey(String smsAppKey) {
		this.smsAppKey = smsAppKey;
	}
	public String getSmsAppSecret() {
		return smsAppSecret;
	}
	public void setSmsAppSecret(String smsAppSecret) {
		this.smsAppSecret = smsAppSecret;
	}
	public String getSmsTemplateCode() {
		return smsTemplateCode;
	}
	public void setSmsTemplateCode(String smsTemplateCode) {
		this.smsTemplateCode = smsTemplateCode;
	}
	public String getSmsSignName() {
		return smsSignName;
	}
	public void setSmsSignName(String smsSignName) {
		this.smsSignName = smsSignName;
	}
	public String getSmsProduct() {
		return smsProduct;
	}
	public void setSmsProduct(String smsProduct) {
		this.smsProduct = smsProduct;
	}
	
}
