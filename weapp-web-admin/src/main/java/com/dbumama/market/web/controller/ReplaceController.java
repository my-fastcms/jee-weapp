package com.dbumama.market.web.controller;

import java.math.BigDecimal;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.kit.Ret;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;


/**
* @ClassName: ReplaceController
* @Description: 用于代发红包的控制类
* @author PC
* @date 2018年12月19日
*/
@RequestMapping(value = "replace")
public class ReplaceController extends BaseAppAdminController{

	@RPCInject
	private SellerUserService sellerUserService;
	@RPCInject
	private AuthUserService authUserService;
	
	public void getAccountBalance(){
		//代发公众号
		AuthUser authUser = authUserService.findById(getAuthUserId());
		SellerUser sellerUser = sellerUserService.findById(authUser.getSellerId());
		if(sellerUser.getBalance() == null || sellerUser.getBalance().compareTo(new BigDecimal(0)) !=1){
			rendFailedJson("账户余额不足，请充值");
		}else{
			renderJson(Ret.ok().set("data",sellerUser.getBalance()));			
		}
	}
	
}
