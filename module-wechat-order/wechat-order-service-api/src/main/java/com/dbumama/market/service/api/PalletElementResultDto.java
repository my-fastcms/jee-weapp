package com.dbumama.market.service.api;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PalletElementResultDto implements Serializable{
	String label;	//显示文本
	String key;		//JSON数据 key值
	String type;	//类型 分table span div img等
	String text;	//显示文本
	String imgSrc;	//当type为img的时候有值

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	
}
