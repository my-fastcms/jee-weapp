package com.dbumama.market.wxpay;

import io.jboot.app.config.annotation.ConfigModel;

/**
 * pc端微信扫码支付配置
 */
@ConfigModel(prefix = "weapp.wxpay")
public class WxpayConfig {

    private String wxpayAppId;
	private String wxpayPartner;
	private String wxpayPaternerKey;
	private String wxpayNotifyUrl;

    	/**
	 * @return the wxpayAppId
	 */
	public String getWxpayAppId() {
		return wxpayAppId;
	}
	/**
	 * @param wxpayAppId the wxpayAppId to set
	 */
	public void setWxpayAppId(String wxpayAppId) {
		this.wxpayAppId = wxpayAppId;
	}
	/**
	 * @return the wxpayPartner
	 */
	public String getWxpayPartner() {
		return wxpayPartner;
	}
	/**
	 * @param wxpayPartner the wxpayPartner to set
	 */
	public void setWxpayPartner(String wxpayPartner) {
		this.wxpayPartner = wxpayPartner;
	}
	/**
	 * @return the wxpayPaternerKey
	 */
	public String getWxpayPaternerKey() {
		return wxpayPaternerKey;
	}
	/**
	 * @param wxpayPaternerKey the wxpayPaternerKey to set
	 */
	public void setWxpayPaternerKey(String wxpayPaternerKey) {
		this.wxpayPaternerKey = wxpayPaternerKey;
	}
	/**
	 * @return the wxpayNotifyUrl
	 */
	public String getWxpayNotifyUrl() {
		return wxpayNotifyUrl;
	}
	/**
	 * @param wxpayNotifyUrl the wxpayNotifyUrl to set
	 */
	public void setWxpayNotifyUrl(String wxpayNotifyUrl) {
		this.wxpayNotifyUrl = wxpayNotifyUrl;
	}

}
