package com.dbumama.market.notify.sms;

import io.jboot.Jboot;
import org.apache.commons.lang3.StringUtils;

public class SmsSenderFactory {
	
	static final WxmSmsConfig smsConfig = Jboot.config(WxmSmsConfig.class);

	public static ISmsSender createSender() {
		
		String provider = smsConfig.getSmsAppProvider();
		
		if(StringUtils.isBlank(provider)){
			return new AlidayuSmsSender();
		}
		
		else if("sms_provider_alidayu".equals(provider)){
			return new AlidayuSmsSender();
		}
		
		else if ("sms_provider_aliyun".equals(provider)){
			return new AliyunSmsSender();
		}
		
//		其他短信服务商
//		else if("sms_provider_xxx".equals(provider)){
//			return new XXXSmsSender();
//		}
		
		return new AlidayuSmsSender();

	}

}
