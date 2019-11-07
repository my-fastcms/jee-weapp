package com.dbumama.weixin.pay;

import com.dbumama.market.utils.SignKit;

/**
 * wjun_java@163.com
 * 2015年11月4日
 */
public class UnifiedOrderReqData extends BaseReqData{

	private static final long serialVersionUID = 1L;
	private String openid;
	private String device_info;
	private String body;
	private String out_trade_no;
	private String total_fee;
	private String spbill_create_ip;
	private String trade_type;
	private String notify_url;
	
	public UnifiedOrderReqData(String appId, String mchId, String mchSecKey, String openId,
			String body, String out_trade_no,
			String total_fee, String spbill_create_ip, String trade_type, String notify_url) {
		
		super(appId, mchId, mchSecKey);
		
		this.device_info = "WEB";
		this.nonce_str = SignKit.genRandomString32();
		this.body = body;
		this.out_trade_no = out_trade_no;
		this.total_fee = total_fee;
		this.spbill_create_ip = spbill_create_ip;
		this.openid = openId;
		this.trade_type = trade_type;
		this.notify_url = notify_url;
		//根据API给的签名规则进行签名
		//把签名数据设置到Sign这个属性中
        setSign(SignKit.sign(toMap(),  this.getMch_sec_key()));
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	/**
	 * @return the notify_url
	 */
	public String getNotify_url() {
		return notify_url;
	}

	/**
	 * @param notify_url the notify_url to set
	 */
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
}
