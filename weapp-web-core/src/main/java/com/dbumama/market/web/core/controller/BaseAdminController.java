/**
 * 文件名:BaseController.java
 * 版本信息:1.0
 * 日期:2015-5-9
 * Copyright 广州点步信息科技
 * 版权所有
 */
package com.dbumama.market.web.core.controller;

import com.dbumama.market.model.SellerUser;
import com.jfinal.log.Log;

/**
 * @author: wjun.java@gmail.com
 * @date:2015-5-9
 */
public abstract class BaseAdminController extends BaseController {
	
	public static final Log log = Log.getLog(BaseAdminController.class);
	
	public SellerUser getSellerUser(){
		Object principal =  getSubject().getPrincipal(); 
		return principal !=null && principal instanceof SellerUser ? (SellerUser) principal : null;
	}
	
	protected Long getSellerId(){
		return getSellerUser()==null ? null : getSellerUser().getId();
	}
	
}
