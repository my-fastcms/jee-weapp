package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.service.api.OrderException;
import com.dbumama.market.service.api.OrderListParamDto;
import com.dbumama.market.service.api.OrderMobileResultDto;
import com.dbumama.market.service.api.OrderService;
import com.dbumama.market.web.core.controller.BaseMobileController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value ="order/group", viewPath="order")
public class OrderGrouponController extends BaseMobileController{

	@RPCInject
	private OrderService orderService;
	
	public void index(){
		//setAttr("order_status", "success");
		render("g_index.html");
	}
	
	public void success(){
		setAttr("group_status", "success");
		render("g_index.html");
	}
	
	public void fail(){
		setAttr("group_status", "fail");
		render("g_index.html");
	}
	
	public void grouping(){
		setAttr("group_status", "grouping");
		render("g_index.html");
	}
	
	public void list(){
		OrderListParamDto orderParamDto = new OrderListParamDto(getAuthUserId(), getBuyerId(), getPageNo());
		orderParamDto.setGroupStatus(getPara("order_status"));
		try {
			List<OrderMobileResultDto> orderListResultDtos = orderService.list4Mobile(orderParamDto);
			rendSuccessJson(orderListResultDtos);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}
