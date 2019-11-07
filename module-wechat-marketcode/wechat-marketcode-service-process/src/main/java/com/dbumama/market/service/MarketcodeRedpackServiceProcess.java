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
 * 一物一码红包处理逻辑
 * @author wangjun
 * 2019年8月13日
 */
public class MarketcodeRedpackServiceProcess implements MarketcodeService{

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeService#doProcess(java.lang.String, java.lang.String, com.dbumama.market.model.WeappTemplate)
	 */
	@Override
	public void doProcess(String wxaAppId, Integer applicationId, String isvApplicationId, String openid, String code)
			throws MarketcodeException {
		//读取红包配置
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeService#render(java.lang.Long)
	 */
	@Override
	public String render(MarketcodeCodeactive marketcodeCodeactive) throws MarketcodeException {
		return null;
	}

}
