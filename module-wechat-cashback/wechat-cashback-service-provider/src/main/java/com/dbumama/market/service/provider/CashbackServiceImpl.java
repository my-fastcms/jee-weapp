package com.dbumama.market.service.provider;

import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.ActivityStatus;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.pay.SendredpackApi;
import com.dbumama.weixin.pay.SendredpackReqData;
import com.dbumama.weixin.pay.SendredpackResData;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Bean
@RPCBean
public class CashbackServiceImpl extends WxmServiceBase<Cashback> implements CashbackService {
	@Inject
	private AuthCertService authCertService;
	
	public static final AuthUser authUserDao = new AuthUser().dao();
	private static final Cashback cashBackDao = new Cashback().dao();
	private static final CashbackProduct cashbackProddao = new CashbackProduct().dao();
	private static final Product productDao = new Product().dao();
	
	@Override
	@Before(Tx.class)
	public void save(Cashback cashback, String products, Long authUserId) throws UmpException{
		if(cashback == null || authUserId == null || StrKit.isBlank(products))
			throw new UmpException("保存订单返现出错:参数不全");
		
		if(cashback.getStartTime().before(new Date()) && cashback.getEndTime().after(new Date())){
			cashback.setStatus(ActivityStatus.activing.ordinal());//进行中
		}else if(cashback.getStartTime().after(new Date())){
			cashback.setStatus(ActivityStatus.unactive.ordinal());
		}else if(cashback.getEndTime().before(new Date())){
			cashback.setStatus(ActivityStatus.activeend.ordinal());
		}
		
		if(cashback.getId() == null){
			cashback.setAppId(authUserId);
			cashback.setActive(true);
			cashback.save();
			String productIds[]=products.split(",");
			for (int i = 0; i < productIds.length; i++) {
				CashbackProduct cashbackSet=new CashbackProduct();
				cashbackSet.setCashbackId(cashback.getId());
				cashbackSet.setProductId(new Long(productIds[i]));
				try {
					cashbackSet.save();
				} catch (Exception e) {
					throw new UmpException(e.getMessage());
				}
			}
		}else{
			cashback.update();
			try {
				Db.deleteById(CashbackProduct.table,"cashback_id",cashback.getId());
			} catch (Exception e) {
				throw new UmpException(e.getMessage());
			}
			String productIds[]=products.split(",");
			for (int i = 0; i < productIds.length; i++) {
				CashbackProduct cashbackSet=new CashbackProduct();
				cashbackSet.setCashbackId(cashback.getId());
				cashbackSet.setProductId(new Long(productIds[i]));
				try {
					cashbackSet.save();
				} catch (Exception e) {
					throw new UmpException(e.getMessage());
				}
			}
		}
	}

