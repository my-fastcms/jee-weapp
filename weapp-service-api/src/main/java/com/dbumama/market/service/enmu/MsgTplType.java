package com.dbumama.market.service.enmu;

public enum MsgTplType {

	/** 拼团中 */
	grouping,

	/** 拼团成功 */
	groupsuccess,
	
	/** 拼团失败 **/
	groupfail,

	/** 支付成功 */
	paied,
	
	/** 待支付 */
	unpay,
	
	/** 支付失败 **/
	payfail,
	
	/** 订单创建 **/
	created,
	
	/** 订单关闭 **/
	closed,
	
	/** 订单发货通知 **/
	shiped,
	
	/** 预约课程成功通知 **/
	appointcoursesuccess,
	
	/** 预约课程失败通知 **/
	appointcoursefail,
	
	/** 预约考试成功通知 **/
	appointexamsuccess,
	
	/** 预约考试失败通知 **/
	appointexamfail,
	
	/** 销售员推广成功通知 **/
	agentgeneralizesuccess,
	
	/** 获得未结算佣金通知 **/
	getcommission,
	
	/** 砍价订单进度通知 **/
	bargainschedule
	
}
