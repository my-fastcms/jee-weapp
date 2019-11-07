package com.dbumama.market.service.config;

import io.jboot.app.config.annotation.ConfigModel;

@ConfigModel(prefix = "weapp.file")
public class AliyunOssConfig {

    private String aliyunOssAk;
    private String aliyunOssSk;
    private String aliyunOssBucket;
    private String aliyunOssEndpoint;

    /**
     * @return the aliyunOssAk
     */
    public String getAliyunOssAk() {
        return aliyunOssAk;
    }
    /**
     * @param aliyunOssAk the aliyunOssAk to set
     */
    public void setAliyunOssAk(String aliyunOssAk) {
        this.aliyunOssAk = aliyunOssAk;
    }
    /**
     * @return the aliyunOssSk
     */
    public String getAliyunOssSk() {
        return aliyunOssSk;
    }
    /**
     * @param aliyunOssSk the aliyunOssSk to set
     */
    public void setAliyunOssSk(String aliyunOssSk) {
        this.aliyunOssSk = aliyunOssSk;
    }
    /**
     * @return the aliyunOssBucket
     */
    public String getAliyunOssBucket() {
        return aliyunOssBucket;
    }
    /**
     * @param aliyunOssBucket the aliyunOssBucket to set
     */
    public void setAliyunOssBucket(String aliyunOssBucket) {
        this.aliyunOssBucket = aliyunOssBucket;
    }
    /**
     * @return the aliyunOssEndpoint
     */
    public String getAliyunOssEndpoint() {
        return aliyunOssEndpoint;
    }
    /**
     * @param aliyunOssEndpoint the aliyunOssEndpoint to set
     */
    public void setAliyunOssEndpoint(String aliyunOssEndpoint) {
        this.aliyunOssEndpoint = aliyunOssEndpoint;
    }

}
