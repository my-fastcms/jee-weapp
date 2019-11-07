package com.dbumama.market.service.api;

import java.util.Date;

import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class ImageSowingResultDto  extends AbstractResultDto{
	private Long id;
	private Long appId;	
	private Date created;
	private Date updated;
	private String sowingImg;
	private String sowingUrl;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public String getSowingImg() {
		return sowingImg;
	}
	public void setSowingImg(String sowingImg) {
		this.sowingImg = sowingImg;
	}
	public String getSowingUrl() {
		return sowingUrl;
	}
	public void setSowingUrl(String sowingUrl) {
		this.sowingUrl = sowingUrl;
	}
	
	

}
