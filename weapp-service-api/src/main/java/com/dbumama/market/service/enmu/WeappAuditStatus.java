package com.dbumama.market.service.enmu;

/**
 * 小程序审核状态
 * @author wangjun
 * 微信官方：审核状态，其中0为审核成功，1为审核失败，2为审核中
 */
public enum WeappAuditStatus {
	
	/** 审核通过 */
	success,

	/** 审核失败 */
	fail,

	/** 正在审核中 */
	auditing
	
}
