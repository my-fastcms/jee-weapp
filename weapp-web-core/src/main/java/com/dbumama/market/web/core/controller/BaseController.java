/**
 * 文件名:BaseController.java
 * 版本信息:1.0
 * 日期:2015-5-9
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.core.controller;

import com.dbumama.market.WeappConstants;
import com.dbumama.market.utils.ResultUtil;
import com.jfinal.core.ActionException;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderManager;
import io.jboot.web.controller.JbootController;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-5-9
 */
public abstract class BaseController extends JbootController {
	
	public Logger log = Logger.getLogger(getClass());
	
    @NotAction
    protected int getPageNo(){
		return this.getParaToInt("page", 1);
	}
    
    @NotAction
	protected int getPageSize(){
		return this.getParaToInt("rows", 10);
	}
    
    @NotAction
	protected Subject getSubject() {
	    return SecurityUtils.getSubject();
	}
	
	/**
	 * HTML视图
	 * @param view 视图文件名不含.html
	 */
    @NotAction
	protected void renderHTML(String view) {
		if(view.endsWith(".html")){
			super.render(view);
		}else{
			super.render(view+".html");
		}
	}

	/***
	 * 
	 * @param success
	 * @param statusCode 状态码默认为401
	 * @param msg
	 * @param data 数组 [0]:data [1]:tokenid
	 */
	@NotAction
	protected void rendJson(boolean success, Integer statusCode, String msg, Object... data){
		Map<String,Object>json=new HashMap<String,Object>();
		json.put("Success",success);
		json.put("status",success?200:(statusCode==null?401:statusCode));
		json.put("msg",msg);
		if(data!=null&&data.length>0){
			json.put("data",data[0]);
			if(data.length>1){
				json.put("tokenid",data[1]);
			}
		}
		rendJson(json);
	}
	
	@NotAction
	protected void rendSuccessJson(Object data){
		rendJson(ResultUtil.genSuccessResult(data));
	}
	
	@NotAction
	protected void rendSuccessJson(){
		rendJson(ResultUtil.genSuccessResult());
	}
	
	@NotAction
	protected void rendFailedJsonObj(final Object obj){
		rendJson(ResultUtil.genFailedResultList(obj));
	}
	
	@NotAction
	public void rendFailedJson(final String msg){
		rendJson(ResultUtil.genFailedResult(msg));
	}
	
	@NotAction
	public void rendFailedJsonWithCode(final int code, final String msg){
		rendJson(ResultUtil.genFailedResult(code, msg));
	}
	
	@NotAction
	protected void rendFailedJson(final String code, final String msg){
		rendJson(ResultUtil.genFailedResult(code, msg));
	}
	
	@NotAction
	protected void rendJson(Object json){
		String agent = getRequest().getHeader("User-Agent");
		if(agent!=null && agent.contains("MSIE"))
			this.render(new JsonRender(json).forIE());
		else{
			this.render(new JsonRender(json));
		}
	}

	@NotAction
	protected synchronized String getUUIDStr(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	@NotAction
	protected String getImageDomain(){
		return WeappConstants.IMAGE_DOMAIN;
	}
	
	@NotAction
	public Date getParaToDate(String name){
		return toDate(getRequest().getParameter(name), null);
	}
	
	@NotAction
	private Date toDate(String value, Date defaultValue) {
		try {
			if (StrKit.isBlank(value))
				return defaultValue;
			return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(value.trim());
		} catch (Exception e) {
			throw new ActionException(400, RenderManager.me().getRenderFactory().getErrorRender(400),  "Can not parse the parameter \"" + value + "\" to Date value.");
		}
	}
	
	@NotAction
	protected void renderFail(String message){
		renderJson(Ret.fail().set("message", message));
	}
	
	@NotAction
	protected void renderSuccess(){
		renderJson(Ret.ok());
	}
	
	@NotAction
	protected void renderSuccess(Object data){
		renderJson(Ret.ok().set("data", data));
	}
	
	@NotAction
	protected void renderSuccess(String key, Object data){
		renderJson(Ret.ok().set(key, data));
	}
	
}
