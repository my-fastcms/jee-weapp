package com.dbumama.market.service.common;

import java.util.Date;

@SuppressWarnings("serial")
public class RedpackParamDto extends CommonRedpackParamDto {

	private Boolean enableCard;		//是否支持卡券 
	private String cardId;			//卡券ID
	private Integer cardCount;		//卡券数量
	private	Integer getType;		//红包领取限制
	private String area;			//区域
	private Integer areaIn;			//是否包含区域
	private String scopeKm;			//范围。多少公里
	private String scopeLnglat;		//范围。经度纬度
	private String redpackMoney;	//红包金额
	private String cardName; 		//卡券名称  
	private Boolean enableReplyConfig;//是否开启自定回复
	private String replyConfig;		//回复内容
	
	private Boolean enableRepeatGet;//是否允许跨活动领取红包
	
	public RedpackParamDto(){}

	public RedpackParamDto(Long appId, String actName, Date startDate,
			Date endDate, Long replaceAppid, Integer redpackType,
			String redpackMax1, String redpackMax2, String redpackMin,
			Integer redpackCount, String redpackWishText, String redpackMemo,
			Boolean enableCard, String cardId, Integer cardCount,
			Integer getType, String area, Integer areaIn, String scopeKm,
			String scopeLnglat, String redpackMoney, String cardName,
			Boolean enableReplyConfig, String replyConfig, String redpackEndMessage) {
		super(appId, actName, startDate, endDate, replaceAppid, redpackType,
				redpackMax1, redpackMax2, redpackMin, redpackCount,
				redpackWishText, redpackMemo ,redpackEndMessage);
		this.enableCard = enableCard;
		this.cardId = cardId;
		this.cardCount = cardCount;
		this.getType = getType;
		this.area = area;
		this.areaIn = areaIn;
		this.scopeKm = scopeKm;
		this.scopeLnglat = scopeLnglat;
		this.redpackMoney = redpackMoney;
		this.cardName = cardName;
		this.enableReplyConfig = enableReplyConfig;
		this.replyConfig = replyConfig;
	}
	
	public Boolean getEnableCard() {
		return enableCard;
	}
	public void setEnableCard(Boolean enableCard) {
		this.enableCard = enableCard;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public Integer getCardCount() {
		return cardCount;
	}
	public void setCardCount(Integer cardCount) {
		this.cardCount = cardCount;
	}
	public Integer getGetType() {
		return getType;
	}
	public void setGetType(Integer getType) {
		this.getType = getType;
	}
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getAreaIn() {
		return areaIn;
	}

	public void setAreaIn(Integer areaIn) {
		this.areaIn = areaIn;
	}

	public String getScopeKm() {
		return scopeKm;
	}
	public void setScopeKm(String scopeKm) {
		this.scopeKm = scopeKm;
	}
	public String getScopeLnglat() {
		return scopeLnglat;
	}
	public void setScopeLnglat(String scopeLnglat) {
		this.scopeLnglat = scopeLnglat;
	}
	public String getRedpackMoney() {
		return redpackMoney;
	}
	public void setRedpackMoney(String redpackMoney) {
		this.redpackMoney = redpackMoney;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public Boolean getEnableReplyConfig() {
		return enableReplyConfig;
	}
	public void setEnableReplyConfig(Boolean enableReplyConfig) {
		this.enableReplyConfig = enableReplyConfig;
	}
	public String getReplyConfig() {
		return replyConfig;
	}
	public void setReplyConfig(String replyConfig) {
		this.replyConfig = replyConfig;
	}
	/**
	 * @return the enableRepeatGet
	 */
	public Boolean getEnableRepeatGet() {
		return enableRepeatGet;
	}

	/**
	 * @param enableRepeatGet the enableRepeatGet to set
	 */
	public void setEnableRepeatGet(Boolean enableRepeatGet) {
		this.enableRepeatGet = enableRepeatGet;
	}
}
