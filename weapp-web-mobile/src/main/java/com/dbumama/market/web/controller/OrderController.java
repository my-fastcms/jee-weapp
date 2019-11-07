package com.dbumama.market.web.controller;

import com.dbumama.market.model.ProductReview;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseMobileController;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@RequestMapping(value = "order")
public class OrderController extends BaseMobileController{

	@RPCInject
	private OrderService orderService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private ProductReviewService productReviewService;
	@RPCInject
	private OrderGroupService orderGroupService;
	
	
	public void index(){
		render("/order/index.html");
	}
	
	/**
	 * 待支付
	 */
	public void unpay(){
		setAttr("payment_status", "unpaid");
		render("/order/index.html");
	}
	
	/**
	 * 已支付，待发货
	 */
	public void payed(){
		setAttr("payment_status", "paid");
		setAttr("shipping_status", "unshipped");
		setAttr("order_status", "unconfirmed");
		render("/order/index.html");
	}
	
	/**
	 * 已支付，已发货
	 */
	public void shipped(){
		setAttr("payment_status", "paid");
		setAttr("shipping_status", "shipped");
		setAttr("order_status", "unconfirmed");
		render("/order/index.html");
	}
	
	/**
	 * 已支付，已发货，已完成
	 */
	public void completed(){
		setAttr("payment_status", "paid");
		setAttr("shipping_status", "shipped");
		setAttr("order_status", "completed");
		render("/order/index.html");
	}
	
	/** 维权订单,已退款*/
	public void refund(){
		setAttr("payment_status", "refunded");
		render("/order/refund_index.html");
	}
	/** 等待退款*/
	public void waitRefund(){
		setAttr("payment_status", "waitRefund");
		render("/order/refund_index.html");
	}
	/**退款订单列表*/
	public void refunds(){
		OrderListParamDto orderParamDto = new OrderListParamDto(getAuthUserId(), getBuyerId(), getPageNo());
		orderParamDto.setPaymentStatus(getPara("payment_status"));
		try {
			List<OrderMobileResultDto> orderListResultDtos = orderService.list4Refunds(orderParamDto);
			rendSuccessJson(orderListResultDtos);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	/**撤销退款*/
	public void cancelRefund(){
		Long orderId = getParaToLong("orderId");
		Long buyerId = getBuyerId();
		try{
			orderGroupService.cancelRefunds(orderId,buyerId);
			rendSuccessJson();
		}catch (OrderException e){
			rendFailedJson(e.getMessage());
		}
	}
	/**发起退款申请*/
	public void applyRefund(){
		try {
			orderService.applyRefund(getBuyerId(), getParaToLong("orderId"));
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void list(){
		OrderListParamDto orderParamDto = new OrderListParamDto(getAuthUserId(), getBuyerId(), getPageNo());
		orderParamDto.setOrderStatus(getPara("order_status"));
		orderParamDto.setPaymentStatus(getPara("payment_status"));
		orderParamDto.setShippingStatus(getPara("shipping_status"));
		try {
			List<OrderMobileResultDto> orderListResultDtos = orderService.list4Mobile(orderParamDto);
			rendSuccessJson(orderListResultDtos);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	//创建订单
	public void create(){
		final String memo = getPara("memo");
		final String items = getPara("items");
		final Long shopId = getParaToLong("shopId");
		OrderCreateParamDto orderParamDto = new OrderCreateParamDto(getBuyerId(),  getAuthUserId(), getParaToLong("receiverId"), items, getPara("formid"));
		orderParamDto.setMemo(memo);	//买家留言
		orderParamDto.setShopId(shopId);
		try {
			rendSuccessJson(orderService.create(orderParamDto));
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 创建拼团订单
	 */
	public void gcreate(){
		final String memo = getPara("memo");
		final String items = getPara("items");
		final Long shopId = getParaToLong("shopId");
		OrderCreateParamDto orderParamDto = new OrderCreateParamDto(getBuyerId(), getAuthUserId(), getParaToLong("receiverId"), items, getPara("formid"));
		orderParamDto.setMemo(memo);	//买家留言
		orderParamDto.setShopId(shopId);
		try {
			rendSuccessJson(orderGroupService.gcreate(orderParamDto));
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 加入拼团
	 */
	public void joinGroup(){
		final String memo = getPara("memo");
		final String items = getPara("items");
		final Long groupId = getParaToLong("groupId");//组团发起者
		final Long shopId = getParaToLong("shopId");
		final OrderJoinParamDto orderJoinParam = new OrderJoinParamDto(groupId, getBuyerId(), getAuthUserId(), getParaToLong("receiverId"), items, getPara("formid"));
		orderJoinParam.setMemo(memo);
		orderJoinParam.setShopId(shopId);
		try {
			Long orderId = orderGroupService.joinGroup(orderJoinParam);
			rendSuccessJson(orderId);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void cancel(){
		try {
			orderService.cancel(getParaToLong("orderId"));
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 买家确认收货
	 */
	public void queren(){
		try {
			orderService.confirm(getParaToLong("orderId"));
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 订单详情
	 */
	public void detail(){
		OrderResultDto orderDetail = orderService.getOrder(getParaToLong("orderId"));
		setAttr("orderDetail", orderDetail);
		render("/order/detail.html");
	}
	
	public void getPostFee(){
		final String items = getPara("items");
		final Long receiverId = getParaToLong("receiverId");
		try {
			OrderResultDto orderDto = orderService.balance(getBuyerId(), receiverId, items);
			rendSuccessJson(orderDto);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 去评价
	 */
	public void toReview(){
		Long orderId = getParaToLong("orderId");
		OrderResultDto dto=orderService.getOrder(orderId);
		setAttr("orderDto", dto);
		render("/order/review.html");
	}
	
	/**
	 * 保存评价
	 */
	public void saveReview(){
		Long orderId = getParaToLong("orderId");
		Long productId = getParaToLong("productId");
		String content=getPara("content");
		Integer score=getParaToInt("score");
		ProductReview review=new ProductReview();
		review.setAppId(getAuthUserId());
		review.setOrderId(orderId);
		review.setProductId(productId);
		review.setContent(content);
		review.setScore(score);
		review.setBuyerId(getBuyerId());
		review.setActive(true);
		review.setCreated(new Date());
		review.setUpdated(new Date());
		review.setIsShow(true);
		try {
			productReviewService.save(review);
			rendSuccessJson("评论成功");
		} catch (Exception e) {
			rendFailedJson("评论失败，请重新评论");
		}
	}
}
