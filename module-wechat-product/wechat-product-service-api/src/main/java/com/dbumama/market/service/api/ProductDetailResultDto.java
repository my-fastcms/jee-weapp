package com.dbumama.market.service.api;

import com.dbumama.market.model.Product;
import com.dbumama.market.service.common.AbstractResultDto;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class ProductDetailResultDto extends AbstractResultDto{
	private List<String> imageList; 							//商品轮播图片
	private Product product;
	private List<SpecificationResultDto> specifications;	//商品规格
//	private ProdPromotionResultDto promotionInfo;			//限时促销
//	private List<ProdFullCutResultDto> fullCutInfo;			//满减
//	private ProdCashbackResultDto cashback;					//订单返现
//	private ProdGroupResultDto groupInfo;           		//拼团活动
	private String wirlessUrl;										//商品无线地址
	
	HashMap<String, ProductSpecPriceResultDto> priceMap;	//商品规格以及价格库存组合
	
//	List<GroupingResultDto> groupings;						//该商品正在拼团的团集合
//	/**
//	 * @return the groupings
//	 */
//	public List<GroupingResultDto> getGroupings() {
//		return groupings;
//	}
//	/**
//	 * @param groupings the groupings to set
//	 */
//	public void setGroupings(List<GroupingResultDto> groupings) {
//		this.groupings = groupings;
//	}
	/**
	 * @return the priceMap
	 */
	public HashMap<String, ProductSpecPriceResultDto> getPriceMap() {
		return priceMap;
	}
	/**
	 * @param priceMap the priceMap to set
	 */
	public void setPriceMap(HashMap<String, ProductSpecPriceResultDto> priceMap) {
		this.priceMap = priceMap;
	}
	public String getWirlessUrl() {
		return wirlessUrl;
	}
	public void setWirlessUrl(String wirlessUrl) {
		this.wirlessUrl = wirlessUrl;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public List<SpecificationResultDto> getSpecifications() {
		return specifications;
	}
	public void setSpecifications(List<SpecificationResultDto> specifications) {
		this.specifications = specifications;
	}
	public List<String> getImageList() {
		return imageList;
	}
	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}
//	public ProdPromotionResultDto getPromotionInfo() {
//		return promotionInfo;
//	}
//	public void setPromotionInfo(ProdPromotionResultDto promotionInfo) {
//		this.promotionInfo = promotionInfo;
//	}
//	public List<ProdFullCutResultDto> getFullCutInfo() {
//		return fullCutInfo;
//	}
//	public void setFullCutInfo(List<ProdFullCutResultDto> fullCutInfo) {
//		this.fullCutInfo = fullCutInfo;
//	}
//	public ProdCashbackResultDto getCashback() {
//		return cashback;
//	}
//	public void setCashback(ProdCashbackResultDto cashback) {
//		this.cashback = cashback;
//	}
//	public ProdGroupResultDto getGroupInfo() {
//		return groupInfo;
//	}
//	public void setGroupInfo(ProdGroupResultDto groupInfo) {
//		this.groupInfo = groupInfo;
//	}
	
}
