package com.dbumama.market.service.api;

import java.io.File;

import com.dbumama.market.service.common.AbstractPageParamDto;

@SuppressWarnings("serial")
public class AuthUserParamDto extends AbstractPageParamDto{

	private Long authUserId;
	private String payMchId;
	private String paySecretKey;
	private File certFile;			//公众号证书文件
	private Integer serviceType;	//服务类型，0小程序，1订阅号，2服务号
	
	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	public AuthUserParamDto(Long sellerId, Integer pageNo) {
		super(sellerId, pageNo);
	}
	public Long getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(Long authUserId) {
		this.authUserId = authUserId;
	}
	public String getPayMchId() {
		return payMchId;
	}
	public void setPayMchId(String payMchId) {
		this.payMchId = payMchId;
	}
	public String getPaySecretKey() {
		return paySecretKey;
	}
	public void setPaySecretKey(String paySecretKey) {
		this.paySecretKey = paySecretKey;
	}
	public File getCertFile() {
		return certFile;
	}
	public void setCertFile(File certFile) {
		this.certFile = certFile;
	}
	
}
