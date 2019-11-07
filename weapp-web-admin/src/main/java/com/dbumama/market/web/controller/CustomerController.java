/**
 * 文件名:FansController.java
 * 版本信息:1.0
 * 日期:2015-6-9
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.controller;

import com.dbumama.market.base.ApiResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.BuyerUserService;
import com.dbumama.market.service.api.CustomerException;
import com.dbumama.market.service.api.CustomerParamDto;
import com.dbumama.market.service.api.MemberResultDto;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.dbumama.weixin.api.CompTagApi;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-6-9
 */
@RequestMapping(value="customer", viewPath="customer")
@RequiresPermissions(value="/customer")
public class CustomerController extends BaseAuthUserController {

	static final String CACHENAME = "/customer/list";
	static final String CACHENAME_KEY = "customer_key";
	
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private BuyerUserService buyerUserService;
	
	public void index(){
		String appId = getAppId();
		if(StrKit.isBlank(appId)){
			setAttr("tags", null);
		}else{
			ApiResult res = CompTagApi.get(authUserService.getAccessToken(getAuthUser()));
			setAttr("tags", res);
		}
		render("customer_index.html");
	}
	
	public void members(){
		render("customer_member.html");
	}
	
	public void listmember(){
		CustomerParamDto params = new CustomerParamDto(getAuthUserId(), getPageNo());
		//params.setNickName(getPara("qname"));
		params.setActive(getParaToInt("active"));
		try {
			Page<MemberResultDto> members = buyerUserService.listMembers(params);
			rendSuccessJson(members);	
		} catch (CustomerException e) {
			rendFailedJson(e.getMessage());
		}		
	}
	
	public void list(){
		CustomerParamDto params = new CustomerParamDto(getAuthUserId(), getPageNo());
		params.setNameOrOpenId(getPara("nameOrOpenId"));
		params.setTagsBasic(getPara("tagsBasic"));
		params.setTagidList(getPara("tagidList"));
		params.setsSceneBasic(getPara("sSceneBasic"));
		params.setSubscribeScene(getPara("subscribeScene"));
		params.setActive(getParaToInt("active"));
		params.setSex(getParaToInt("sex"));
		params.setFollowBasic(getPara("followBasic"));
		params.setFollowStartDate(getParaToDate("followStartDate"));
		params.setFollowEndDate(getParaToDate("followEndDate"));
		params.setCancelBasic(getPara("cancelBasic"));
		params.setCancelStartDate(getParaToDate("cancelStartDate"));
		params.setCancelEndDate(getParaToDate("cancelEndDate"));
		try {
			Page<BuyerUser> buyerUser = buyerUserService.list(params, getAppId(), getPageNo());
			rendSuccessJson(buyerUser);	
		} catch (CustomerException e) {
			rendFailedJson(e.getMessage());
		}		
	}
	
	public void detail(){
		BuyerUser buser = buyerUserService.findById(getParaToLong("id"));
		setAttr("customer", buser);
		render("customer_detail.html");
	}
	
	public void del(){
		String ids = getPara("ids");
		for(String id : ids.split("-")){
			BuyerUser user = buyerUserService.findById(Long.valueOf(id));
			user.setActive(0);
			buyerUserService.update(user);
		}
		rendSuccessJson("操作成功！");
	}
	
	public void undel(){
		String ids = getPara("ids");
		for(String id : ids.split("-")){
			BuyerUser user = buyerUserService.findById(Long.valueOf(id));
			user.setActive(1);
			buyerUserService.update(user);
		}
		rendSuccessJson("操作成功！");
	}
	
	public void selectCustomer(){
		render("customer_select.html");
	}
	
}
