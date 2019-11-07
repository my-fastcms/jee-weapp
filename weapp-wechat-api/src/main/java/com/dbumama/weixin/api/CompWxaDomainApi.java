package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.jfinal.kit.HttpKit;

/**
 * 微信小程序第三方服务器域名管理接口
 * @author wangjun
 *
 */
public class CompWxaDomainApi {
	static String url = "https://api.weixin.qq.com/wxa/modify_domain?access_token=";
	
	public static ApiResult domain(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(url + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
}
