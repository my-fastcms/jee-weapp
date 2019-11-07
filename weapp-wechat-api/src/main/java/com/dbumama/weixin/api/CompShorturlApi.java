/**
 * Copyright (c) 2011-2015, Unas 小强哥 (unas@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.dbumama.weixin.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 将一条长链接转成短链接 API
 * 文档地址：http://mp.weixin.qq.com/wiki/6/d2ec191ffdf5a596238385f75f95ecbe.html
 */
public class CompShorturlApi
{
    private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=";

    public static ApiResult getShorturl(String jsonStr, String accessToken) {
        String jsonResult = HttpUtils.post(apiUrl + accessToken, jsonStr);
        return new ApiResult(jsonResult);
    }

    /**
     * 长链接转短链接接口
     * @param longUrl 需要转换的长链接，支持http://、https://、weixin://wxpay 格式的url
     * @return ApiResult 短连接信息
     */
    public static ApiResult getShortUrl(String longUrl, String accessToken) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("action", "long2short");
        params.put("long_url", longUrl);
        return getShorturl(JsonUtils.toJson(params), accessToken);
    }
}
