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
import com.dbumama.weixin.utils.HttpUtils;

/**
 * @author wangjun
 * 2018年5月27日
 */
public class CompWxaTemplateApi {

	private static final String add_template_url = "https://api.weixin.qq.com/cgi-bin/wxopen/template/add?access_token=";
	
	private static String sendApiUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";
	
	private static String listApiUrl = "https://api.weixin.qq.com/cgi-bin/wxopen/template/list?access_token=";
	
	private static String delApiUrl = "https://api.weixin.qq.com/cgi-bin/wxopen/template/del?access_token=";
	
	private static String getApiUrl = "https://api.weixin.qq.com/cgi-bin/wxopen/template/library/get?access_token=";
	
	public static ApiResult addTemplate(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(add_template_url + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
    /**
     * 发送模板消息
     * @param jsonStr 模版json
     * @return {ApiResult}
     */
    public static ApiResult send(String accessToken, String jsonStr) {
        String jsonResult = HttpUtils.post(sendApiUrl + accessToken, jsonStr);
        return new ApiResult(jsonResult);
    }

    /**
     * 发送模板消息
     * @param template 模版对象
     * @return {ApiResult}
     */
    public static ApiResult send(String accessToken, WxaTemplate template) {
        return send(accessToken, template.build());
    }
    
    /**
     * 查询模板消息列表
     * @param jsonStr
     * @return {ApiResult}
     */
    public static ApiResult list(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(listApiUrl + accessToken, jsonStr);
		return new ApiResult(jsonResult);
    }
    
    /**
     * 删除模板消息
     * @param jsonStr 模版jsona
     * @return {ApiResult}
     */
    public static ApiResult delTemplate(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(delApiUrl + accessToken, jsonStr);
		return new ApiResult(jsonResult);
    }
    
    /**
     * 获取某个模板消息的关键词
     * @param jsonStr 模版jsona
     * @return {ApiResult}
     */
    public static ApiResult getTemplate(String accessToken, String jsonStr) {
    	String jsonResult = HttpKit.post(getApiUrl + accessToken, jsonStr);
    	return new ApiResult(jsonResult);
    }
    
}
