package com.dbumama.market.web.controller;

import com.dbumama.market.model.PlatActivitys;
import com.dbumama.market.service.api.PlatActivitysJoinService;
import com.dbumama.market.service.api.PlatActivitysService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BasePlatController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping(value="activity")
public class ActivityController extends BasePlatController {

	@RPCInject
	private PlatActivitysService platActivitysService;
	@RPCInject
	private PlatActivitysJoinService platActivitysJoinService;
	
	public void index(){
		render("activity_index.html");
	}
	
	public void add(){
		PlatActivitys platActivitys = platActivitysService.findById(getPara(0));
		setAttr("platActivitys", platActivitys);
		render("activity_add.html");
	}
	
	public void list(){
		renderSuccess(platActivitysService.list(getPlatUserId(), getPageNo(), getPageSize(), getPara("name")));
	}
	
	@Before(POST.class)
	public void save(){
		try{
			platActivitysService.save(
					getPlatUserId(), getParaToLong("activityId"), getPara("name"),
					getParaToDate("startDate"), getParaToDate("endDate"),
					getPara("explain"), getPara("imgPath") 
					);
			renderSuccess();
		}catch(WxmallBaseException e){
			rendFailedJson(e.getMessage());
		}
	}
	
	public void look(){
		setAttr("activityId", getPara(0));
		setAttr("activity", platActivitysService.findOnlieById(getParaToLong(0)));
		render("activity_look.html");
	}
	
	public void lookList(){
		renderSuccess(platActivitysJoinService.lookList(getParaToLong("activityId"), getPara("nickName"), getPageNo(), getPageSize()));
	}
	
	public void stop(){
		try{
			platActivitysJoinService.stopAct(getParaToLong("activityId"));
			renderSuccess();
		}catch(WxmallBaseException e){
			rendFailedJson(e.getMessage());
		}
	}
}
