package com.dbumama.market.web.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.FollowConfig;
import com.dbumama.market.service.api.FollowConfigService;
import com.dbumama.market.service.api.FollowReplyConfigResDto;
import com.dbumama.market.service.api.FollowReplyConfigService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Ret;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value="follow")
@RequiresPermissions(value={"/follow", "/follow/reply"}, logical = Logical.OR)
public class FollowController extends BaseAppAdminController {
	
	
	@RPCInject
	private FollowReplyConfigService followReplyConfigService;
	
	@RPCInject
	private FollowConfigService followConfigService;
	
	
	public void reply(){
		render("follow_reply_index.html");
	}

	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "followConfig", message = "请设置菜单回复消息"),
	})
	public void save(String followConfig, Boolean enableConfig){
		try {
			followReplyConfigService.save(getAuthUserId(), followConfig, enableConfig);
			renderSuccess();
		} catch (WxmallBaseException e) {
			renderFail(e.getMessage());
		}
	}
	
	public void list(){
		FollowConfig followConfig = followConfigService.findByAppId(getAuthUserId());
		if(followConfig == null){
			renderSuccess();
		}else{
			List<FollowReplyConfigResDto> replyConfigs = followReplyConfigService.findFollowReplyConfig(followConfig.getId());
			renderJson(Ret.ok().set("data", replyConfigs).set("followConfig",followConfig));
		}
	}
}
