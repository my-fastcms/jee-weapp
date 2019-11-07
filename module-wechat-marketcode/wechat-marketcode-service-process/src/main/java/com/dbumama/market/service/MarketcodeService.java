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
package com.dbumama.market.service;

import com.dbumama.market.model.MarketcodeCodeactive;
import com.dbumama.market.service.api.MarketcodeException;

/**
 * @author wangjun
 * 2019年8月9日
 */
public interface MarketcodeService {

	/**
	 * 处理二维码扫码逻辑
	 * @param wxaAppId
	 * @param applicationId
	 * @param openid
	 * @param code
	 * @throws MarketcodeException
	 */
	void doProcess(String wxaAppId, Integer applicationId, String isvApplicationId, String openid, String code) throws MarketcodeException;
	
	/**
	 * 渲染到具体模板配置界面
	 * @param marketcodeCodeactiveId
	 * @return
	 */
	String render(MarketcodeCodeactive marketcodeCodeactive) throws MarketcodeException;
	
}
