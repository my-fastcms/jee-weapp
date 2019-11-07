package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import io.jboot.Jboot;

import java.util.Map;

public class QueryCompUserAuthApi {

	static final String url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=";
	
	/**
	 * { 
"authorization_info": {
"authorizer_appid": "wxf8b4f85f3a794e77", 
"authorizer_access_token": "QXjUqNqfYVH0yBE1iI_7vuN_9gQbpjfK7hYwJ3P7xOa88a89-Aga5x1NMYJyB8G2yKt1KCl0nPC3W9GJzw0Zzq_dBxc8pxIGUNi_bFes0qM", 
"expires_in": 7200, 
"authorizer_refresh_token": "dTo-YCXPL4llX-u1W1pPpnp8Hgm4wpJtlR6iV0doKdY", 
"func_info": [
{
"funcscope_category": {
"id": 1
}
}, 
{
"funcscope_category": {
"id": 2
}
}, 
{
"funcscope_category": {
"id": 3
}
}
] 
}
	 * @param authCode
	 * @return
	 */
	public static ApiResult queryAuth(String compAccessToken, String authCode){
		final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId())
				.put("authorization_code", authCode)
				.getData();
		String jsonResult = HttpUtils.post(url + compAccessToken, JSON.toJSONString(queryParas));
		return new ApiResult(jsonResult);
	}
	
}
