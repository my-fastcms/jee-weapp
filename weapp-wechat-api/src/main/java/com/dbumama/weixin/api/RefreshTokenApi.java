package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import io.jboot.Jboot;

import java.util.Map;

/**
 * 刷新token
 * @author wangjun
 *
 */
public class RefreshTokenApi {

	static final String url = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=";
	
	/**
	 * {
		"authorizer_access_token": "aaUl5s6kAByLwgV0BhXNuIFFUqfrR8vTATsoSHukcIGqJgrc4KmMJ-JlKoC_-NKCLBvuU1cWPv4vDcLN8Z0pn5I45mpATruU0b51hzeT1f8", 
		"expires_in": 7200, 
		"authorizer_refresh_token": "BstnRqgTJBXb9N2aJq6L5hzfJwP406tpfahQeLNxX0w"
	 }
	 * @param authAppId
	 * @param refreshToken
	 * @return
	 */
	public static ApiResult getRefreshToken(String compAccessToken, String authAppId, String refreshToken){
		final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId())
				.put("authorizer_appid", authAppId)
				.put("authorizer_refresh_token", refreshToken)
				.getData();
		try {
			String jsonResult = HttpUtils.post(url + compAccessToken, JSON.toJSONString(queryParas));
			return new ApiResult(jsonResult);
		} catch (Exception e) {
			return null;
		}
		
	}
	
}
