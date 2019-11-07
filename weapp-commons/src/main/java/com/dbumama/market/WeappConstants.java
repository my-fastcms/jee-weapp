package com.dbumama.market;

public abstract class WeappConstants {

	public static final String IMAGE_DOMAIN = "http://image.dbumama.com/";
	public static final String FILE_HANDLE_TYPE = "aliyunoss";

	//把BuyerUserVo的数据放到session中
	public static final String BUYER_USER_IN_SESSION = "_buyerUser";
	/** 当前微信小程序存放在session中的key值 **/
	public static final String WEB_WEAPP_IN_SESSION = "weapp_in_session";
	/** 红包代发公众号id **/
	public static final String REDPACK_REPLACE_APPID = "wxfc6d9901f7bb2837";
	public static final String WECHAT_LOGIN_APPID = "wxfc6d9901f7bb2837";
	/** mobile config begin **/
	public static final String MOBILE_TEST_AUTH_APPID = "wxfc6d9901f7bb2837";
	public static final String MOBILE_TEST_NEEdAHTH = "false";
	public static final String MOBILE_TEST_OPENID = "oj9Un06knAHNgDti45BpAbBfn00g";

	/**==========================================wechat qrcode config begin==================================================**/
	/** 二维码扫码登录事件 **/
	public static final String QRCODE_LOGIN_PREFIX = "10000";
	/** 消息提醒 扫描的是添加消息接收者二维码 或者是添加消息预览者的二维码 **/
	public static final String QRCODE_NOTIFIY_PREFIX = "10008";
	/** 添加群发预览者 **/
	public static final String QRCODE_ADD_PREVIEWER_PREFIX = "10009";
	/** 任务宝二维码 **/
	public static final String QRCODE_TASK_PREFIX = "10010";
	/** 二维码红包 **/
	public static final String QRCODE_REDPACK_PREFIX = "10011";
	/** 积分二维码 **/
	public static final String QRCODE_JIFEN_PREFIX = "10012";
	/** 口令红包 **/
	public static final String QRCODE_TOKENPACK_PREFIX = "10013";
	/** 免单二维码 **/
	public static final String QRCODE_ASSISFREE_PREFIX = "10014";
	/** 拼团二维码 **/
	public static final String QRCODE_MULTIGROUP_PREFIX = "10015";
	/** 添加企业付款人二维码 **/
	public static final String COMPANY_PAY_PREFIX = "10016";
	/** 参数二维码 **/
	public static final String QRCODE_PARAMQRCODE_PREFIX = "10017";
	/**==========================================wechat qrcode config end==================================================**/


	/**==========================================cache qrcode config begin==================================================**/
	public static final String WECHAT_ADD_MENU_NOTIFIYER_CACHE = "wechat_menu_add_notiyer_cache_";
	public static final String WECHAT_LOGIN_CACHE = "wechat_login_cache_";
	public static final String WECHAT_ADD_PREVIEWER_CACHE = "wechat_menu_previewer_cache_";
	public static final String WECHAT_ADD_COMPANYPAY_CACHE = "wechat_menu_companypay_cache_";
	/**==========================================cache qrcode config begin==================================================**/

}
