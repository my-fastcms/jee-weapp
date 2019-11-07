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

import com.dbumama.market.base.ApiResult;
import com.jfinal.kit.HttpKit;

import java.util.Map;

/**
 * @author wangjun
 * 2017年11月7日
 */
public class CompWeappstatApi {

	//用户访问小程序的详细数据可从访问分析中获取，概况中提供累计用户数等部分指标数据。
	static final String getweanalysisappiddailysummarytrend_url = "https://api.weixin.qq.com/datacube/getweanalysisappiddailysummarytrend?access_token=";
	
	public static final ApiResult getWeanalysisappiddailysummarytrend(String accessToken, String begin_date, String end_date){
		Map<String, String> queryParas = ParaMap.create().put("begin_date", begin_date).put("end_date", end_date).getData();
		String jsonResult = HttpKit.get(getweanalysisappiddailysummarytrend_url + accessToken, queryParas);
		return new ApiResult(jsonResult);
	}
	
}
