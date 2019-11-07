package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import io.jboot.Jboot;

import java.util.Map;

/**
 * 第三方平台对其所有API调用次数清零（只与第三方平台相关，与公众号无关，接口如api_component_token）
 * @author wangjun
 *
 */
public class ClearQuotaApi {

	static String url = "https://api.weixin.qq.com/cgi-bin/component/clear_quota?component_access_token=";
	
	public static ApiResult clearQuota(String compAceessToken){
		final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId()).getData();
		String jsonResult = HttpUtils.post(url + compAceessToken, JSON.toJSONString(queryParas));
		return new ApiResult(jsonResult);
	}
	
}
