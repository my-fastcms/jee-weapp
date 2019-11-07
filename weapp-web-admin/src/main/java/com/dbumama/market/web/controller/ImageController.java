package com.dbumama.market.web.controller;

import com.dbumama.market.web.core.controller.BaseAdminController;

import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "image")
public class ImageController extends BaseAdminController {
	
	public void index(){
		render("image_list.html");
	}

}
