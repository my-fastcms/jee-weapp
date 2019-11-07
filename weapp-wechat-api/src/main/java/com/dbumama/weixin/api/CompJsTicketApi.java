/**
 * Copyright (c) 2011-2015, Unas 小强哥 (unas@qq.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import com.dbumama.weixin.utils.HttpUtils;
import com.dbumama.weixin.utils.RetryUtils;
import com.jfinal.kit.StrKit;
import io.jboot.Jboot;

import java.util.concurrent.Callable;

/**
 * 
 * 生成签名之前必须先了解一下jsapi_ticket，jsapi_ticket是公众号用于调用微信JS接口的临时票据
 * https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi
 * 
 * 微信卡券接口签名凭证api_ticket
 * https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=wx_card
 */
public class CompJsTicketApi {

	private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

	/**
	 * JSApi的类型
	 * 
	 * jsapi: 用于分享等js-api
	 * 
	 * wx_card：用于卡券接口签名凭证api_ticket
	 * 
	 */
	public enum JsApiType {
		jsapi, wx_card
	}

	/**
	 * 
	 * http GET请求获得jsapi_ticket（有效期7200秒，开发者必须在自己的服务全局缓存jsapi_ticket）
	 * 
	 * @param jsApiType
	 * @return JsTicket
	 */
	public static CompJsTicket getTicket(JsApiType jsApiType, String appId, String accessToken) {
		String key = appId + ':' + jsApiType.name();
		final ParaMap pm = ParaMap.create("access_token", accessToken).put("type", jsApiType.name());
		
		String jsTicketJson = Jboot.getCache().get("JSPAPI_TICKET_CACHE", key);
        CompJsTicket jsTicket = null;
        if (StrKit.notBlank(jsTicketJson)) {
            jsTicket = new CompJsTicket(jsTicketJson);
        }
		
		if (null == jsTicket || !jsTicket.isAvailable()) {
			// 最多三次请求
			jsTicket = RetryUtils.retryOnException(3, new Callable<CompJsTicket>() {
				
				@Override
				public CompJsTicket call() throws Exception {
					return new CompJsTicket(HttpUtils.get(apiUrl, pm.getData()));
				}
				
			});
			if(jsTicket != null)
				Jboot.getCache().put("JSPAPI_TICKET_CACHE", key, jsTicket.getJson());
		}
		return jsTicket;
	}

}