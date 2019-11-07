package com.dbumama.market.web.controller;

import com.dbumama.market.web.core.controller.BaseController;

import io.jboot.web.controller.annotation.RequestMapping;
@RequestMapping(value = "component", viewPath = "component")
public class ComponentController extends BaseController{
     public void index(){
    	 render("/component/component_set.html");
     }
}
