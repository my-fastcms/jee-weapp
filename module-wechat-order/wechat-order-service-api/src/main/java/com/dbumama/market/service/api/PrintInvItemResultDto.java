package com.dbumama.market.service.api;

@SuppressWarnings("serial")
public class PrintInvItemResultDto extends OrderResultDto {

	private String contact_name;			//发货联系人
	private String city;					//发货人所在城市
	private String country;					//发货人所在县区
	private String province;				//发货人所在省
	private String addr;					//发货人详细地址
	private String memo;					//发货人备注信息
	private String phone;					//发货人联系电话，手机
	private String seller_company;			//发货人公司
	private String zip_code;				//发货人邮编
	
	//以下为公众账号信息
	private String nick_name;				//公众号名称
	private String head_img;				//公众号头像
	private String principal_name;			//公众号账号主体
	private String qrcode_url;				//公众号二维码
	
	public PrintInvItemResultDto(){
	}
	
	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSeller_company() {
		return seller_company;
	}

	public void setSeller_company(String seller_company) {
		this.seller_company = seller_company;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}
	
	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getPrincipal_name() {
		return principal_name;
	}

	public void setPrincipal_name(String principal_name) {
		this.principal_name = principal_name;
	}

	public String getQrcode_url() {
		return qrcode_url;
	}

	public void setQrcode_url(String qrcode_url) {
		this.qrcode_url = qrcode_url;
	}
	
}
