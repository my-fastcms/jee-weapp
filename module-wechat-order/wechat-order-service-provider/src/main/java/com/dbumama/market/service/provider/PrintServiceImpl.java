package com.dbumama.market.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.ExpressTemplate;
import com.dbumama.market.model.InvoiceTemplate;
import com.dbumama.market.model.Order;
import com.dbumama.market.model.SellerAddr;
import com.dbumama.market.service.api.InvoiceTemplateService;
import com.dbumama.market.service.api.OrderException;
import com.dbumama.market.service.api.OrderResultDto;
import com.dbumama.market.service.api.OrderService;
import com.dbumama.market.service.api.PrintExpItemResultDto;
import com.dbumama.market.service.api.PrintExpResultDto;
import com.dbumama.market.service.api.PrintInvItemResultDto;
import com.dbumama.market.service.api.PrintInvResultDto;
import com.dbumama.market.service.api.PrintParamDto;
import com.dbumama.market.service.api.PrintService;
import com.dbumama.market.service.api.SellerAddrService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Bean
@RPCBean
public class PrintServiceImpl implements PrintService{

	@Inject
	private OrderService orderService;
	@Inject
	private SellerAddrService sendAddrService;
	@Inject
	private InvoiceTemplateService invoiceTemplateService;
	
	public static final AuthUser authUserDao = new AuthUser().dao();
	private static final ExpressTemplate expressTpldao = new ExpressTemplate().dao();
	
	@Override
	public PrintExpResultDto getExpressPrintData(PrintParamDto printParamDto) throws OrderException {
		if(printParamDto == null || StrKit.isBlank(printParamDto.getOrderIds()) 
				|| StrKit.isBlank(printParamDto.getExpressKey())
				|| printParamDto.getAuthUserId() == null)
			throw new OrderException("调用获取打印快递单数据接口参数错误");
		
		ExpressTemplate template = expressTpldao.findFirst("select * from " + ExpressTemplate.table + " where "
				+ " app_id=? and exp_key = ? ", printParamDto.getAuthUserId(), printParamDto.getExpressKey());
		if(template == null) throw new OrderException("快递模板不存在");
		
		//查询发货设置信息
		SellerAddr sendAddr = sendAddrService.getSendAddr(printParamDto.getAuthUserId());
		if(sendAddr == null) throw new OrderException("发货信息为空，请到店铺模块中设置");
		
		//查询订单数据
		List<OrderResultDto> orderDetailList = orderService.getOrders(printParamDto.getOrderIds());
		
		PrintExpResultDto orderPrintResultDto = new PrintExpResultDto();
		orderPrintResultDto.setExpressTemplate(template);
		List<PrintExpItemResultDto> printItems = new ArrayList<PrintExpItemResultDto>();
		orderPrintResultDto.setPrintData(printItems);
		
		for(OrderResultDto orderDto : orderDetailList){
			PrintExpItemResultDto printItemDto = new PrintExpItemResultDto();
			//卖家发货信息
			printItemDto.setContact_name(sendAddr.getContactName());
			printItemDto.setCity(sendAddr.getCity());
			printItemDto.setCountry(sendAddr.getCountry());
			printItemDto.setProvince(sendAddr.getProvince());
			printItemDto.setAddr(sendAddr.getAddr());
			printItemDto.setMemo(sendAddr.getMemo());
			printItemDto.setPhone(sendAddr.getPhone());
			printItemDto.setSeller_company(sendAddr.getSellerCompany());
			printItemDto.setZipCode(sendAddr.getZipCode());
			
			//买家订单信息
			printItemDto.setOrderId(orderDto.getOrderId());
			printItemDto.setReceiverName(orderDto.getReceiverName());
			printItemDto.setReceiverPhone(orderDto.getReceiverPhone());
			printItemDto.setReceiverProvince(orderDto.getReceiverProvince());
			printItemDto.setReceiverCity(orderDto.getReceiverCity());
			printItemDto.setReceiverCountry(orderDto.getReceiverCountry());
			printItemDto.setReceiverAddr(orderDto.getReceiverAddr());
			printItemDto.setZipCode(orderDto.getZipCode());
			printItemDto.setBuyerNick(orderDto.getBuyerNick());
			printItemDto.setBuyerMemo(orderDto.getBuyerMemo());
			printItemDto.setTotalPrice(orderDto.getTotalPrice());
			printItemDto.setPostFee(orderDto.getPostFee());
			printItemDto.setPayFee(orderDto.getPayFee());
			printItemDto.setOrderSn(orderDto.getOrderSn());
			printItemDto.setOrderCreated(orderDto.getOrderCreated());
			printItemDto.setOrderStatus(orderDto.getOrderStatus());
			printItemDto.setOrderItems(orderDto.getOrderItems());
			printItems.add(printItemDto);
		}
		
		return orderPrintResultDto;
	}

