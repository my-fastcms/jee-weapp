package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.jfinal.kit.HttpKit;

/**
 * 绑定小程序体验者
 * @author wangjun
 *
 */
public class CompWxaTesterApi {

	static final String bind_url = "https://api.weixin.qq.com/wxa/bind_tester?access_token=";
	static final String unbind_url = "https://api.weixin.qq.com/wxa/unbind_tester?access_token=";
	
	public static ApiResult bind(String accessToken, String testId){
		ParaMap map = ParaMap.create().put("wechatid", testId);
		String jsonResult = HttpKit.post(bind_url + accessToken, JsonUtils.toJson(map.getData()));
		return new ApiResult(jsonResult);
	}
	
	public static ApiResult unbind(String accessToken, String testId){
		ParaMap map = ParaMap.create().put("wechatid", testId);
		String jsonResult = HttpKit.post(unbind_url + accessToken, JsonUtils.toJson(map.getData()));
		return new ApiResult(jsonResult);
	}
	
}
