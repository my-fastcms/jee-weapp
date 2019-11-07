/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.market.base;

import io.jboot.app.config.annotation.ConfigModel;

import java.io.Serializable;

/**
 * 存放 Weixin 服务号需要用到的各个参数
 */
@ConfigModel(prefix="wxmall.wechat")
public class ApiConfig implements Serializable {

    private String appId = null;				//第三方开放平台appid
    private String appSecret = null;			//第三方开发平台appSecret
	private String token = null;
    private String encodingAesKey = null;

	public ApiConfig() {}

    public ApiConfig(String token, String appId, String appSecret) {
        setToken(token);
        setAppId(appId);
        setAppSecret(appSecret);
    }

    public ApiConfig(String token, String appId, String appSecret, String encodingAesKey) {
        this(token, appId, appSecret);
        setEncodingAesKey(encodingAesKey);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

}


