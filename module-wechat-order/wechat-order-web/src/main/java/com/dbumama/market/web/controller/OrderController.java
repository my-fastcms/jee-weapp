package com.dbumama.market.web.controller;

import com.dbumama.market.model.ExpressTemplate;
import com.dbumama.market.model.Order;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

@RequestMapping(value="order")
@RequiresPermissions(value="/order")
@Api(description = "订单相关接口文档", basePath = "/product", tags = "订单")
public class OrderController extends BaseAppAdminController {

	@RPCInject
	private OrderService orderService;
	@RPCInject
	private PrintService printService;
	@RPCInject
	private ExpressTemplateService expressTemplateService;
	
	public void index(){
		//查询用户已添加的快递模板
		List<ExpressTemplate> templates = expressTemplateService.getUserExpTemplate(getAuthUserId());
		setAttr("expressList", templates);
		render("/order/o_index.html");
	}

	@ApiOperation(value = "普通订单列表", httpMethod = "GET", notes = "order list")
	public void list(){
		OrderListParamDto orderParamDto = new OrderListParamDto(getAuthUserId(), getPageNo());
		orderParamDto.setStartDate(getPara("startDate"));
		orderParamDto.setEndDate(getPara("endDate"));
		orderParamDto.setBuyerNickName(getPara("nickNmae"));
		orderParamDto.setReceiverPhone(getPara("receiverPhone"));
		orderParamDto.setReceiverName(getPara("receiverName"));
		orderParamDto.setOrderStatus(getPara("orderStatus"));
		orderParamDto.setPaymentStatus(getPara("paymentStatus"));
		orderParamDto.setShippingStatus(getPara("shippingStatus"));
		orderParamDto.setOrderSn(getPara("orderSn"));
		try {
			//合并订单  买家收货地址跟收货人做map key 对应的交易数据用List装
			Page<OrderResultDto> orders = orderService.list(orderParamDto);
			rendSuccessJson(orders);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}

	/**
	 * 获取快递单打印数据
	 */
	public void getExpPrintData(){
		try {
			String exp = getPara("exp");
			PrintParamDto printParamDto = new PrintParamDto(getAuthUserId(), getPara("ids"), exp);
			PrintExpResultDto orderPrintResultDto = printService.getExpressPrintData(printParamDto);
			rendSuccessJson(orderPrintResultDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void getExpPrintData4Group(){
		try {
			String exp = getPara("exp");
			PrintParamDto printParamDto = new PrintParamDto(getAuthUserId(), getPara("ids"), exp);
			PrintExpResultDto orderPrintResultDto = printService.getExpressPrintData4Group(printParamDto);
			rendSuccessJson(orderPrintResultDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 获取发货单打印数据
	 */
	public void getInvPrintData(){
		try {
			PrintParamDto printParamDto = new PrintParamDto(getAuthUserId(), getPara("ids"));
			PrintInvResultDto printInvResultDto = printService.getInvoicePrintData(printParamDto);
			rendSuccessJson(printInvResultDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void getInvPrintData4Group(){
		try {
			PrintParamDto printParamDto = new PrintParamDto(getAuthUserId(), getPara("ids"));
			PrintInvResultDto printInvResultDto = printService.getInvoicePrintData4Group(printParamDto);
			rendSuccessJson(printInvResultDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 发货详情
	 */
	public void sendgoodsList(){
		OrderResultDto orderDetailResultDto = orderService.getOrder(getParaToLong("orderIds"));
		setAttr("expresscomps", expressTemplateService.getUserExpComps(getAuthUserId()));
		setAttr("orderDetailResultDto", orderDetailResultDto);
		setAttr("orderIds", getParaToLong("orderIds"));
		render("/order/send_goods.html");
	}
	
	/**
	 * 批量发货列表
	 */
	public void sendMoreGoodsList(){
		setAttr("expresscomps", expressTemplateService.getUserExpComps(getAuthUserId()));
		setAttr("orderIds", getPara("ids"));
		setAttr("groupIds", getPara("groupIds"));
		setAttr("order_type", getPara("order_type"));
		render("/order/send_more_goods.html");
	}
	
	public void sendMoregoods(){
		String orderIds=getPara("orderIds");
		try {
			List<OrderResultDto> orderDetailResultDto=orderService.getOrders(orderIds);
			rendSuccessJson(orderDetailResultDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void sendMoregoodsGroup(){
		String groupIdsStr = getPara("groupIds");
		String groupIds [] = groupIdsStr.split("-");
		StringBuffer sbuff = new StringBuffer();
		for(String groupId : groupIds){
			List<Order> orders = orderService.getOrdersByGroup(Long.valueOf(groupId));
			for(Order order : orders){
				sbuff.append(order.getId()).append("-");
			}
		}
		
		try {
			List<OrderResultDto> orderDetailResultDto = orderService.getOrders(sbuff.toString());
			rendSuccessJson(orderDetailResultDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 批量保存发货信息
	 */
	@Before(POST.class)
	public void saveSendMoreGoods(){
		try {
			orderService.batchShipping(getAuthUserId(), getPara("items"));
			rendSuccessJson("发货成功");
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 保存发货信息
	 */
	@Before(POST.class)
	public void saveSendGoods(){
		final Long orderIds = getParaToLong("orderIds");
		final String exp_key = getPara("exp_key");
		final String exp_name = getPara("exp_name");
		final String bill_number = getPara("bill_number");
		SendGoodParamDto sendGoodParam = new SendGoodParamDto();
		sendGoodParam.setOrderId(orderIds);
		sendGoodParam.setExpKey(exp_key);
		sendGoodParam.setExpName(exp_name);
		sendGoodParam.setAuthUserId(getAuthUserId());
		sendGoodParam.setBillNumber(bill_number);
		try {
			orderService.shipping(sendGoodParam);
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 更改自提商品发货信息
	 */
	@Before(POST.class)
	public void sendPickupGood(){
		final Long orderId = getParaToLong("id");
		try {
			orderService.sendPickupGood(orderId,getAuthUserId());
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 退款
	 */
	public void refund(){
		String orderIds=getPara("orderIds");
		List<OrderResultDto> orderDetailResultDto=orderService.getOrders(orderIds);
		setAttr("orderDetailResultDto", orderDetailResultDto);
		setAttr("orderIds", orderIds);
		render("/order/refund.html");
	}
	
	/**
	 * 拼团订单退款
	 */
	public void refundGroup(){
		String groupIdsStr = getPara("groupIds");
		String groupIds [] = groupIdsStr.split("-");
		StringBuffer sbuff = new StringBuffer();
		for(String groupId : groupIds){
			List<Order> orders = orderService.getOrdersByGroup(Long.valueOf(groupId));
			for(Order order : orders){
				sbuff.append(order.getId()).append("-");
			}
		}
		List<OrderResultDto> orderDetailResultDto=orderService.getOrders(sbuff.toString());
		setAttr("orderDetailResultDto", orderDetailResultDto);
		setAttr("orderIds", sbuff.toString());
		render("/order/refund.html");
	}
	
	/**
	 * 保存退款信息
	 */
	@Before(POST.class)
	public void saveRefund(){
		final String items = getPara("items");
		try {
			orderService.refund(getAuthUserId(), items);
			rendSuccessJson("退款成功");
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}

}
