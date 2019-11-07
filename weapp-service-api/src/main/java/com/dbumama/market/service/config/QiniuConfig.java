package com.dbumama.market.service.config;

import io.jboot.app.config.annotation.ConfigModel;

@ConfigModel(prefix = "weapp.file")
public class QiniuConfig {

    private String qiniuAk;
	private String qiniuSk;
	private String qiniuBucket;

    public String getQiniuAk() {
		return qiniuAk;
	}
	public void setQiniuAk(String qiniuAk) {
		this.qiniuAk = qiniuAk;
	}
	public String getQiniuSk() {
		return qiniuSk;
	}
	public void setQiniuSk(String qiniuSk) {
		this.qiniuSk = qiniuSk;
	}
	public String getQiniuBucket() {
		return qiniuBucket;
	}
	public void setQiniuBucket(String qiniuBucket) {
		this.qiniuBucket = qiniuBucket;
	}

}