	@Override
	public PrintInvResultDto getInvoicePrintData(PrintParamDto printParamDto) throws OrderException {
		if(printParamDto == null || StrKit.isBlank(printParamDto.getOrderIds()) || printParamDto.getAuthUserId() == null)
			throw new OrderException("调用获取打印快递单数据接口参数错误");
		
		InvoiceTemplate invTpl = invoiceTemplateService.getUserTpl(printParamDto.getAuthUserId());
		if(invTpl == null) throw new OrderException("发货模板不存在");
		
		//查询发货设置信息
		SellerAddr sendAddr = sendAddrService.getSendAddr(printParamDto.getAuthUserId());
		if(sendAddr == null) throw new OrderException("发货信息为空，请到店铺模块中设置");
		
		//获取正在使用的公众号信息
		AuthUser authUser = authUserDao.findById(printParamDto.getAuthUserId());
		if(authUser == null) throw new OrderException("授权公众号不存在");
		String qrcodeBase64 = getImgBase64(authUser.getQrcodeUrl());
		authUser.setQrcodeUrl(qrcodeBase64);
		
		List<OrderResultDto> orderDetailList = orderService.getOrders(printParamDto.getOrderIds());
		
		PrintInvResultDto orderPrintInvResultDto = new PrintInvResultDto();
		orderPrintInvResultDto.setInvoiceTemplate(invTpl);
		List<PrintInvItemResultDto> printItems = new ArrayList<PrintInvItemResultDto>();
		orderPrintInvResultDto.setPrintData(printItems);
		
		for(OrderResultDto orderDto : orderDetailList){
			PrintInvItemResultDto printItemDto = new PrintInvItemResultDto();
			//卖家发货信息
			printItemDto.setContact_name(sendAddr.getContactName());
			printItemDto.setCity(sendAddr.getCity());
			printItemDto.setCountry(sendAddr.getCountry());
			printItemDto.setProvince(sendAddr.getProvince());
			printItemDto.setAddr(sendAddr.getAddr());
			printItemDto.setMemo(sendAddr.getMemo());
			printItemDto.setPhone(sendAddr.getPhone());
			printItemDto.setSeller_company(sendAddr.getSellerCompany());
			printItemDto.setZipCode(sendAddr.getZipCode());
			
			//小程序信息
			printItemDto.setNick_name(authUser.getNickName());
			printItemDto.setHead_img(authUser.getHeadImg());
			printItemDto.setPrincipal_name(authUser.getPrincipalName());
			printItemDto.setQrcode_url(authUser.getQrcodeUrl());
			
			//买家订单信息
			printItemDto.setOrderId(orderDto.getOrderId());
			printItemDto.setReceiverName(orderDto.getReceiverName());
			printItemDto.setReceiverPhone(orderDto.getReceiverPhone());
			printItemDto.setReceiverProvince(orderDto.getReceiverProvince());
			printItemDto.setReceiverCity(orderDto.getReceiverCity());
			printItemDto.setReceiverCountry(orderDto.getReceiverCountry());
			printItemDto.setReceiverAddr(orderDto.getReceiverAddr());
			printItemDto.setZipCode(orderDto.getZipCode());
			printItemDto.setBuyerNick(orderDto.getBuyerNick());
			printItemDto.setBuyerMemo(orderDto.getBuyerMemo());
			printItemDto.setTotalPrice(orderDto.getTotalPrice());
			printItemDto.setPostFee(orderDto.getPostFee());
			printItemDto.setPayFee(orderDto.getPayFee());
			printItemDto.setOrderSn(orderDto.getOrderSn());
			printItemDto.setOrderCreated(orderDto.getOrderCreated());
			printItemDto.setOrderStatus(orderDto.getOrderStatus());
			printItemDto.setOrderItems(orderDto.getOrderItems());
			printItems.add(printItemDto);
		}
		
		return orderPrintInvResultDto;
	}
	
	private String getImgBase64(final String url){
		String result = "";
		//获取公众号二维码的base64图片数据
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().
                url(url)
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116Safari/537.36")
                .build();
		try {
			Response okResponse = okHttpClient.newCall(request).execute();
			Base64 base64 = new Base64();
            result = "data:image/jpg;base64," + base64.encodeAsString(okResponse.body().bytes());
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.PrintService#getExpressPrintData4Group(com.dbumama.market.service.api.PrintParamDto)
	 */
	@Override
	public PrintExpResultDto getExpressPrintData4Group(PrintParamDto printParamDto) throws OrderException {
		
		String groupIds [] = printParamDto.getOrderIds().split("-");
		
		StringBuffer sbuff = new StringBuffer();
		
		for(String groupId : groupIds){
			List<Order> orders = orderService.getOrdersByGroup(Long.valueOf(groupId));
			for(Order order : orders){
				sbuff.append(order.getId()).append("-");
			}
		}
		
		printParamDto.setOrderIds(sbuff.toString());
		
		return getExpressPrintData(printParamDto);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.PrintService#getInvoicePrintData4Group(com.dbumama.market.service.api.PrintParamDto)
	 */
	@Override
	public PrintInvResultDto getInvoicePrintData4Group(PrintParamDto printParamDto) throws OrderException {
		String groupIds [] = printParamDto.getOrderIds().split("-");
		
		StringBuffer sbuff = new StringBuffer();
		
		for(String groupId : groupIds){
			List<Order> orders = orderService.getOrdersByGroup(Long.valueOf(groupId));
			for(Order order : orders){
				sbuff.append(order.getId()).append("-");
			}
		}
		
		printParamDto.setOrderIds(sbuff.toString());
		return getInvoicePrintData(printParamDto);
	}

}
