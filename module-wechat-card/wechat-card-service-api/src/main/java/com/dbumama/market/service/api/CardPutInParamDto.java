package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class CardPutInParamDto extends AbstractParamDto{

	private String[] openIds;	//指定用户
	private Boolean isAll;			//是否全部用户
	private String cardId;			//卡券
	
	public CardPutInParamDto (String cardId, Boolean isAll){
		this.cardId = cardId;
		this.isAll = isAll;
	}
	
	public String[] getOpenIds() {
		return openIds;
	}
	public void setOpenIds(String[] openIds) {
		this.openIds = openIds;
	}
	public Boolean getIsAll() {
		return isAll;
	}
	public void setIsAll(Boolean isAll) {
		this.isAll = isAll;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	
}
