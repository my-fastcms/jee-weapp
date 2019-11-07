/**
 * 文件名:IndexController.java
 * 版本信息:1.0
 * 日期:2015-5-10
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.PlatUser;
import com.dbumama.market.model.UserCode;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.BuyerUserService;
import com.dbumama.market.service.api.OrderService;
import com.dbumama.market.service.api.PhoneCodeService;
import com.dbumama.market.service.api.PlatUserService;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.service.api.UserException;
import com.dbumama.market.web.core.controller.BaseController;
import com.dbumama.market.web.core.utils.IpKit;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-5-10
 */
@RequestMapping(value="/")
public class IndexController extends BaseController{

	@RPCInject
	private PhoneCodeService phoneCodeService;
	@RPCInject
	private PlatUserService platUserService;
	@RPCInject
	private SellerUserService sellerUserService;
	@RPCInject
	private BuyerUserService buyerUserService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private OrderService orderService;
	
	final int loginFailureLockCount = 9;

	public void index(){
		setAttr("buyerCount", buyerUserService.getCount());
		setAttr("sellerCount", sellerUserService.getCount());
		setAttr("weappCount", authUserService.getWeappCount());
		render("index.html");
	}
	
	public void chartData(){
		//一个月内每天的已支付订单总数统计
		List<Record> records=orderService.getOrderByDay(null);
		JSONArray array=new JSONArray();
		for (Record record : records) {
			if(record.getLong("numCount") != null && record.getStr("dt") != null){
				String dataStr = record.getStr("dt");
				JSONObject json=new JSONObject();
				json.put("incomeNum", record.getLong("numCount"));
				json.put("year", dataStr.split("-")[0]);
				json.put("month", dataStr.split("-")[1]);
				json.put("day", dataStr.split("-")[2]);
				array.add(json);
			}
		}
		//一个月内每天的总订单总数统计
		List<Record> records2=orderService.getOrderByMonth(null);
		JSONArray array2=new JSONArray();
		for (Record record : records2) {
			if(record.getLong("numCount") != null && record.getStr("dt") != null){
				String dataStr = record.getStr("dt");
				JSONObject json=new JSONObject();
				json.put("incomeNum", record.getLong("numCount"));
				json.put("year", dataStr.split("-")[0]);
				json.put("month", dataStr.split("-")[1]);
				json.put("day", dataStr.split("-")[2]);
				array2.add(json);
			}
		}
		
		Map<String, JSONArray> dataMap = new HashMap<String, JSONArray>();
		dataMap.put("data1", array);
		dataMap.put("data2", array2);
		
		rendSuccessJson(dataMap);
	}
	
	public void login(){
		render("login.html");
	}
	
	public void logout(){
		Subject currentUser = SecurityUtils.getSubject();
		if (SecurityUtils.getSubject().getSession() != null) {
			currentUser.logout();
		}
		redirect("/");
	}
	
	@Clear
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "username", message = "请输入账号"),
        @Form(name = "password", message = "请输入密码"),
        @Form(name = "captchaToken", message = "请输入验证码")
	})
	public void auth(String username, String password, String captchaToken){
		
		if(!CaptchaRender.validate(this, captchaToken)){
			rendFailedJson("验证码错误");
			return;
		}
		
		UsernamePasswordToken token = new UsernamePasswordToken(username, DigestUtils.md5Hex(password));
        Subject subject = SecurityUtils.getSubject();

        try {
        	
        	token.setRememberMe(false);
            subject.login(token);
            
            
            if (subject.isAuthenticated()) {

                if (getParaToBoolean("rememberMe") != null && getParaToBoolean("rememberMe")) {
                    setCookie("loginName", username, 60 * 60 * 24 * 7);
                } else {
                    removeCookie("loginName");
                }
                
                PlatUser seller = platUserService.findByAccount(username);
                seller.setLoginIp(IpKit.getRealIpV2(getRequest()));
                seller.setUpdated(new Date());
                platUserService.saveOrUpdate(seller);
                
            }
            
            rendSuccessJson();
        } catch (UnknownAccountException une) {
        	rendFailedJson("用户名不存在");
        } catch (LockedAccountException lae) {
        	rendFailedJson("用户被锁定");
        } catch (IncorrectCredentialsException ine) {
        	rendFailedJson("用户名或密码不正确");
        } catch (ExcessiveAttemptsException exe) {
        	rendFailedJson("账户密码错误次数过多，账户已被限制登录1小时");
        } catch (Exception e) {
            e.printStackTrace();
            rendFailedJson("服务异常，请稍后重试");
        }
		
	}
	
	@Clear
    public void captcha() {
        renderCaptcha();
    }
	
	public void register(){
		render("register.html");
	}
	
	public void sendCode(){
		final String phone = getPara("phone");
		if(StringUtils.isEmpty(phone)){
			rendFailedJson("手机号码为空");
			return;
		}
		try {
			rendSuccessJson(phoneCodeService.getCode(phone, IpKit.getRealIpV2(getRequest())));
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		} 
	}
	
	public void resetPwd(){
		final String phone = getPara("phone");
		final String password = getPara("password");
		final String confirmPwd = getPara("confirmPwd");
		final String captchaToken = getPara("captchaToken");
		final String code = getPara("phoneCode");
		if(StrKit.isBlank(phone)){
			rendFailedJson("手机号码不能为空");
			return;
		}
		if(StrKit.isBlank(password)){
			rendFailedJson("密码不能为空");
			return;
		}
		if(StrKit.isBlank(confirmPwd)){
			rendFailedJson("确认密码不能为空");
			return;
		}
		if(StrKit.isBlank(code)){
			rendFailedJson("手机验证码不能为空");
			return;
		}
		
		if(!password.equals(confirmPwd)){
			rendFailedJson("两次输入的密码不一样");
			return;
		}
		
		//check 验证码
		if(!CaptchaRender.validate(this, captchaToken)){
			rendFailedJson("验证码错误");
			return;
		}
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, code);
		if(userCode == null){
			rendFailedJson("手机验证码错误");
			return;
		}
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()){
			rendFailedJson("验证码已过期");
			return;
		}
		
		PlatUser sellerUser = platUserService.findByAccount(phone);
		if(sellerUser == null || sellerUser.getActive() != 1){
			rendFailedJson("账号信息异常");
			return;
		}
		sellerUser.setAccount(phone);
		sellerUser.setPassword(DigestUtils.md5Hex(password));
		sellerUser.setActive(1);
		sellerUser.setUpdated(new Date());
		sellerUser.update();
		rendSuccessJson();
	}
	
}
