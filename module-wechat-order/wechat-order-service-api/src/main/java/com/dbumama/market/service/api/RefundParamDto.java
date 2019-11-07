package com.dbumama.market.service.api;

import java.math.BigDecimal;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class RefundParamDto extends AbstractParamDto {

	private Long orderId;
	private BigDecimal refundFee; //退款金额
	
	public RefundParamDto(Long authUserId, Long orderId, BigDecimal refundFee){
		this.authUserId = authUserId;
		this.orderId = orderId;
		this.refundFee = refundFee;
	}
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public BigDecimal getRefundFee() {
		return refundFee;
	}
	public void setRefundFee(BigDecimal refundFee) {
		this.refundFee = refundFee;
	}
	
}
