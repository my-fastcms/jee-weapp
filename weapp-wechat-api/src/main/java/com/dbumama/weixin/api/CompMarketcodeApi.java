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
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信一物一码接口 
 * @author wangjun
 * 2019年7月18日
 */
public class CompMarketcodeApi {

	static final String applyCodeUrl = "https://api.weixin.qq.com/intp/marketcode/applycode?access_token=";
	
	/**
	 * 返回
	 * {
		"errcode":0,
		"errmsg":"ok",
		"application_id":581865877
		}
	 * @param accessToken
	 * @param code_count
	 * @param isv_application_id
	 * @return
	 */
	public static ApiResult applyCode(String accessToken, Long code_count, String isv_application_id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code_count", code_count);
		params.put("isv_application_id", isv_application_id);
		return new ApiResult(HttpKit.post(applyCodeUrl + accessToken, JsonKit.toJson(params)));
	}
	
	static final String applyCodeQueryUrl = "https://api.weixin.qq.com/intp/marketcode/applycodequery?access_token=";
	
	/**
	  返回
	  {
		"errcode": 0,
		"errmsg": "ok",
		"status": "FINISH", //状态为finish 可下载
		"code_generate_list": [{
		   "code_start": 0,
		   "code_end": 49999
		  },{
		   "code_start": 50000,
		   "code_end": 99999
		   }]
		}
	 * @param accessToken
	 * @param application_id
	 * @param isv_application_id
	 * @return
	 */
	public static ApiResult applyCodeQuery(String accessToken, String application_id, String isv_application_id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", application_id);
		params.put("isv_application_id", isv_application_id);
		return new ApiResult(HttpKit.post(applyCodeQueryUrl + accessToken, JsonKit.toJson(params)));
	}
	
	static final String applyCodeDownload = "https://api.weixin.qq.com/intp/marketcode/applycodedownload?access_token=";
	
	/**
	 * 返回
	 * {
		"errcode":0,
		"errmsg":"ok",
		"buffer": "ajfiwejfoiawjfijweofi"
		}
	 * @param accessToken
	 * @param application_id
	 * @param code_start
	 * @param code_end
	 * @return
	 */
	public static ApiResult applyCodeDownload(String accessToken, String application_id, Long code_start, Long code_end){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", application_id);
		params.put("code_start", code_start);
		params.put("code_end", code_end);
		return new ApiResult(HttpKit.post(applyCodeDownload + accessToken, JsonKit.toJson(params)));
	}
	
	static final String codeActiveUrl = "https://api.weixin.qq.com/intp/marketcode/codeactive?access_token=";
	
	/**
	 * 返回
	 * {"errcode":0,"errmsg":"ok"}
	 * @param accessToken
	 * @param application_id
	 * @param activity_name
	 * @param product_brand
	 * @param product_title
	 * @param product_code
	 * @param wxa_appid
	 * @param wxa_path
	 * @param wxa_type
	 * @param code_start
	 * @param code_end
	 * @return
	 */
	public static ApiResult codeActive(String accessToken, String application_id, 
			String activity_name, String product_brand, String product_title, String product_code,
			String wxa_appid, String wxa_path, Integer wxa_type, 
			Long code_start, Long code_end){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", application_id);
		params.put("activity_name", activity_name);
		params.put("product_brand", product_brand);
		params.put("product_title", product_title);
		params.put("product_code", product_code);
		params.put("wxa_appid", wxa_appid);
		params.put("wxa_path", wxa_path);
		params.put("wxa_type", wxa_type == null ? 0 : wxa_type);
		params.put("code_start", code_start);
		params.put("code_end", code_end);
		return new ApiResult(HttpKit.post(codeActiveUrl + accessToken, JsonKit.toJson(params)));
	}
	
	static final String codeActiveQueryUrl = "https://api.weixin.qq.com/intp/marketcode/codeactivequery?access_token=";
	
	public static ApiResult codeActiveQuery(String accessToken, Long application_id, String code, Long code_index){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_id", application_id);
		if(StrKit.notBlank(code)){
			params.put("code", code);
		}
		if(code_index !=null){
			params.put("code_index", code_index);
		}
		return new ApiResult(HttpKit.post(codeActiveQueryUrl + accessToken, JsonKit.toJson(params)));
	}
	
	public static ApiResult codeActiveQuery(String accessToken, Long application_id, String code){
		return codeActiveQuery(accessToken, application_id, code, null);
	}
	
	public static ApiResult codeActiveQuery(String accessToken, Long application_id, Long code_index){
		return codeActiveQuery(accessToken, application_id, null, code_index);
	}
	
	final static String ticketToCodeUrl = "https://api.weixin.qq.com/intp/marketcode/tickettocode?access_token=";
	
	/**
	 * 
	 * {
		"errcode": 0,
		"errmsg": "ok",
		"code": "8",
		"code_start": 0,
		"code_end": 200,
		"activity_name": "test_name",
		"product_brand": "test_brand",
		"product_title": "test_title",
		"product_code": "test_code",
		"wxa_appid":"wx3sxjifjwojfsffef",
		"wxa_path":"pages/index/index"
		}
	 * 
	 * @param accessToken
	 * @param openid
	 * @param code_ticket
	 * @return
	 */
	public static ApiResult ticketToCode(String accessToken, String openid, String code_ticket){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("openid", openid);
		params.put("code_ticket", code_ticket);
		return new ApiResult(HttpKit.post(ticketToCodeUrl + accessToken, JsonKit.toJson(params)));
	}
	
}
