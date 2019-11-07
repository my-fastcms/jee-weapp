package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.ZanTag;
import com.dbumama.market.service.api.ZanTagService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Ret;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value = "zan/tag")
public class ZanTagController extends BaseAppAdminController {
	
	@RPCInject
	private ZanTagService zanTagService;
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "tagName", message = "请设置标签名称"),
	})
	public void save(String tagName,Long tagId) throws Exception{
		ZanTag zanTag = zanTagService.save(getAuthUserId(), tagName, tagId);
		renderJson(Ret.ok().set("zanTag", zanTag));
	}
	
	public void list(){
		List<ZanTag> zanTags = zanTagService.findZanTagsByShopId(getAuthUserId());
		if(zanTags == null){
			renderSuccess();
		}else{
			renderJson(Ret.ok().set("zanTags", zanTags));
		}
	}
	
}
