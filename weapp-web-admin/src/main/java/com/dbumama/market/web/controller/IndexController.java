/**
 * 文件名:IndexController.java
 * 版本信息:1.0
 * 日期:2015-5-10
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.controller;

import com.dbumama.market.WeappConstants;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.model.UserCode;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.dbumama.market.web.core.interceptor.CSRFInterceptor;
import com.dbumama.market.web.core.interceptor.WeimoLoginInterceptor;
import com.dbumama.market.web.core.shiro.WechatLoginToken;
import com.dbumama.market.web.core.utils.IpKit;
import com.dbumama.weixin.api.CompQrcodeApi;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import com.vdurmont.emoji.EmojiParser;
import io.jboot.Jboot;
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.utils.RequestUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.session.JbootSessionConfig;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;

import java.util.Date;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-5-10
 */
@SuppressWarnings("restriction")
@RequestMapping(value="/")
public class IndexController extends BaseAdminController{

	private static final Object lockobj = new Object();  
	
	@RPCInject
	private PhoneCodeService phoneCodeService;
	@RPCInject
	private SellerUserService sellerUserService;
	@RPCInject
	private InvitecodeRuleService invitecodeRuleService;
	@RPCInject
	private OrderService orderService;
	@RPCInject
	private AuthUserService authUserService;

	JbootSessionConfig jbootSessionConfig = Jboot.config(JbootSessionConfig.class);

	public void index(){
		if(RequestUtil.isMobileBrowser(getRequest())){
			redirect("/invitecode/app");
		}else{
			redirect("/authuser");
		}
	}
	
