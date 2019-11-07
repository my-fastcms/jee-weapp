package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class ImagepathResultDto extends AbstractResultDto {
	private Long id;
	private String mediaId;	//对应微信图片素材media_id，调用微信接口使用
	private String imgPath;
	private String imgUrl;
	private String title;
	private String size;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the mediaId
	 */
	public String getMediaId() {
		return mediaId;
	}

	/**
	 * @param mediaId the mediaId to set
	 */
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
