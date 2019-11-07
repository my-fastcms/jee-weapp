package com.dbumama.market.web.controller;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.Payee;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.*;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.dbumama.market.web.core.utils.IpKit;
import com.dbumama.weixin.api.CompQrcodeApi;
import com.jfinal.captcha.CaptchaRender;
import io.jboot.Jboot;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * 
* @ClassName: CompanyPayController
* @Description: 企业付款插件Controller
* @author PC
*
 */
@RequestMapping(value = "companypay")
public class CompanyPayController extends BaseAppAdminController{
	
	public static final Object lockobj = new Object();
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private PayeeService payeeService;
	@RPCInject
	private PaymentRecordService paymentRecordService;
	@RPCInject
	private SellerUserService sellerUserService;
	@RPCInject
	private AuthCertService authCertService;
	@RPCInject
	private PhoneCodeService phoneCodeService;
	
	public void index(){
		AuthUser authUser = getAuthUser();
		if(authUser !=null){
			SellerUser sellerUser = sellerUserService.findById(authUser.getSellerId());
			if(sellerUser != null){
				setAttr("phone", sellerUser.getPhone());	
			}
		}
		render("index.html");
	}
	
	public void record(){
		render("record.html");
	}
	
	public void tpl(){
		render("companypay_tpl.html");
	}
	
	public void list(){
		renderSuccess(payeeService.list(getAuthUserId()));
	}
	
	/**
	 * 获取付款记录数据
	 */
	public void recordList(){
		Date start_date=this.getParaToDate("start_date");
		Date end_date=this.getParaToDate("end_date");
		renderSuccess(paymentRecordService.list(getAuthUserId(),getPara("payee_name"),getParaToInt("status"),
				getPageNo(), getPageSize(),start_date,end_date));
	}
	
	/**
	 * 发送手机验证码
	 */
	public void phoneCode(){
		SellerUser sellerUser = getSellerUser();
		if(sellerUser == null){
			rendFailedJson("用户登录已过期，请重新登录");
		}
		try {
			rendSuccessJson(phoneCodeService.getCodeByCompanyPay(sellerUser.getPhone(), IpKit.getRealIpV2(getRequest())));
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 增加收款人二维码
	 */
	public void add(){
		String scenceId = "10016";
		
		synchronized (lockobj) {
			String scenceIdStr = String.valueOf(System.currentTimeMillis());
			scenceId = scenceId + scenceIdStr.substring(0, 10);
		}
		ApiResult qrcodeRes = CompQrcodeApi.createTemporaryStr(600, scenceId,  authUserService.getAccessToken(getAuthUser()));
		String ticket = qrcodeRes.getStr("ticket");
		String url = CompQrcodeApi.getShowQrcodeUrl(ticket);
		setAttr("qrcode", url);
		setAttr("scenceId", scenceId);
		render("payee_add.html");
	}
	
	/**
	 * 删除收款人
	 */
	public void deletePayee(){
		try {
			payeeService.deletePayee(getParaToLong("id"));
			renderSuccess();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}catch(Exception ex){
			rendFailedJson("系统错误");
		}
	}
	
	/**
	 * 付款
	 */
	public void savePay(){
		final String captchaToken = getPara("captcha");
		if(!CaptchaRender.validate(this, captchaToken)){
			rendFailedJson("验证码错误");
			return;
		}
		
		SellerUser sellerUser = getSellerUser();
		if(sellerUser == null){
			rendFailedJson("用户登录已过期，请重新登录");
		}

		final String payment_money = getPara("payment_money");
		final String explain = getPara("payment_money");
		final Long payee_id = getParaToLong("payee_id");
		final String payee_name = getPara("payee_name");
		final String phone_code = getPara("phone_code");
		
		try {
			paymentRecordService.pay(getAuthUserId(),payee_id,explain,payment_money,payee_name,phone_code,sellerUser.getPhone());
			renderSuccess();
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}catch(Exception ex){
			rendFailedJson("系统错误");
		}
	}
	
	/**
	 * 检测是否有新增付款人扫码
	 */
	public void checkScan(){
		String scenceId  = getPara("scenceId");
		ApiResult userInfo = Jboot.getCache().get(WeappConstants.WECHAT_ADD_COMPANYPAY_CACHE, scenceId);
		
		if(userInfo == null){
			renderSuccess();
			return;
		}
		
		Payee payee = payeeService.findByOpenId(getAuthUserId(), userInfo.getStr("openid"));
		
		if(payee == null){
			payee = new Payee();
			payee.setAppId(getAuthUserId()).setOpenId(userInfo.getStr("openid")).setCreated(new Date()).setUpdated(new Date());
		}
		
		payee.setActive(true).setUpdated(new Date());
		
		payeeService.saveOrUpdate(payee);
		
		renderSuccess(payee);
		
	}
	
}
