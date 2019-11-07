package com.dbumama.market.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.ProductReview;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@RequestMapping(value = "order")
public class OrderController extends BaseApiController{
	
	@RPCInject
	private OrderService orderService;
	@RPCInject
	private ProductReviewService productReviewService;
	@RPCInject
	private OrderGroupService orderGroupService;

	//创建订单
	@Before(ApiSessionInterceptor.class)
	public void create(){
		final String memo = getJSONPara("memo");
		final String items = getJSONPara("items");
		final Long shopId = getJSONParaToLong("shopId");
		final Long cityId = getJSONParaToLong("cityId");
		OrderCreateParamDto orderParamDto = new OrderCreateParamDto(getBuyerId(), getAuthUserId(), getJSONParaToLong("receiverId"), items, getJSONPara("formid"));
		orderParamDto.setMemo(memo);	//买家留言
		if(shopId != null && cityId ==null){
			orderParamDto.setShopId(shopId);
		}else{
			orderParamDto.setShopId(cityId);
		}
		try {
			rendSuccessJson(orderService.create(orderParamDto));
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**退款订单列表*/
	@Before(ApiSessionInterceptor.class)
	public void refunds(){
		OrderListParamDto orderParamDto = new OrderListParamDto(getAuthUserId(), getBuyerId(), getPageNo());
		orderParamDto.setPaymentStatus(getJSONPara("payment_status"));
		try {
			List<OrderMobileResultDto> orderListResultDtos = orderService.list4Refunds(orderParamDto);
			rendSuccessJson(orderListResultDtos);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**撤销退款*/
	public void cancelRefund(){
		Long orderId = getJSONParaToLong("orderId");
		Long buyerId = getBuyerId();
		try{
			orderGroupService.cancelRefunds(orderId,buyerId);
			rendSuccessJson();
		}catch (OrderException e){
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 创建拼团订单
	 */
	@Before(ApiSessionInterceptor.class)
	public void gcreate(){
		final String memo = getJSONPara("memo");
		final String items = getJSONPara("items");
		final Long shopId = getJSONParaToLong("shopId");
		OrderCreateParamDto orderParamDto = new OrderCreateParamDto(getBuyerId(), getAuthUserId(), getJSONParaToLong("receiverId"), items, getJSONPara("formid"));
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
	@Before(ApiSessionInterceptor.class)
	public void joinGroup(){
		final String memo = getJSONPara("memo");
		final String items = getJSONPara("items");
		final Long groupId = getJSONParaToLong("groupid");//组团发起者
		final Long shopId = getJSONParaToLong("shopId");
		final OrderJoinParamDto orderJoinParam = new OrderJoinParamDto(groupId, getBuyerId(), getAuthUserId(), getJSONParaToLong("receiverId"), items, getJSONPara("formid"));
		orderJoinParam.setMemo(memo);
		orderJoinParam.setShopId(shopId);
		try {
			Long orderId = orderGroupService.joinGroup(orderJoinParam);
			rendSuccessJson(orderId);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	@Before(ApiSessionInterceptor.class)
	public void list(){
		OrderListParamDto orderParamDto = new OrderListParamDto(getAuthUserId(), getBuyerId(), getPageNo());
		orderParamDto.setOrderStatus(getJSONPara("order_status"));
		orderParamDto.setPaymentStatus(getJSONPara("payment_status"));
		orderParamDto.setShippingStatus(getJSONPara("shipping_status"));
		orderParamDto.setGroupStatus(getJSONPara("group_status"));
		orderParamDto.setOrderType(getJSONPara("order_type"));//拼团订单传2
		try {
			List<OrderMobileResultDto> orderListResultDtos = orderService.list4Mobile(orderParamDto);
			rendSuccessJson(orderListResultDtos);
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 订单详情
	 */
	@Before(ApiSessionInterceptor.class)
	public void detail(){
		try{
		    OrderResultDto orderDetail = orderService.getOrder(getJSONParaToLong("orderId"));
		    rendSuccessJson(orderDetail);
		}catch(Exception ex){
			rendFailedJson(ex.getMessage());
		}
	}
	
	@Before({POST.class, ApiSessionInterceptor.class})
	public void confirm(){//确认收货，完成订单
		try {
			orderService.confirm(getJSONParaToLong("orderId"));
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	@Before({POST.class, ApiSessionInterceptor.class})
	public void wantRefund(){
		try {
			orderService.applyRefund(getBuyerId(), getJSONParaToLong("orderId"));
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/**
	 * 去评价
	 */
	@Before(ApiSessionInterceptor.class)
	public void getOrderInfo(){
		try{
			JSONObject res = getJSONRespones();
			OrderResultDto dto=orderService.getOrder(getJSONParaToLong("orderId"));
			List<ProductReview> reviews = productReviewService.getReviewsByBuyer(getBuyerId(), getJSONParaToLong("orderId"), getJSONParaToLong("productId"));
			
			res.put("orderDto", dto);
			res.put("reviews", reviews);
			rendSuccessJson(res);
		}catch(Exception ex){
			rendFailedJson(ex.getMessage());
		}
	}
	
	/**
	 * 保存评价
	 */
	@Before({POST.class, ApiSessionInterceptor.class})
	public void saveReview(){
		final Long orderId = getJSONParaToLong("orderId");
		final Long productId = getJSONParaToLong("productId");
		final String content = getJSONPara("content");
		final Integer score = getJSONParaToInteger("score");
		
		if(orderId == null){
			rendFailedJson("orderId is null");
			return;
		}
		
		if(productId == null){
			rendFailedJson("productId is null");
			return;
		}

		if(score == null){
			rendFailedJson("score is null");
			return;
		}
		
		if(StrKit.isBlank(content)){
			rendFailedJson("请输入评价内容");
			return;
		}
		
		ProductReview review = new ProductReview();
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