	@Override
	public Page<CashbackResultDto> list(CashbackParamDto cashbackParamDto) throws UmpException {
		if(cashbackParamDto == null || cashbackParamDto.getAuthUserId() == null)
			throw new UmpException("获取订单返现列表数据参数错误");
		
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", cashbackParamDto.getAuthUserId()));
		columns.add(Column.create("active", cashbackParamDto.getActive()));
		columns.add(Column.create("status", cashbackParamDto.getStatus()));
		
		Page<Cashback> pages = cashBackDao.paginateByColumns(cashbackParamDto.getPageNo(), cashbackParamDto.getPageSize(),
				columns, " updated desc ");
		List<CashbackResultDto> cashbackDtos = new ArrayList<CashbackResultDto>();
		
		for (Cashback cashback : pages.getList()) {
			CashbackResultDto dto=new CashbackResultDto();
			dto.setId(cashback.getId());
			dto.setName(cashback.getName());
			dto.setStartDate(cashback.getStartTime());
			dto.setEndDate(cashback.getEndTime());
			dto.setCashbackStart(cashback.getCashbackStart().toString());
			if(cashback.getCashbackMethod()==0){
				dto.setCashbackMethod("随机返现");
				dto.setCashbackStart(cashback.getCashbackStart().toString()+"% 至  "+cashback.getCashbackEnd().toString()+"%");
			}else{
				dto.setCashbackMethod("固定返现");
				dto.setCashbackStart(cashback.getCashbackStart().toString()+"%");
			}
			dto.setStatus(cashback.getStatus());
			dto.setCashbackLimit(cashback.getCashbackLimit());
			dto.setActive(cashback.getActive() == true ? 1 : 0);
			cashbackDtos.add(dto);
		}
		return  new Page<CashbackResultDto> (cashbackDtos, cashbackParamDto.getPageNo(), cashbackParamDto.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public Page<ProductResultDto> getProducts4CashbackPage(ProductParamDto productParamDto) throws ProductException {
		if(productParamDto == null || productParamDto.getAuthUserId() == null) throw new ProductException("获取未打折商品列表出错");
		List<ProductResultDto> resultDtos = new ArrayList<ProductResultDto>();
		
		//查询出有效打折数据
		Date currDate = new Date();
		final String select = "select * ";
		final String sqlExceptSelect = " from "+Product.table+" where app_id=? and is_marketable=1 and active=1 and id not in"
				+ " (select product_id from " + CashbackProduct.table + " where cashback_id "
				+ "in (select id from "+Cashback.table+" where app_id=? and start_time<=? and end_time >=? ))";
		Page<Product> pages = productDao.paginate(productParamDto.getPageNo(), productParamDto.getPageSize(),
				select, sqlExceptSelect, productParamDto.getAuthUserId(), productParamDto.getAuthUserId(), currDate, currDate);
		for(Product product : pages.getList()){
			ProductResultDto resultDto = new ProductResultDto();
			resultDto.setId(product.getId());
			resultDto.setImg(getImageDomain() + product.getImage());
			resultDto.setName(product.getName());
			resultDto.setPrice(product.getPrice());
			resultDto.setStock(product.getStock());
			resultDtos.add(resultDto);
		}
	
		return new Page<ProductResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public Cashback getProductCashSet(ProductParamDto productParamDto) throws ProductException {
		if(productParamDto == null || productParamDto.getProductId() == null)
			throw new ProductException("获取商品订单返现接口缺少必要参数");
		
		List<CashbackProduct> cashProducts = cashbackProddao.find("select * from " + CashbackProduct.table + " where product_id=? ", productParamDto.getProductId());
		for(CashbackProduct cp : cashProducts){
			Cashback cashback = cashBackDao.findById(cp.getCashbackId());
			if(cashback.getStartTime().before(new Date()) && cashback.getEndTime().after(new Date()))
				return cashback;
		}
		return null;
	}

	@Override
	public ProdCashbackResultDto getProductCashBack(Product product) throws ProductException {
		if(product == null) throw new ProductException("没有商品");

		//商品价格
		String price = product.getPrice();
//		//是否是多规格商品，是的话，取商品价格最大值
		if(price.contains(" ~ ")){
			price = price.split(" ~ ")[1];
		}
		
		ProductParamDto productParam = new ProductParamDto(product.getAppId(), product.getId());
		Cashback cashback = getProductCashSet(productParam);
		if(cashback != null){
			ProdCashbackResultDto cashbackDto = new ProdCashbackResultDto();
			cashbackDto.setCashbackName(cashback.getName());
			cashbackDto.setLimit(cashback.getCashbackLimit());
			cashbackDto.setCashbackMethod(cashback.getCashbackMethod());
			cashbackDto.setPercentStart(cashback.getCashbackStart());
			cashbackDto.setPercentEnd(cashback.getCashbackEnd());
			if(cashback.getCashbackMethod() == 1){
				//固定返现
				cashbackDto.setMaxCash(cashback.getCashbackStart().multiply(new BigDecimal(price)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
				cashbackDto.setTag("支付返"+cashbackDto.getMaxCash()+"元现金红包,限前" + cashback.getCashbackLimit() + "笔订单");
			}else{
				//随机返现
				cashbackDto.setMaxCash(cashback.getCashbackEnd().multiply(new BigDecimal(price)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
				cashbackDto.setTag("支付随机返现,最高返"+cashbackDto.getMaxCash()+"元现金红包,限前" + cashback.getCashbackLimit() + "笔订单");
			}
			if(cashback.getStartTime().after(new Date())){
				cashbackDto.setBackTime("还差" + DateTimeUtil.compareDay(cashback.getStartTime(), new Date()) + "天开始");
			}else {
				cashbackDto.setBackTime("剩余" + DateTimeUtil.compareDay(cashback.getEndTime(), new Date()) + "天结束");
			}
			return cashbackDto;
		}
		return null;
	}

	@Override
	public BigDecimal cash2Buyer(BuyerUser buyerUser, Order order, List<OrderItem> orderItems, Product product, ProdCashbackResultDto cashback)
			throws UmpException {

		AuthUser authUser = authUserDao.findById(buyerUser.getAppId());
		if(authUser == null){
			throw new UmpException("授权公众号/小程序不存在或已取消授权");
		}
		
		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			use = authCertService.findDefault();
		}
		if(use == null || use.getPayMchId() == null || use.getPaySecretKey() ==null || use.getCertFile() == null){
			throw new UmpException("公众号支付配置设置不全");
		}

		BigDecimal price = null;
		for(OrderItem orderItem : orderItems){
			//找出该商品价格
			if(orderItem.getProductId() == product.getId()){
				price = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));//该商品在订单中的支付金额
			}
		}
		if(price == null) throw new UmpException("返现订单商品金额异常，返现失败");
		
		BigDecimal cashbackFee = null;
		//计算红包金额
		if(cashback.getCashbackMethod() == 1){
			//固定返现
			cashbackFee = cashback.getPercentStart().divide(new BigDecimal(100)).multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
		}else{
			//随机返现
			Random rand = new Random();
			int randNumber = rand.nextInt(cashback.getPercentEnd().intValue() - cashback.getPercentStart().intValue() + 1) + cashback.getPercentStart().intValue();
			if(randNumber <=0 ) throw new UmpException("随机返现区间计算错误，返现失败");
			cashbackFee = new BigDecimal(randNumber).divide(new BigDecimal(100)).multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		if(cashbackFee == null) throw new UmpException("返现金额计算错误，返现失败");
		
		cashbackFee = cashbackFee.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		SendredpackReqData sendredpackReqData = new SendredpackReqData(use.getPayMchId(),
				use.getPaySecretKey(),
				use.getPayMchId() + DateTimeUtil.getDateTime8String() + String.valueOf(System.currentTimeMillis()).substring(3, 13), 
				authUser.getAppId(), 
				authUser.getNickName(), buyerUser.getOpenId(), cashbackFee.toString(), "1", cashback.getTag(), 
				"127.0.0.1", cashback.getCashbackName(), authUser.getPrincipalName());
		
		try {
			SendredpackApi sendredpackApi = new SendredpackApi();
			SendredpackResData sendredpackResData = (SendredpackResData) sendredpackApi.post(sendredpackReqData, use.getCertFile());
			if(!"SUCCESS".equals(sendredpackResData.getResult_code())){
				//记录红包发送失败的错误记录
				throw new UmpException(sendredpackResData.getErr_code_des());
			}
			CashbackRcd cashbackRcd = new CashbackRcd();
			cashbackRcd.setOrderId(order.getId());
			cashbackRcd.setProductId(product.getId());
			cashbackRcd.setBuyerId(buyerUser.getId());
			cashbackRcd.setCashBackFee(cashbackFee);
			cashbackRcd.setSendLog("红包发放成功");//发送成功
			cashbackRcd.setActive(true);
			cashbackRcd.setCreated(new Date());
			cashbackRcd.setUpdated(new Date());
			cashbackRcd.save();
			return cashbackFee;
		} catch (Exception e) {
			e.printStackTrace();
			//记录红包发送失败的错误记录
			throw new UmpException(e.getMessage());
		}
	}

	@Override
	public List<CashbackProduct> getCashbackProducts(Long cashbackId) {
		return cashbackProddao.find("select * from " + CashbackProduct.table + " where cashback_id=?", cashbackId);
	}

}