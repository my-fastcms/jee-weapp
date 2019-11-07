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
package com.dbumama.market.notify.email;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * @author wangjun
 *
 */
@ConfigModel(prefix = "wxmall.email")
public class WxmEmailConfig {

	private String emailHost;
	private String emailUsername;
	private String emailPassword;
	private Boolean emailUsessl;
	
	public Boolean getEmailUsessl() {
		return emailUsessl;
	}
	public void setEmailUsessl(Boolean emailUsessl) {
		this.emailUsessl = emailUsessl;
	}
	public String getEmailHost() {
		return emailHost;
	}
	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}
	public String getEmailUsername() {
		return emailUsername;
	}
	public void setEmailUsername(String emailUsername) {
		this.emailUsername = emailUsername;
	}
	public String getEmailPassword() {
		return emailPassword;
	}
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}
	
}
