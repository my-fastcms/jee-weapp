package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractPageParamDto;

@SuppressWarnings("serial")
public class ProductParamDto extends AbstractPageParamDto{
    private Long productId;
    private Integer isMarketable;
    private String saleOver;	//售罄
	private Long promotionId;
    private Long categoryId;
    private String name;
    private String productIds;  //多个商品id
    private Boolean needImageDomain;  //是否需要图片域名
    

	public Boolean getNeedImageDomain() {
		return needImageDomain;
	}

	public void setNeedImageDomain(Boolean needImageDomain) {
		this.needImageDomain = needImageDomain;
	}

	public ProductParamDto(Long appId){
    	this.authUserId = appId;
    }
    
    public ProductParamDto(Long appId, Long productId){
    	this.authUserId = appId;
    	this.productId = productId;
    }
    
    public ProductParamDto(Long appId, Integer pageNo){
    	super(appId, pageNo);
    }
    public ProductParamDto(Long appId, Integer pageNo, Integer pageSize){
    	super(appId, pageNo,pageSize);
    }

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public Integer getIsMarketable() {
		return isMarketable;
	}

	public void setIsMarketable(Integer isMarketable) {
		this.isMarketable = isMarketable;
	}
	
	/**
	 * @return the saleOver
	 */
	public String getSaleOver() {
		return saleOver;
	}

	/**
	 * @param saleOver the saleOver to set
	 */
	public void setSaleOver(String saleOver) {
		this.saleOver = saleOver;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public String getProductIds() {
		return productIds;
	}

	public void setProductIds(String productIds) {
		this.productIds = productIds;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
