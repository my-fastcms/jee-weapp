package com.dbumama.market.notify.sms;

import io.jboot.Jboot;

public final class SendSmsMessageUtil {

	private SendSmsMessageUtil() {}

	public static boolean sendCheckCodeSMS(final String phone, final String checkcode) {
		WxmSmsConfig smsConfig = Jboot.config(WxmSmsConfig.class);
		SmsMessage sms = new SmsMessage();
		sms.setContent("test");
		sms.setRec_num(phone);
		sms.setTemplate(smsConfig.getSmsTemplateCode());
		sms.setParam("{\"code\":\"" + checkcode + "\",\"product\":\"" + smsConfig.getSmsProduct() + "\"}");
		sms.setSign_name(smsConfig.getSmsSignName());
		sms.setCode(checkcode);
		return SmsSenderFactory.createSender().send(sms);
	}

}