	public void login(){
		
		String scenceId = WeappConstants.QRCODE_LOGIN_PREFIX;
		synchronized (lockobj) {
			String scenceIdStr = String.valueOf(System.currentTimeMillis());
			scenceId = scenceId + scenceIdStr.substring(0, 10);
		}
		AuthUser authUser = authUserService.getAuthUserByAppId(WeappConstants.WECHAT_LOGIN_APPID);
		try {
			ApiResult qrcodeRes = CompQrcodeApi.createTemporaryStr(600, scenceId, authUserService.getAccessToken(authUser));
			final String ticket = qrcodeRes.getStr("ticket");
			final String url = CompQrcodeApi.getShowQrcodeUrl(ticket);
			setAttr("qrcode", url);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		setAttr("scenceId", scenceId);
		setAttr("moblie", RequestUtil.isMobileBrowser(getRequest()));
		render("login.html");
	}
	
	public void welcome(){
		String scenceId = getPara("scenceId");
		setAttr("scenceId", scenceId);
		ApiResult result = sellerUserService.getUserInfoByScence(scenceId);
		
		if(result == null){
			setAttr("error", "获取用户信息失败，请重新扫码登录");
		}
		
		render("welcome.html");
	}
	
	@Before(POST.class)
	@EnableLimit(rate=1, fallback="login")
	@EmptyValidate({
        @Form(name = "scenceId", message = "登录错误"),
        @Form(name = "phone", message = "请输入手机号码"),
        @Form(name = "password", message = "请输入登录密码"),
        @Form(name = "confirmpwd", message = "请输入确认密码"),
        @Form(name = "code", message = "请输入短信验证码"),
	})
	public void bindUser(String scenceId, String phone, String password, String confirmpwd, String code){
		
		if(!password.trim().equals(confirmpwd.trim())){
			rendFailedJson("两次输入密码不一致");
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
		
		ApiResult result = sellerUserService.getUserInfoByScence(scenceId);
		if(result == null){
			rendFailedJson("获取用户信息失败，登陆不成功，请重新扫码登录");
			return;
		}
		
		//检查通过openid是否存在用户
		SellerUser sellerUser = sellerUserService.findByOpenid(result.getStr("openid"));
		if(sellerUser != null){
			//如果已经存在微信扫码用户，但是未绑定手机号
			//再检查是否存在手机用户
			SellerUser phoneUser = sellerUserService.findByPhone(phone);
			//如果存在手机账户，又存在微信账户的情况下
			if(phoneUser == null){
				//不存在手机用户的话
				//把手机号直接绑定在微信用户账号上
				String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
		        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
		        String encryptPassword = hash.toHex();
				sellerUser.setPassword(encryptPassword);
				sellerUser.setSalt(salt);
				sellerUser.setPhone(phone);
			}else{
				if(StrKit.isBlank(phoneUser.getOpenId())){
					//此处使用哪个账号？？
					//统一使用微信登录账号，保留原来的手机号账号，但是把手机号设置为空，保留其绑定的微信公众好，如果有的话
					phoneUser.setPhone(null);
					sellerUserService.update(phoneUser);
					
					String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
			        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
			        String encryptPassword = hash.toHex();
					sellerUser.setPassword(encryptPassword);
					sellerUser.setSalt(salt);
					sellerUser.setPhone(phone);
				}else{
					if(!phoneUser.getOpenId().equals(result.getStr("openid"))){
						rendFailedJson("该手机号已绑定在["+phoneUser.getNick()+"]微信号上，不可重复绑定");
						return;
					}
					//不会存在两个账号openid相同的情况
				}
			}
		}else{
			//如果openid查不到用户 那么通过手机号码查
			sellerUser = sellerUserService.findByPhone(phone);
			if(sellerUser == null){
				sellerUser = new SellerUser();
				sellerUser.setCreated(new Date());
				String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
		        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
		        String encryptPassword = hash.toHex();
				sellerUser.setPassword(encryptPassword);
				sellerUser.setSalt(salt);
				sellerUser.setPhone(phone);
			}
		}

        sellerUser.setOpenId(result.getStr("openid"))
        .setNick(EmojiParser.removeAllEmojis(result.getStr("nickname") == null ? "" : result.getStr("nickname")))
        .setHeaderImg(result.getStr("headimgurl")).setUpdated(new Date());
        
        sellerUserService.saveOrUpdate(sellerUser);
        
        //绑定微信账号后，做一次账号密码登录
        UsernamePasswordToken token = new UsernamePasswordToken(phone, password);
        Subject subject = SecurityUtils.getSubject();

        try {
        	token.setRememberMe(false);
            subject.login(token);
            if (subject.isAuthenticated()) {
            	setCookie(jbootSessionConfig.getCookieName(), (String)subject.getSession().getId(), jbootSessionConfig.getCookieMaxAge());
                if (getParaToBoolean("rememberMe") != null && getParaToBoolean("rememberMe")) {
                	token.setRememberMe(true);
                    setCookie("loginName", phone, 60 * 60 * 24 * 7);
                } else {
                    removeCookie("loginName");
                }
                SellerUser seller = sellerUserService.findByPhone(phone);
                seller.setLoginIp(IpKit.getRealIpV2(getRequest()));
                seller.setLoginTime(new Date());
                seller.setUpdated(new Date());
                sellerUserService.saveOrUpdate(seller);
            }
            
            rendSuccessJson();
        } catch (UnknownAccountException une) {
        	rendFailedJson("用户名不存在");
        } catch (LockedAccountException lae) {
        	rendFailedJson("用户被锁定");
        } catch (IncorrectCredentialsException ine) {
        	rendFailedJson("用户名或密码不正确");
        } catch (ExcessiveAttemptsException exe) {
        	rendFailedJson(exe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            rendFailedJson("服务异常，请稍后重试");
        }
        
	}
	
	@Clear
	@Before(POST.class)
	public void wxlogin(){
		ApiResult result = sellerUserService.getUserInfoByScence(getPara("scenceId"));
		if(result == null){
			rendSuccessJson();
			return;
		}
		SellerUser sellerUser = sellerUserService.findByOpenid(result.getStr("openid"));
		
		if(sellerUser != null && StrKit.notBlank(sellerUser.getPhone())){
			//20190613 注意:没有绑定手机号的微信号不让登录
			//直接做登录
			WechatLoginToken token = new WechatLoginToken(sellerUser.getOpenId(), sellerUser.getOpenId());
	        Subject subject = SecurityUtils.getSubject();
	        token.setRememberMe(false);
            subject.login(token);
            if (subject.isAuthenticated()) {
            	setCookie(jbootSessionConfig.getCookieName(), (String)subject.getSession().getId(), jbootSessionConfig.getCookieMaxAge());
                if (getParaToBoolean("rememberMe") != null && getParaToBoolean("rememberMe")) {
                	token.setRememberMe(true);
                    setCookie("loginName", sellerUser.getNick(), 60 * 60 * 24 * 7);
                } else {
                    removeCookie("loginName");
                }
                SellerUser seller = sellerUserService.findByOpenid(sellerUser.getOpenId());
                seller.setLoginIp(IpKit.getRealIpV2(getRequest())).setLoginTime(new Date());
                seller.setUpdated(new Date());
                seller.setNick(EmojiParser.removeAllEmojis(result.getStr("nickname"))).setHeaderImg(result.getStr("headimgurl"));
                sellerUserService.saveOrUpdate(seller);
            }
            rendSuccessJson(JFinal.me().getContextPath() + "/index");
		}else{
			//新授权进来的用户，跳转到是创建新用户，还是绑定现有手机账户
			rendSuccessJson(JFinal.me().getContextPath() + "/welcome?scenceId="+getPara("scenceId"));
		}
	}
	
	@Clear
	@Before(WeimoLoginInterceptor.class)
	public void weimologin(){}
	
	public void logout(){
		Subject currentUser = SecurityUtils.getSubject();
		
		SellerUser seller = getSellerUser();
		
		boolean isYz = seller !=null && StrKit.notBlank(seller.getKdtId());
		
		if (SecurityUtils.getSubject().getSession() != null) {
			currentUser.logout();
		}
		setCookie("app_id_in_cookie", "", 0);
		
		
		if(isYz){
			redirect("https://app.youzanyun.com/center/my-apps");
		}else{
			redirect("/");			
		}
		
	}
	
	@Clear
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "username", message = "请输入账号"),
        @Form(name = "password", message = "请输入密码"),
	})
	@EnableLimit(rate=1, fallback="login")
	public void auth(String username, String password, String captchaToken){
		
		if(!Jboot.isDevMode() && StrKit.isBlank(captchaToken)){
			rendFailedJson("请输入验证码");
			return;
		} 
		
		if(!Jboot.isDevMode() && !CaptchaRender.validate(this, captchaToken)){
			rendFailedJson("验证码错误");
			return;
		}
		
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();

        try {
        	
        	token.setRememberMe(false);
            subject.login(token);
            
            
            if (subject.isAuthenticated()) {
            	
            	setCookie(jbootSessionConfig.getCookieName(), (String)subject.getSession().getId(), jbootSessionConfig.getCookieMaxAge());
            	
                if (getParaToBoolean("rememberMe") != null && getParaToBoolean("rememberMe")) {
                	token.setRememberMe(true);
                    setCookie("loginName", username, 60 * 60 * 24 * 7);
                } else {
                    removeCookie("loginName");
                }
                
                SellerUser seller = sellerUserService.findByPhone(username);
                seller.setLoginIp(IpKit.getRealIpV2(getRequest())).setLoginTime(new Date());
                seller.setUpdated(new Date());
                sellerUserService.saveOrUpdate(seller);
                
            }
            
            rendSuccessJson();
        } catch (UnknownAccountException une) {
        	rendFailedJson("用户名不存在");
        } catch (LockedAccountException lae) {
        	rendFailedJson("用户被锁定");
        } catch (IncorrectCredentialsException ine) {
        	rendFailedJson("用户名或密码不正确");
        } catch (ExcessiveAttemptsException exe) {
        	rendFailedJson(exe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            rendFailedJson("服务异常，请稍后重试");
        }
		
	}

	public void register(){
		setAttr("incode", getPara("incode"));
		setAttr("incodeRule", invitecodeRuleService.findRule());
		render("register.html");
	}
	
	@Clear
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "phone", message = "手机号码不能为空"),
        @Form(name = "password", message = "密码不能为空"),
        @Form(name = "confirmPwd", message = "确认密码不能为空"),
        @Form(name = "phoneCode", message = "手机验证码不能为空"),
        @Form(name = "captchaToken", message = "验证码不能为空"),
	})
	@EnableLimit(rate=1, fallback="register")
	public void doRegister(String phone, String password, String confirmPwd, String captchaToken, String phoneCode){
		
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
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, phoneCode);
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
		
		String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        SimpleHash hash = new SimpleHash("md5", password, salt, 2);
        String encryptPassword = hash.toHex();
		
		SellerUser sellerUser = new SellerUser();
		sellerUser.setPhone(phone).setPassword(encryptPassword).setSalt(salt).setActive(1).setCreated(new Date()).setUpdated(new Date());
		sellerUserService.save(sellerUser);
		rendSuccessJson();
	}
	
	@Before(POST.class)
	public void sendBindCode(String phone){
		if(StringUtils.isEmpty(phone)){
			rendFailedJson("手机号码为空");
			return;
		}
		try {
			rendSuccessJson(phoneCodeService.verifyCode(phone, IpKit.getRealIpV2(getRequest())));
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		} 
	}
	
	@Before(POST.class)
	@EnableLimit(rate=1, fallback="login")
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
	
	@Clear
    public void captcha() {
		renderCaptcha();
    }
	
	@Clear
	public void forbid(){}
	
	@Clear
	public void unauth(){}
	
	@Clear
	@Before(CSRFInterceptor.class)
	public void forgetPwd(){
		render("forgetpwd.html");
	}
	
	@Before(POST.class)
	@EnableLimit(rate=1, fallback="login")
	public void sendPwdCode(){
		final String phone = getPara("phone");
		try {
			rendSuccessJson(phoneCodeService.getCodeByForget(phone, IpKit.getRealIpV2(getRequest())));
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		} 
	}
	
	@Clear
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "phone", message = "手机号码不能为空"),
        @Form(name = "password", message = "密码不能为空"),
        @Form(name = "confirmPwd", message = "确认密码不能为空"),
        @Form(name = "phoneCode", message = "手机验证码不能为空"),
        @Form(name = "captchaToken", message = "验证码不能为空"),
	})
	@EnableLimit(rate=1, fallback="login")
	public void resetPwd(String phone, String password, String confirmPwd, String captchaToken, String phoneCode){
		//check 验证码
		if(!CaptchaRender.validate(this, captchaToken)){
			rendFailedJson("验证码错误");
			return;
		}
		try {
			sellerUserService.resetPwd(phone, password, confirmPwd, captchaToken, phoneCode);
			rendSuccessJson();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}
