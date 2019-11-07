package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;

@SuppressWarnings("serial")
public class BuyerProdItemResultDto extends AbstractResultDto{

	private Long id;
	private String goodId;
    private String goodImg;
    private String goodName;
    private String goodPrice;
    private String mainImg;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGoodId() {
		return goodId;
	}
	public void setGoodId(String goodId) {
		this.goodId = goodId;
	}
	public String getGoodImg() {
		return goodImg;
	}
	public void setGoodImg(String goodImg) {
		this.goodImg = goodImg;
	}
	public String getGoodName() {
		return goodName;
	}
	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}
	public String getGoodPrice() {
		return goodPrice;
	}
	public void setGoodPrice(String goodPrice) {
		this.goodPrice = goodPrice;
	}
	public String getMainImg() {
		return mainImg;
	}
	public void setMainImg(String mainImg) {
		this.mainImg = mainImg;
	}
    
	
}
