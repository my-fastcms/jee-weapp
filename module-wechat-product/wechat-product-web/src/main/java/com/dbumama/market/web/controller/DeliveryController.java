package com.dbumama.market.web.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Area;
import com.dbumama.market.model.DeliveryTemplate;
import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.service.api.DeliveryTemplateException;
import com.dbumama.market.service.api.DeliveryTemplateService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value = "delivery", viewPath = "delivery")
@RequiresPermissions(value="/delivery")
public class DeliveryController extends BaseAppAdminController {

	@RPCInject
	private AreaService areaService;
	@RPCInject
	private DeliveryTemplateService deliveryTemplateService;

	public void index() {
		render("delivery_index.html");
	}

	public void add() {
		render("delivery_add.html");
	}

	public void list() {
		rendSuccessJson(deliveryTemplateService.list(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("active")));
	}

	@Before(POST.class)
	@EmptyValidate({ @Form(name = "name", message = "请输入运费模板名称"), @Form(name = "items", message = "请设置运费模板设置"), })
	public void save(Long id, String name, Long valuation_type, Integer active) {
		
		DeliveryTemplate deliveryTemplate = deliveryTemplateService.findById(id);

		if (deliveryTemplate == null) {
			deliveryTemplate = new DeliveryTemplate();
			deliveryTemplate.setAppId(getAuthUserId());
		}

		deliveryTemplate.setName(name);
		deliveryTemplate.setValuationType(valuation_type);
		deliveryTemplate.setActive(active);
		
		try {
			DeliveryTemplate dt = deliveryTemplateService.save(deliveryTemplate, getPara("items"));
			rendSuccessJson(dt);
		} catch (DeliveryTemplateException e) {
			rendFailedJson(e.getMessage());
		}
	}

	public void edit() {
		setAttr("dtResultDto", deliveryTemplateService.findByTemplateId(getParaToLong("id")));
		render("delivery_edit.html");
	}

	/**
	 * 删除运费模板
	 */
	public void del() {
		DeliveryTemplate dt = deliveryTemplateService.findById(getParaToLong("ids"));
		try {
			dt.setActive(0);
			deliveryTemplateService.update(dt);
			rendSuccessJson(dt);
		} catch (DeliveryTemplateException e) {
			rendFailedJson(e.getMessage());
		}
	}

	public void area() {
		List<Area> listRoot = areaService.findRoots();
		JSONArray jsonArray = new JSONArray();

		for (Area area : listRoot) {
			JSONArray jsonArrayChild = new JSONArray();
			JSONObject json = new JSONObject();
			json.put("id", area.getId());
			json.put("name", area.getName());
			List<Area> listChild = areaService.getChildren(area.getId());

			for (Area areaChild : listChild) {
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("id", areaChild.getId());
				jsonChild.put("name", areaChild.getName());
				jsonArrayChild.add(jsonChild);

			}
			json.put("sub", jsonArrayChild);
			jsonArray.add(json);

		}

		renderJson(jsonArray);
	}

	// 通过配送方式获取运费模板
	public void getAllDeliverys() {
		List<DeliveryTemplate> templateList = deliveryTemplateService.getDelivTemplateByApp(getAuthUserId());
		renderJson(templateList);
	}

}
