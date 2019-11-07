package com.dbumama.market.service.common;

import java.io.Serializable;

/**
 * 服务调用传输基类
 * wjun_java@163.com
 * 2016年7月6日
 */
@SuppressWarnings("serial")
public abstract class AbstractParamDto implements Serializable{
	protected String appId;			//微信公众账号app_id, 与app_secert对应
	protected Long authUserId;
	protected Integer active;
	protected Integer status;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * @return the active
	 */
	public Integer getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(Integer active) {
		this.active = active;
	}
	/**
	 * @return the authUserId
	 */
	public Long getAuthUserId() {
		return authUserId;
	}
	/**
	 * @param authUserId the authUserId to set
	 */
	public void setAuthUserId(Long authUserId) {
		this.authUserId = authUserId;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
}
