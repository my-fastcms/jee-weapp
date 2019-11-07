/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import java.util.HashMap;
import java.util.Map;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.dbumama.weixin.utils.HttpUtils;

/**
 * menu api
 */
public class CompMenuApi {
	
	private static String getMenu = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=";
	private static String createMenu = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";

	
	/**
	 * 查询菜单
	 */
	public static ApiResult getMenu(String accessToken, String authAppId) {
		String jsonResult = HttpUtils.get(getMenu + accessToken + "&appid="+authAppId);
		return new ApiResult(jsonResult);
	}
	
	/**
	 * 创建菜单
	 */
	public static ApiResult createMenu(String accessToken, String authAppId, String jsonStr) {
		String jsonResult = HttpUtils.post(createMenu + accessToken+ "&appid="+authAppId, jsonStr);
		return new ApiResult(jsonResult);
	}
	
	private static String deleteMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";

    /**
     * 自定义菜单删除接口
     * @return ApiResult
     */
    public static ApiResult deleteMenu(String accessToken, String authAppId) {
        String jsonResult = HttpUtils.get(deleteMenuUrl + accessToken+ "&appid="+authAppId);
        return new ApiResult(jsonResult);
    }

    private static String addConditionalUrl = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=";

    /**
     * 创建个性化菜单
     * @param jsonStr json字符串
     * @return {ApiResult}
     */
    public static ApiResult addConditional(String accessToken, String authAppId, String jsonStr) {
        String jsonResult = HttpUtils.post(addConditionalUrl + accessToken+ "&appid="+authAppId, jsonStr);
        return new ApiResult(jsonResult);
    }

    private static String delConditionalUrl = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=";

    /**
     * 删除个性化菜单
     * @param menuid menuid为菜单id，可以通过自定义菜单查询接口获取。
     * @return ApiResult
     */
    public static ApiResult delConditional(String accessToken, String authAppId, String menuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("menuid", menuid);

        String url = delConditionalUrl + accessToken+ "&appid="+authAppId;

        String jsonResult = HttpUtils.post(url, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    private static String tryMatchUrl = "https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=";

    /**
     * 测试个性化菜单匹配结果
     * @param userId user_id可以是粉丝的OpenID，也可以是粉丝的微信号。
     * @return ApiResult
     */
    public static ApiResult tryMatch(String accessToken, String authAppId, String userId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", userId);

        String url = tryMatchUrl + accessToken+ "&appid="+authAppId;

        String jsonResult = HttpUtils.post(url, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    private static String getCurrentSelfMenuInfoUrl = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=";

    /**
     * 获取自定义菜单配置接口
     * @return {ApiResult}
     */
    public static ApiResult getCurrentSelfMenuInfo(String accessToken, String authAppId) {
        String jsonResult = HttpUtils.get(getCurrentSelfMenuInfoUrl + accessToken+ "&appid="+authAppId);
        return new ApiResult(jsonResult);
    }
	
}