package com.dbumama.market.service.enmu;

public enum GroupStatus {
	/** 拼团中 */
	grouping,

	/** 组团成功 */
	success,

	/** 组团失败 */
	fail,
	
	/** 超时，规定时间内未达成拼团人数的情况，不可直接失败，因为如果开启模拟成团的话，使用匿名用户参团成功 
	 *  如果只有团长一人的团，直接fail，表示拼团失败  **/
	timeout
}
