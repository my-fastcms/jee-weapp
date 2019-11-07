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

/**
 * @author wangjun
 * 2018年9月29日
 */
@SuppressWarnings("serial")
public class WxmallMsgBaseException extends WxmallBaseException{

	public WxmallMsgBaseException(String openid, String message){
		super(message);
		this.openid = openid;
	}
	
	public WxmallMsgBaseException(String openid, Throwable throwable) {
		super(openid, throwable);
		this.openid = openid;
	}
	
	/**
	 * @param errorCode
	 * @param message
	 */
	public WxmallMsgBaseException(Integer errorCode, String message) {
		super(errorCode, message);
	}
	
	public WxmallMsgBaseException(String message){
		super(message);
	}

	private String openid;

	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}

	/**
	 * @param openid the openid to set
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
}
