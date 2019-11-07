package com.dbumama.market.web.controller;

import com.dbumama.market.web.core.controller.BaseMobileController;
import com.dbumama.market.web.core.render.QrcodeRender;

import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="qrcode")
public class QrcodeController extends BaseMobileController{
	
	//生成二维码图片流
	public void genio(){
		final String content = getPara("url");
	    render(new QrcodeRender(content));
	}
	
}
