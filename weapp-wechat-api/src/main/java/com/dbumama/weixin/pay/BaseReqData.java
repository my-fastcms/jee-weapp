package com.dbumama.weixin.pay;

/**
 * wjun_java@163.com
 * 2015年11月4日
 */
public abstract class BaseReqData extends BaseReqCommData{
	
	private static final long serialVersionUID = 1L;
	protected String appid;
	protected String mch_id;
	
	protected BaseReqData(String appId, String mchId, String mchSecKey){
		super();
		this.appid = appId;
		this.mch_id = mchId;
		this.mch_sec_key = mchSecKey;
	}
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

}
