package com.dbumama.market.web.controller;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.jfinal.kit.Ret;
import com.dbumama.weixin.api.CompShorturlApi;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
/**
 * 
* @ClassName: ShortUrlController
* @Description: 短链接生成工具controller
* @author PC
* @date 2018年12月6日
*
 */
@RequestMapping(value = "shorturl", viewPath = "shorturl")
public class ShortUrlController extends BaseAuthUserController{
	
	@RPCInject
	private AuthUserService authUserService;
	
	public void index(){
		render("/shorturl/short_url_index.html");
	}
	
	public void generateUrl(){
		ApiResult shortUrl = CompShorturlApi.getShortUrl(getPara("longUrl"),authUserService.getAccessToken(getAuthUser()));
		rendSuccessJson(Ret.ok().set("shortUrl", shortUrl.get("short_url")));
	}

}
