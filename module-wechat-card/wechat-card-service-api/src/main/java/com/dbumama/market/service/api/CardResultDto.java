package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * 卡券dto
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class CardResultDto extends AbstractResultDto {

	private Long id;
	private String cardId;   	//卡券号
	
	//团购券：GROUPON; 
	//折扣券：DISCOUNT; 
	//礼品券：GIFT; 
	//代金券：CASH; 
	//通用券：GENERAL_COUPON; 
	//会员卡：MEMBER_CARD; 
	//景点门票：SCENIC_TICKET；
	//电影票：MOVIE_TICKET； 
	//飞机票：BOARDING_PASS； 会议门票：MEETING_TICKET； 汽车票：BUS_TICKET;
	private String cardType;	//
	
	private String cardName;	//卡券名。
	private String dateInfo; 	//使用日期，有效期的信息
	private Integer quantity;	//卡券库存
	private Integer totalQuantity;	//卡券总库存
	private String brandName;	//商户名称
	private String brandLogo;	//商户logo
	
	//“CARD_STATUS_NOT_VERIFY”,待审核；
	//“CARD_STATUS_VERIFY_FALL”,审核失败；
	//“CARD_STATUS_VERIFY_OK”，通过审核；
	//“CARD_STATUS_USER_DELETE”，卡券被用户删除；
	//“CARD_STATUS_USER_DISPATCH”，在公众平台投放过的卡券
	private String status;		//状态
	private String statusCn;	//状态显示名称
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getDateInfo() {
		return dateInfo;
	}
	public void setDateInfo(String dateInfo) {
		this.dateInfo = dateInfo;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getBrandLogo() {
		return brandLogo;
	}
	public void setBrandLogo(String brandLogo) {
		this.brandLogo = brandLogo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusCn() {
		return statusCn;
	}
	public void setStatusCn(String statusCn) {
		this.statusCn = statusCn;
	}
	
}
