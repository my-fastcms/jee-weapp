package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import io.jboot.Jboot;

import java.util.Map;

/**
 * 获取授权公众号信息
 * @author wangjun
 *
 */
public class GetCompAuthInfoApi {

	static final String url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=";
	
	/**
	 * {
"authorizer_info": {
"nick_name": "微信SDK Demo Special", 
"head_img": "http://wx.qlogo.cn/mmopen/GPyw0pGicibl5Eda4GmSSbTguhjg9LZjumHmVjybjiaQXnE9XrXEts6ny9Uv4Fk6hOScWRDibq1fI0WOkSaAjaecNTict3n6EjJaC/0", 
"service_type_info": { "id": 2 }, 
"verify_type_info": { "id": 0 },
"user_name":"gh_eb5e3a772040",
"business_info": {"open_store": 0, "open_scan": 0, "open_pay": 0, "open_card": 0, "open_shake": 0},
"alias":"paytest01"
}, 
"qrcode_url":"URL",    
"authorization_info": {
"appid": "wxf8b4f85f3a794e77", 
"func_info": [
{ "funcscope_category": { "id": 1 } }, 
{ "funcscope_category": { "id": 2 } }, 
{ "funcscope_category": { "id": 3 } }
]
}
}
	 * @param authAppId
	 * @return
	 */
	public static ApiResult getAuthInfo(String compAccessToken, String authAppId){
		final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId())
				.put("authorizer_appid", authAppId)
				.getData();
		String jsonResult = HttpUtils.post(url + compAccessToken, JSON.toJSONString(queryParas));
		return new ApiResult(jsonResult);
	}
	
}
