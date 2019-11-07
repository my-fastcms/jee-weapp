/**
 * 文件名:ItemsettingController.java
 * 版本信息:1.0
 * 日期:2015-7-10
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.controller;

import java.io.File;
import java.util.List;

import com.dbumama.market.model.AuthCert;
import com.dbumama.market.service.api.AuthCertService;
import com.dbumama.market.service.api.AuthUserParamDto;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.dbumama.market.web.core.interceptor.CSRFInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-7-10
 */
@RequestMapping(value="setting")
public class SettingController extends BaseAuthUserController{
	
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private AuthCertService authCertService;
	
	public void index(){
		render("st_index.html");
	}
	
	public void list(){
		AuthUserParamDto userParam = new AuthUserParamDto(getSellerId(), getPageNo());
		rendSuccessJson(authUserService.list(userParam));
	}
	
	public void listxcx(){
		AuthUserParamDto userParam = new AuthUserParamDto(getSellerId(), getPageNo());
		userParam.setServiceType(0);
		rendSuccessJson(authUserService.list(userParam));
	}
	
	public void payconfig(){
		setAttr("defaultAuthUser", authCertService.findDefault());
		render("st_pay_config.html");
	}
	
	public void getAuths(){
		List<AuthCert> auth = authCertService.findByAppId(getAppId());
		if(auth != null && auth.size() >0) {
			rendSuccessJson(auth);
			return;
		}
		rendFailedJson("暂无数据");
	}
	
	public void addPaySetting(){
		render("st_add.html");
	}
	
	@Before(POST.class)
	public void startUse(){
		authCertService.startUse(getParaToLong("id"),getAppId());
		rendSuccessJson();
	}
	
	//更新授权用户的支付配置信息
	@Before(POST.class)
	@Clear(CSRFInterceptor.class)
	public void update(){
		List<UploadFile> uFile = null;
		try {
			 uFile = getFiles();
		} catch (Exception e) {
			setAttr("error", e.getMessage());
			redirect("/setting/payconfig/");
			return;
		}
		final String pay_mch_id = getPara("pay_mch_id");
		final String pay_secret_key = getPara("pay_secret_key");
		final Long id = getParaToLong("id");
		
		File file = null;
		if(uFile != null && uFile.size()>0){
			file = uFile.get(0).getFile();
			String fileName = file.getName();
			fileName = fileName.substring(fileName.lastIndexOf('.')+1);
			if(!"p12".equals(fileName)){
				setAttr("error", "证书文件必须是p12文件格式");
				redirect("/setting/payconfig/");
				return;
			}
		}
		authCertService.saveOrUpdate(id,getAppId(),pay_mch_id,pay_secret_key,file);
		redirect("/setting/payconfig/");
	}
	
}
