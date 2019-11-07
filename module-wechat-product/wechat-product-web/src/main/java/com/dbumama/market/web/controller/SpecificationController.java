package com.dbumama.market.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.Specification;
import com.dbumama.market.service.api.SpecificationException;
import com.dbumama.market.service.api.SpecificationResultDto;
import com.dbumama.market.service.api.SpecificationService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value = "specification", viewPath = "specification")
@RequiresPermissions(value="/specification")
public class SpecificationController extends BaseAppAdminController {
	@RPCInject
	private SpecificationService specificationService;

	public void index() {
		render("specification_index.html");
	}

	public void list() {
		rendSuccessJson(specificationService.list(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("active")));
	}

	public void add() {
		render("specification_add.html");
	}

	public void edit() {
		Specification specification = (Specification) specificationService.findById(getParaToLong(0));
		if (specification != null) {
			setAttr("specification", specification);
			setAttr("specifitionValue", specificationService.getSpeciValues(specification.getId()));
		}
		render("specification_add.html");
	}

	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "name", message = "请输入规格名称"),
        @Form(name = "active", message = "请选择状态"),
	})
	public void save(Long id, String name, Integer active,String memo,Integer orders) {

		Specification specification = specificationService.findById(id);
		
		if(specification == null) {
			specification = new Specification();
			specification.setAppId(getAuthUserId()).setType(1);
		}
		specification.setName(name);
		specification.setActive(active);
		specification.setMemo(memo);
		specification.setOrders(orders);
		
		try {
			Specification specificationDto = specificationService.save(specification, getPara("items"), getAuthUserId());
			SpecificationResultDto dto = specificationService.getSpeciAndVaules(specificationDto.getId());
			rendSuccessJson(dto);
		} catch (SpecificationException e) {
			rendFailedJson(e.getMessage());
		}
	}

	public void addSpecification() {
		render("specification_new_add.html");
	}

}
