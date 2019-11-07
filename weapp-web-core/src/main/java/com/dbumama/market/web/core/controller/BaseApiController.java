package com.dbumama.market.web.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.service.api.AuthUserService;
import com.jfinal.core.NotAction;
import io.jboot.Jboot;
import org.apache.log4j.Logger;

public abstract class BaseApiController extends BaseController{
	
	public Logger log = Logger.getLogger(getClass());

	protected AuthUserService authUserService = Jboot.service(AuthUserService.class);
	
	protected JSONObject messageJson;
	
	@NotAction
	public void setMessageJson(JSONObject messageJson){
		this.messageJson = messageJson;
	}
	
	@NotAction
	public JSONObject getMessageJson(){
		return this.messageJson;
	}
	
	protected JSONObject getJSONRespones(){
		JSONObject resp = new JSONObject();
		resp.put("res", getImageDomain());
		return resp;
	}

	protected int getPageNo(){
		return this.getMessageJson().getInteger("page") == null
				|| this.getMessageJson().getInteger("page") <=0 ? 1 : this.getMessageJson().getInteger("page");
	}
	
	protected int getPageSize(){
		return this.getMessageJson().getInteger("rows")==null 
				|| this.getMessageJson().getInteger("rows") <=0 ? 20 : this.getMessageJson().getInteger("rows");
	}
	
	@NotAction
	public String getAppId(){
		return this.getMessageJson().getString("appid");
	}
	
	@NotAction
	public BuyerUser getBuyerUser(){
		return (BuyerUser)getSession().getAttribute(WeappConstants.BUYER_USER_IN_SESSION);
	}
	
	protected Long getBuyerId(){
		return getBuyerUser()==null ? null : getBuyerUser().getId();
	}
	
	protected String getBuyerOpenId(){
		return getBuyerUser()==null ? null : getBuyerUser().getOpenId();
	}
	
	protected AuthUser getAuthUser(){
		return authUserService.getAuthUserByAppId(getAppId());
	}
	
	protected Long getAuthUserId(){
		return getAuthUser()==null ? null : getAuthUser().getId();
	}
	
	/*protected Long getSellerId(){
		return getAuthUser()==null ? null : getAuthUser().getSellerId();
	}*/

	@NotAction
	public String getJSONPara(String paramName){
		return getMessageJson().getString(paramName);
	}
	
	@NotAction
	public Long getJSONParaToLong(String paramName){
		return getMessageJson().getLong(paramName) ;
	}
	
	@NotAction
	public Integer getJSONParaToInteger(String paramName){
		return getMessageJson().getInteger(paramName);
	}
	
}
