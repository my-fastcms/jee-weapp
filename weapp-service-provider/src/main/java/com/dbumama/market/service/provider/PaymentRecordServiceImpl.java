package com.dbumama.market.service.provider;

import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.pay.CompanyTransfersApi;
import com.dbumama.weixin.pay.CompanyTransfersReqData;
import com.dbumama.weixin.pay.CompanyTransfersResData;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
@RPCBean
public class PaymentRecordServiceImpl extends WxmServiceBase<PaymentRecord> implements PaymentRecordService {
	
	@Inject
	private PayeeService payeeService;
	@Inject
	private AuthCertService authCertService;
	@Inject
	private AuthUserService authUserService;
	@Inject
	private SellerUserService sellerUserService;
	@Inject
	private SelleruserBalanceRcdService selleruserBalanceRcdService;
	@Inject
	private PhoneCodeService phoneCodeService;

	@Override
	public void pay(Long appId,Long payee_id, String explain, String payment_money,String payee_name, String phone_code, String phone) {
		
		if(payee_id == null) throw new WxmallMsgBaseException("payee_id is null");
		if(phone == null) throw new WxmallMsgBaseException("phone is null");
		if(StrKit.isBlank(phone_code)) throw new WxmallMsgBaseException("phone_code is null");
		if(appId == null) throw new WxmallMsgBaseException("appId is null");
		if(StrKit.isBlank(explain)) throw new WxmallMsgBaseException("explain is null");
		if(StrKit.isBlank(payment_money)) throw new WxmallMsgBaseException("redpackMax2 is null");
		if(new BigDecimal(payment_money).floatValue() > new BigDecimal(5000).floatValue() ){
			throw new WxmallMsgBaseException("付款金额不允许超过5000元");
		}
		if(StrKit.isBlank(payment_money)) throw new WxmallMsgBaseException("redpackMin is null");
		if(new BigDecimal(payment_money).floatValue() < new BigDecimal(1).floatValue() ){
			throw new WxmallMsgBaseException("付款金额不允许少于1元");
		}
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, phone_code);
		if(userCode == null){
			throw new WxmallMsgBaseException("手机验证码错误");
		}
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()){
			throw new WxmallMsgBaseException("验证码已过期");
		}
		
		Payee payee = payeeService.findById(payee_id);
		if(payee == null) throw new WxmallMsgBaseException("payee is null");
		if(StrKit.isBlank(payee_name)) throw new WxmallMsgBaseException("payee_name is null");
		
		payee.setPayeeName(payee_name);
		payeeService.update(payee);
		
		//付款金额数（单位分）
		BigDecimal money = new BigDecimal(payment_money).multiply(new BigDecimal(100));
		
		//公众号
		AuthUser authUser = authUserService.findById(appId);
		if(authUser == null) throw new WxmallMsgBaseException("authUser is null");
		//支付信息
		AuthCert cert = authCertService.findUse(authUser.getAppId());

		if(cert == null || cert.getPayMchId() == null || cert.getPaySecretKey() ==null || cert.getCertFile() == null){
			throw new WxmallMsgBaseException("公众号支付配置设置不全");
		}
		
		CompanyTransfersReqData reqData = new CompanyTransfersReqData(
				cert.getAppId(), 
				cert.getPayMchId(),
				cert.getPaySecretKey(), 
				cert.getPayMchId() + DateTimeUtil.getDateTime8String() + String.valueOf(System.currentTimeMillis()).substring(3, 13),
				payee.getOpenId(), 
				"FORCE_CHECK", 
				payee.getPayeeName(), 
				money.toString(), 
				explain, 
				"127.0.0.1");
		CompanyTransfersApi transfersApi = new CompanyTransfersApi();
		
		PaymentRecord record = new PaymentRecord();
		record.setAppId(appId).setCertId(cert.getId()).setPayeeId(payee.getId())
		.setPaymentMoney(money.intValue()).setCreated(new Date()).setActive(true)
		.setUpdated(new Date()).setExplain(explain);
		
		try {
			CompanyTransfersResData resData = (CompanyTransfersResData) transfersApi.post(reqData,cert.getCertFile());

			if("SUCCESS".equals(resData.getResult_code())){
				//更新付款成功记录
				record.setStatus(true);
				System.out.println(resData.getPayment_no() + "time:" + resData.getPayment_time());
			}else{
				//更新付款失败记录
				record.setStatus(false).setFailReason(resData.getErr_code_des());
				System.out.println(resData.getErr_code_des() + "returnMsg:" + resData.getReturn_msg());
			}
			save(record);
		} catch (Exception e) {
			//更新付款失败记录
			record.setStatus(false).setFailReason("支付配置有误");
			save(record);
			throw new WxmallMsgBaseException("支付配置有误");
		}
	}

	@Override
	public Page<PaymentRecordResDto> list(Long appId, String payee_name, Integer status, int pageNo, int pageSize, Date start_date,
			Date end_date) {
		if(appId == null) throw new WxmallMsgBaseException("appId is null");
		
		String select = " SELECT r.*,p.payee_name,b.headimgurl,b.nickname ";
		String sqlExceptSelect = " FROM "+PaymentRecord.table + " r LEFT JOIN "+Payee.table+" p ON r.payee_id = p.id "
				+"LEFT JOIN "+BuyerUser.table+" b ON p.open_id = b.open_id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("r.app_id", appId);
		helper.addWhere("r.status", status);
		helper.addWhereLike("p.payee_name", payee_name);
		helper.addWhereTHEN_GE("r.created", start_date);
		helper.addWhereTHEN_LE("r.created", end_date);
		helper.addOrderBy("desc", "r.created");
		helper.build();
		
		Page<Record> records = Db.paginate(pageNo, pageSize, select, helper.getSqlExceptSelect(), helper.getParams());
		
		List<PaymentRecordResDto> awardStatResDtos = new ArrayList<PaymentRecordResDto>();
		
		for(Record record : records.getList()){
			PaymentRecordResDto statResDto = new PaymentRecordResDto();
			
			statResDto.setId(record.getLong("id"));
			statResDto.setNick(record.getStr("nickname"));
			statResDto.setHeadimg(record.getStr("headimgurl"));
			statResDto.setCreated(record.getDate("created"));
			statResDto.setExplain(record.getStr("explain"));
			statResDto.setFailReason(record.getStr("fail_reason"));
			statResDto.setPayeeName(record.getStr("payee_name"));
			statResDto.setPayment_money(new BigDecimal(record.getInt("payment_money")).divide(new BigDecimal(100)).toString());
			
			Boolean pStatus = record.getBoolean("status");
			
			if(pStatus){
				statResDto.setStatus("付款成功");
			}else{
				statResDto.setStatus("付款失败");
			}
			
			awardStatResDtos.add(statResDto);
		}
		
		return new Page<PaymentRecordResDto>(awardStatResDtos, pageNo, pageSize, records.getTotalPage(), records.getTotalRow());
	}


}