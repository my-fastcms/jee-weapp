package com.dbumama.market.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.MemberRank;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.session.JbootSessionConfig;

import javax.servlet.http.HttpSession;

@RequestMapping(value="user")
public class UserController extends BaseApiController{

	@RPCInject
	private BuyerUserService buyerUserService;
	@RPCInject
	private MemberRankService memberRankService;
	@RPCInject
	private OrderService orderService;

	JbootSessionConfig jbootSessionConfig = Jboot.config(JbootSessionConfig.class);

	@Before(ApiSessionInterceptor.class)
	public void index(){
		try{
			JSONObject result = new JSONObject();

			//待付款
			result.put("unpayedCount", orderService.getCountByBuyerAndStatus(getBuyerId(), 1));
			//组团中（待成团）
			result.put("groupingCount", orderService.getCountByBuyerAndStatus(getBuyerId(), 5));
			//已支付，未发货（含拼团）
			result.put("payedCount",  orderService.getCountByBuyerAndStatus(getBuyerId(), 2));
			//已发货
			result.put("shippedCount", orderService.getCountByBuyerAndStatus(getBuyerId(), 3));
			//交易成功
			result.put("complateCount", orderService.getCountByBuyerAndStatus(getBuyerId(), 4));
			
			if(getBuyerUser()!=null &&getBuyerUser().getMemberRankId() != null){
				MemberRank rank = memberRankService.findById(getBuyerUser().getMemberRankId());
				result.put("rank", rank);
			}
			rendSuccessJson(result);
		} catch (Exception e) {
			e.printStackTrace();
			rendFailedJson(e.getMessage());
		}	
	}
	
	@Before(POST.class)
	public void login(){
		final String code = getJSONPara("code");
		final String appId = getAppId();
		try {
			WeappLoginResultDto result = buyerUserService.loginWeapp(appId, code);
			rendSuccessJson(result);
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	//检查小程序端用户数据合法性
	@Before({POST.class})
	public void check(){
		final String sign = getJSONPara("signature");
		final String rawData = getJSONPara("rawData");
		final String encryptedData = getJSONPara("encryptedData");
		final String iv = getJSONPara("iv");
		final String openid = getJSONPara("openid");
//		WeappLoginResultDto weappLoginRes = (WeappLoginResultDto) getSession().getAttribute(getSession().getId());
//		if(weappLoginRes == null) {
//			rendFailedJson("session 失效");
//			return;
//		}
		
		BuyerUser buyer = buyerUserService.findByOpenId(openid);
		if(buyer == null){
			renderFail("buyer is not exsit");
			return;
		}
		
		WeappUserCheckParamDto userCheckParam = new WeappUserCheckParamDto(buyer.getSessionKey(), sign, rawData, encryptedData, iv);
		userCheckParam.setAppId(getAppId());
		userCheckParam.setAuthUserId(getAuthUserId());
		try {
			BuyerUser buyerRes = buyerUserService.check(userCheckParam);
			
			HttpSession session = getSession(true);
			setCookie(jbootSessionConfig.getCookieName(), session.getId(), jbootSessionConfig.getCookieMaxAge());
			//登陆后把sessionKey+openId的值存储到session
			setSessionAttr(WeappConstants.BUYER_USER_IN_SESSION, buyer);
			renderSuccess(Ret.ok().set("buyerRes", buyerRes).set("sessionId", session.getId()));
		} catch (UserException e) {
			renderFail(e.getMessage());
		}
	}
	
	public void info(){
		rendSuccessJson(buyerUserService.findByOpenId(getBuyerOpenId()));
	}
	
}
