package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class PrintParamDto extends AbstractParamDto{

	private String orderIds;		//需要打印的订单数据
	private String expressKey;		//使用的哪个快递模板进行打印
	
	public PrintParamDto(Long authUserId, String orderIds, String expressKey) {
		super();
		this.authUserId = authUserId;
		this.orderIds = orderIds;
		this.expressKey = expressKey;
	}
	
	public PrintParamDto(Long authUserId, String orderIds) {
		this(authUserId, orderIds, null);
	}

	public String getOrderIds() {
		return orderIds;
	}
	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}
	public String getExpressKey() {
		return expressKey;
	}
	public void setExpressKey(String expressKey) {
		this.expressKey = expressKey;
	}
	
}
