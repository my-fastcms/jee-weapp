package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class ImageGroupResultDto extends AbstractResultDto {
	private Long id;
	private Long imageNum;
	private String groupName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getImageNum() {
		return imageNum;
	}

	public void setImageNum(Long imageNum) {
		this.imageNum = imageNum;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
