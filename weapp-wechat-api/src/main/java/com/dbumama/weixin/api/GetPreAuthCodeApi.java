package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import io.jboot.Jboot;

import java.util.Map;

public class GetPreAuthCodeApi {

	static final String url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=";
	
	public static ApiResult getPreAuthCode(String compAccessToken){
		final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId()).getData();
		String jsonResult = HttpUtils.post(url + compAccessToken, JSON.toJSONString(queryParas));
		return new ApiResult(jsonResult);
	}
	
}
