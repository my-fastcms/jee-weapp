/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.jfinal.kit.HttpKit;

/**
 * 用户管理 API
 * https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
 */
public class CompUserApi {
	
	private static String getUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info";
	private static String getFollowers = "https://api.weixin.qq.com/cgi-bin/user/get";
	private static String batchGetUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=";
	private static String defriendUser = "https://api.weixin.qq.com/cgi-bin/tags/members/batchblacklist?access_token=";
	private static String cancelDefriendUser = "https://api.weixin.qq.com/cgi-bin/tags/members/batchunblacklist?access_token=";

	/**
	 * 获取用户基本信息（包括UnionID机制）
	 * @param openId 普通用户的标识，对当前公众号唯一
	 * @return ApiResult
	 */
	public static ApiResult getUserInfo(String accessToken, String openId) {
		ParaMap pm = ParaMap.create("access_token", accessToken).put("openid", openId).put("lang", "zh_CN");
		return new ApiResult(HttpKit.get(getUserInfo, pm.getData()));
	}
	
	/**
	 * 获取用户列表
	 * @param nextOpenid 第一个拉取的OPENID，不填默认从头开始拉取
	 * @return ApiResult
	 */
	public static ApiResult getFollowers(String accessToken, String nextOpenid) {
		ParaMap pm = ParaMap.create("access_token", accessToken);
		if (nextOpenid != null)
			pm.put("next_openid", nextOpenid);
		return new ApiResult(HttpKit.get(getFollowers, pm.getData()));
	}
	
	/**
	 * 获取用户列表
	 * @return ApiResult
	 */
	public static ApiResult getFollows(String accessToken) {
		return getFollowers(accessToken, null);
	}

	/**
	 * 批量获取用户基本信息, by Unas
	 * @param jsonStr json字符串
	 * @return ApiResult
	 */
	public static ApiResult batchGetUserInfo(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(batchGetUserInfo + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
	/**
	 * 批量获取用户基本信息
	 * @param openIdList openid列表
	 * @return ApiResult
	 */
	public static ApiResult batchGetUserInfo(String accessToken, List<String> openIdList) {
		Map<String, List<Map<String, Object>>> userListMap = new HashMap<String, List<Map<String, Object>>>();
		
		List<Map<String, Object>> userList = new ArrayList<Map<String,Object>>();
		for (String openId : openIdList) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("openid", openId);
			mapData.put("lang", "zh_CN");
			userList.add(mapData);
		}
		userListMap.put("user_list", userList);
		
		return batchGetUserInfo(accessToken, JsonUtils.toJson(userListMap));
	}
	
	private static String updateRemarkUrl = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=";
	
	/**
	 * 设置备注名
	 * @param openid 用户标识
	 * @param remark 新的备注名，长度必须小于30字符
	 * @return
	 */
	public static ApiResult updateRemark(String accessToken, String openid, String remark) {
		String url = updateRemarkUrl + accessToken;
		
		Map<String, String> mapData = new HashMap<String, String>();
		mapData.put("openid", openid);
		mapData.put("remark", remark);
		String jsonResult = HttpKit.post(url, JsonUtils.toJson(mapData));
		
		return new ApiResult(jsonResult);
	}
	
	/**
	 * 批量拉黑用户
	 * @param openIdList openid列表
	 * @return ApiResult
	 */
	public static ApiResult defriendUser(String accessToken, List<String> openIdList) {
		Map<String, List<String>> userListMap = new HashMap<String, List<String>>();
		
		userListMap.put("openid_list", openIdList);
		
		return defriendUser(accessToken, JsonUtils.toJson(userListMap));
	}

	/**
	 * 批量拉黑用户
	 * @param accessToken
	 * @param jsonStr
	 * @return
	 */
	private static ApiResult defriendUser(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(defriendUser + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
	/**
	 * 批量取消拉黑用户
	 * @param openIdList openid列表
	 * @return ApiResult
	 */
	public static ApiResult cancelDefriendUser(String accessToken, List<String> openIdList) {
		Map<String, List<String>> userListMap = new HashMap<String, List<String>>();
		
		userListMap.put("openid_list", openIdList);
		
		return cancelDefriendUser(accessToken, JsonUtils.toJson(userListMap));
	}

	/**
	 * 批量取消拉黑用户
	 * @param accessToken
	 * @param jsonStr
	 * @return
	 */
	private static ApiResult cancelDefriendUser(String accessToken, String jsonStr) {
		String jsonResult = HttpKit.post(cancelDefriendUser + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
}
