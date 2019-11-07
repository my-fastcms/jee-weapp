/**
 * 文件名:PhoneCodeService.java
 * 版本信息:1.0
 * 日期:2015-10-10
 * 版权所有
 */
package com.dbumama.market.service.api;

import com.dbumama.market.model.UserCode;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-10-10
 */
public interface PhoneCodeService {

	/**（企业付款）
	 * 根据登录用户的手机号获取短信验证码
	 * 并把验证码发送到对应手机上 
	 * @param phone
	 * @return
	 */
	public String getCodeByCompanyPay(String phone, String ip);
	
	/**
	 * 根据手机号获取短信验证码
	 * 并把验证码发送到对应手机上 
	 * @param phone
	 * @return
	 * @throws UserException
	 */
	public String getCode(String phone,String ip) throws UserException;
	
	/**
	 * 忘记密码
	 * @param phone
	 * @param ip
	 * @return
	 * @throws UserException
	 */
	public String getCodeByForget(String phone, String ip) throws UserException;
	
	public String verifyCode(String phone,String ip) throws UserException;
	
	UserCode getVerifyUserCode(String phone, String code);

	/**
	 * 驾校获取手机验证码
	 * @param buyerId
	 * @param phone
	 * @param ip
	 * @return
	 */
//	public String getCodeByDriver(Long buyerId, String phone, String ip);

}
