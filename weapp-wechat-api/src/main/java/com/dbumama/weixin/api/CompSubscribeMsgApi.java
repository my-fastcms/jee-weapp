/**
 * Copyright (c) 2011-2017, Javen Zhou (javendev@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.dbumama.weixin.utils.HttpUtils;
import com.jfinal.kit.JsonKit;

/**
 * 
 * @author Javen
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1500374289_66bvB
 */
public class CompSubscribeMsgApi {
	private static String subscribe = "https://api.weixin.qq.com/cgi-bin/message/template/subscribe?access_token=";
    
	/**
	 * 发送一次性订阅消息
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return ApiResult 发送json数据示例: 
{
	"touser" : "OPENID",
	"template_id" :
	"TEMPLATE_ID",
	"url" : "URL",
	"scene" : "SCENE",
	"title" :
	"TITLE",
	"data" : {
		"content" : {
			"value" : "VALUE",
			"color" :
			"COLOR"
		}
	}
}
	 */
	public static ApiResult subscribe(String jsonStr, String accessToken) {
		String jsonResult = HttpUtils.post(subscribe + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}

	public static ApiResult subscribe(SubscribeInfo subscribeInfo) {
		return new ApiResult(JsonKit.toJson(subscribeInfo));
	}
	
	public static ApiResult subscribe(String accessToken, String openId, String templateId, String url, String miniAppid, String miniPagepath, int scene, String title,
			String value, String color) {
		SubscribeInfo subscribeInfo = SubscribeInfo.Builder().setTouser(openId).setTemplate_id(templateId).setUrl(url)
				.setScene(String.valueOf(scene)).setTitle(title)
				.setMiniprogram(Miniprogram.Builder().setAppid(miniAppid).setPagepath(miniPagepath))
				.setData(Data.Builder().setContent(Content.Builder().setValue(value).setColor(color).build()).build());
		return subscribe(JsonUtils.toJson(subscribeInfo), accessToken);
	}

}
