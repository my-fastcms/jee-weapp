package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.ExpressImg;
import com.dbumama.market.model.ExpressTemplate;
import com.dbumama.market.service.api.ExpTplSaveParamDto;
import com.dbumama.market.service.api.ExpressTemplateService;
import com.dbumama.market.service.api.OrderException;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "express")
public class ExpressController extends BaseAppAdminController{

	@RPCInject
	private ExpressTemplateService expressTemplateService;
	
	/**
	 * 获取用户支持的快递公司
	 */
	public void comps(){
		setAttr("expresscomps", expressTemplateService.getUserExpComps(getAuthUserId()));
		render("/order/exp_list.html");
	}
	
	public void getTplBgImagesByCompkey(){
		String expkey = getPara("expkey");
		try {
			List<ExpressImg> expressImgs = expressTemplateService.getExpTemplateBackImage(expkey); 
			rendSuccessJson(expressImgs);	
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
		
	}
	
	public void getUserTplByKey(){
		try {
			ExpressTemplate expTemplate = expressTemplateService.getUserExpTemplateByKey(getPara("expKey"), getAuthUserId());
			rendSuccessJson(expTemplate);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void delTemplate(){
		try {
			expressTemplateService.delTemplate(getPara("expKey"), getAuthUserId());
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	@Before(POST.class)
	public void saveTemplate(){
		String expkey = getPara("expkey");
		if("".equals(expkey) || expkey==null){
			rendFailedJson("您还没有选择快递公司!");
			return;
		}
		String expname = getPara("expname");
		String expbgimg = getPara("expbgimg");
		if(StrKit.isBlank(expname) || StrKit.isBlank(expbgimg)){
			rendFailedJson("快递名称跟背景图不能为空");
			return;
		}
		
		String tplcontent = getPara("tplcontent");
		String designhtml = getPara("designhtml");
		int pagewidth = getParaToInt("pagewidth", 0);
		int pageheight = getParaToInt("pageheight", 0);
		int offsetx = getParaToInt("offsetx", 0);
		int offsety = getParaToInt("offsety", 0);
		
		ExpTplSaveParamDto expTemplateSaveParamDto = new ExpTplSaveParamDto(expkey, expname, expbgimg, getAuthUserId());
		expTemplateSaveParamDto.setTplcontent(tplcontent);
		expTemplateSaveParamDto.setDesignhtml(designhtml);
		expTemplateSaveParamDto.setPagewidth(pagewidth);
		expTemplateSaveParamDto.setPageheight(pageheight);
		expTemplateSaveParamDto.setOffsetx(offsetx);
		expTemplateSaveParamDto.setOffsety(offsety);
		
		try {
			rendSuccessJson(expressTemplateService.saveTemplate(expTemplateSaveParamDto));
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
		
	}
	
	public void setBgImage(){
		String expkey = getPara("expKey");
		List<ExpressImg> expressImgs = expressTemplateService.getExpTemplateBackImage(expkey);
		setAttr("expImgs", expressImgs);
		render("/order/exp_change_bg.html");
	}
	
	public void initPallet(){
		setAttr("elementCategoryMap", expressTemplateService.initPalletElement(getAuthUserId()));
		render("/order/exp_tpl_setting.html");
	}
		
}
