package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * 商品限时打折
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class ProdPromotionResultDto extends AbstractResultDto{

	private String promotionPrice;
	private String promotionTag;
	private String promotionTime;
	private String promotionInfo;
	public String getPromotionPrice() {
		return promotionPrice;
	}
	public void setPromotionPrice(String promotionPrice) {
		this.promotionPrice = promotionPrice;
	}
	public String getPromotionTag() {
		return promotionTag;
	}
	public void setPromotionTag(String promotionTag) {
		this.promotionTag = promotionTag;
	}
	public String getPromotionTime() {
		return promotionTime;
	}
	public void setPromotionTime(String promotionTime) {
		this.promotionTime = promotionTime;
	}
	public String getPromotionInfo() {
		return promotionInfo;
	}
	public void setPromotionInfo(String promotionInfo) {
		this.promotionInfo = promotionInfo;
	}
	
}
