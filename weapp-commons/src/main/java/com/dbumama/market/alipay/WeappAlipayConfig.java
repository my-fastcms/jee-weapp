package com.dbumama.market.alipay;

import io.jboot.app.config.annotation.ConfigModel;

@ConfigModel(prefix="weapp.alipay")
public class WeappAlipayConfig {

    private String alipayPartner;
	private String alipayKey;
	private String alipayNotifyUrl;
	private String alipayReturnUrl;

    /**
     * @return the alipayPartner
     */
	public String getAlipayPartner() {
		return alipayPartner;
	}
	/**
	 * @param alipayPartner the alipayPartner to set
	 */
	public void setAlipayPartner(String alipayPartner) {
		this.alipayPartner = alipayPartner;
	}
	/**
	 * @return the alipayKey
	 */
	public String getAlipayKey() {
		return alipayKey;
	}
	/**
	 * @param alipayKey the alipayKey to set
	 */
	public void setAlipayKey(String alipayKey) {
		this.alipayKey = alipayKey;
	}
	/**
	 * @return the alipayNotifyUrl
	 */
	public String getAlipayNotifyUrl() {
		return alipayNotifyUrl;
	}
	/**
	 * @param alipayNotifyUrl the alipayNotifyUrl to set
	 */
	public void setAlipayNotifyUrl(String alipayNotifyUrl) {
		this.alipayNotifyUrl = alipayNotifyUrl;
	}
	/**
	 * @return the alipayReturnUrl
	 */
	public String getAlipayReturnUrl() {
		return alipayReturnUrl;
	}
	/**
	 * @param alipayReturnUrl the alipayReturnUrl to set
	 */
	public void setAlipayReturnUrl(String alipayReturnUrl) {
		this.alipayReturnUrl = alipayReturnUrl;
	}

}
