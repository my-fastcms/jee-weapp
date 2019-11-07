package com.dbumama.market.notify.sms;

import com.dbumama.market.utils.SignKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlidayuSmsSender implements ISmsSender {
	private static final Log log = Log.getLog(AlidayuSmsSender.class);

	/**
	 * http://open.taobao.com/doc2/apiDetail.htm?spm=a219a.7395905.0.0.Y1YXKM&
	 * apiId=25443
	 */
	
	private WxmSmsConfig smsConfig = Jboot.config(WxmSmsConfig.class); 

	@Override
	public boolean send(SmsMessage sms) {
		String app_key =  smsConfig.getSmsAppKey();//"your app key";
		String app_secret =  smsConfig.getSmsAppSecret();//"your app secret"

		String sendResult = doSend(sms, app_key, app_secret);


		if (StringUtils.isNotBlank(sendResult)) {
			if (sendResult != null && sendResult.contains("alibaba_aliqin_fc_sms_num_send_response")
					&& sendResult.contains("success") && sendResult.contains("true")) {
				return true;
			}else{
				System.out.println("=============================");
				System.out.println(sendResult);
			}
		}
		return false;
	}

	private static String doSend(SmsMessage sms, String app_key, String app_secret) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("format", "json");
		params.put("method", "alibaba.aliqin.fc.sms.num.send");
		params.put("sign_method", "md5");

		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		params.put("timestamp", timestamp);
		params.put("v", "2.0");
		params.put("rec_num", sms.getRec_num());
		params.put("sms_free_sign_name", sms.getSign_name());
		params.put("sms_param", sms.getParam());
		params.put("sms_template_code", sms.getTemplate());
		params.put("sms_type", "normal");
		params.put("app_key", app_key);

		String sign = SignKit.signForRequest(params, app_secret);
		params.put("sign", sign);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		try {
			return HttpKit.post("http://gw.api.taobao.com/router/rest", params, "", headers);
		} catch (Exception e) {
			log.error("AlidayuSmsSender doSend http exception", e);
		}
		return null;
	}

	public static void main(String[] args) {
		SmsMessage sms = new SmsMessage();

		sms.setContent("test");
		sms.setRec_num("13533109940");
		sms.setTemplate("SMS_119090227");
		sms.setParam("{\"code\":\"8888\"}");
		sms.setSign_name("收亿科技");

		boolean sendOk = new AlidayuSmsSender().send(sms);

		System.out.println(sendOk);
		System.out.println("===============finished!===================");
	}

}
