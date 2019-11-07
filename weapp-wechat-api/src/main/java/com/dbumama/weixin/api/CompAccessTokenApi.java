/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.weixin.utils.HttpUtils;
import com.dbumama.weixin.utils.RetryUtils;
import io.jboot.Jboot;

import java.util.Map;

/**
 * 微信开放平台
 * 
 * 注意：该接口获取第三方平台自己的token；
 * 不是用来调用授权公众号接口的token
 * 
 * 授权公众号授权确认后，
 * 根据该token调用QueryCompUserAuthApi接口后，会返回被授权公众号的接口调用token
 * 
 */
public class CompAccessTokenApi {
	
	private static String url = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";

	/**
	 * 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
	 */
	public static CompAccessToken getAccessToken(String compTicket) {
        ApiConfig ac = Jboot.config(ApiConfig.class);
        return refreshAccessToken(ac, compTicket);
	}
	
	/**
	 * 直接获取 accessToken 字符串，方便使用
	 * @return String accessToken
	 */
	public static String getAccessTokenStr(String compTicket) {
		return getAccessToken(compTicket)==null ? "" : getAccessToken(compTicket).getAccessToken();
	}
	
	/**
	 * 强制更新 access token 值
	 */
	private static CompAccessToken refreshAccessToken(ApiConfig ac, String compTicket) {
        final Map<String, String> queryParas = ParaMap.create("component_appid", ac.getAppId())
				.put("component_appsecret", ac.getAppSecret()).put("component_verify_ticket", compTicket).getData();
        // 最多三次请求
        return RetryUtils.retryOnException(3, ()->{
            String json = HttpUtils.post(url, JSON.toJSONString(queryParas));
            return new CompAccessToken(json);
        });
    }

}
