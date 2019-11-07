package com.dbumama.market.web.controller;

import java.util.Base64;

import com.dbumama.market.web.core.controller.BaseController;
import com.dbumama.market.web.core.render.Base64ImageRender;
import com.dbumama.market.web.core.render.QrcodeRender;

import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="qrcode")
public class QrcodeController extends BaseController{
	
	//生成二维码图片流
	public void genio(){
		final String content = getPara("url");
	    render(new QrcodeRender(content));
	}
	
	//生成二维码图片流
	public void genImage64(){
		String qrcode = getPara("url");
		qrcode=new String(Base64.getUrlDecoder().decode(qrcode.getBytes()));
	    render(new QrcodeRender(qrcode));
	}
	
	public void genadminio(){
		final String content = getPara("url");
	    render(new QrcodeRender(getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort() + content));
	}
	
	public void base64(){
		final String content = getPara("url");
		render(new Base64ImageRender(content));
	}
	
}
