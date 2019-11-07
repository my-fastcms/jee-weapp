package com.dbumama.market.web.controller;

import java.util.List;
import java.util.Map;

import com.dbumama.market.model.InvoiceTemplate;
import com.dbumama.market.service.api.InvTplSaveParamDto;
import com.dbumama.market.service.api.InvoiceTemplateService;
import com.dbumama.market.service.api.OrderException;
import com.dbumama.market.service.api.PalletElementResultDto;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "invoice")
public class InvoiceController extends BaseAppAdminController {
	
	@RPCInject
	private InvoiceTemplateService invoiceTemplateService;
	
	public void setting(){
		Map<String, List<PalletElementResultDto>> elementCategoryMap = invoiceTemplateService.initPalletElement(getAuthUserId());
		setAttr("elementCategoryMap", elementCategoryMap);
		
		Map<String, List<PalletElementResultDto>> columnCategoryMap = invoiceTemplateService.initTableColumnElement(getAuthUserId());
		setAttr("columnCategoryMap", columnCategoryMap);
		
		render("/order/inv_tpl_setting.html");
	}
	
	public void getUserTpl(){
		try {
			InvoiceTemplate invoiceTemplate = invoiceTemplateService.getUserTpl(getAuthUserId());
			rendSuccessJson(invoiceTemplate);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	@Before(POST.class)
	public void save(){
		final String tplcontent = getPara("tplcontent");
		final String designhtml = getPara("designhtml");
		final String tablehtml = getPara("tablehtml");
		final String imghtml = getPara("imghtml");
		final int pagewidth = getParaToInt("pagewidth", 0);
		final int pageheight = getParaToInt("pageheight", 0);
		final int offsetx = getParaToInt("offsetx", 0);
		final int offsety = getParaToInt("offsety", 0);
		
		InvTplSaveParamDto tplParamDto = new InvTplSaveParamDto(tplcontent, designhtml, tablehtml, getAuthUserId());
		if(StrKit.notBlank(imghtml)) tplParamDto.setImghtml(imghtml);
		tplParamDto.setPagewidth(pagewidth);
		tplParamDto.setPageheight(pageheight);
		tplParamDto.setOffsetx(offsetx);
		tplParamDto.setOffsety(offsety);
		try {
			InvoiceTemplate invoicetpl = invoiceTemplateService.save(tplParamDto);
			rendSuccessJson(invoicetpl);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
		
	}

}
