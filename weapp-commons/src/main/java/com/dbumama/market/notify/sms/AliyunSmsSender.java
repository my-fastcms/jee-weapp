/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.notify.sms;

import com.dbumama.market.utils.SignKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangjun
 * 2018年1月8日
 */
public class AliyunSmsSender implements ISmsSender {

	private static final Log log = Log.getLog(AliyunSmsSender.class);
	
	private final static String TIME_ZONE = "GMT";
    private final static String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    private WxmSmsConfig smsConfig = Jboot.config(WxmSmsConfig.class); 
    
    public static String getISO8601Time(Date date) {
        Date nowDate = date;
        if (null == date) {
            nowDate = new Date();
        }
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ISO8601);
        df.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));

        return df.format(nowDate);
    }
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.notify.sms.ISmsSender#send(com.dbumama.market.service.notify.sms.SmsMessage)
	 */
	@Override
	public boolean send(SmsMessage sms) {
		
		String app_key =  smsConfig.getSmsAppKey();//"your app key";
		String app_secret =  smsConfig.getSmsAppSecret();//"your app secret"
		
//		String app_key =  "LTAInkqz5gg3mgQQ";//"your app key";
//		String app_secret =  "O49048okkpABIdbQXRLfjy5hGOruIR";//"your app secret"
		
		String sendResult = doSend(sms, app_key, app_secret);

		/**
		 * 接口返回
		 * <?xml version='1.0' encoding='UTF-8'?>
		 * <SendSmsResponse>
		 * <Message>OK</Message>
		 * <RequestId>DC230867-1946-4044-A382-951FF2F5AA17</RequestId>
		 * <BizId>822801715396849381^0</BizId><Code>OK</Code>
		 * </SendSmsResponse>
		 * 
		 */
		if (StringUtils.isNotBlank(sendResult)) {
			if (sendResult != null && sendResult.contains("<Message>OK</Message>")
					&& sendResult.contains("<Code>OK</Code>")) {
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
		params.put("Format", "XML");
		params.put("SignName", sms.getSign_name());
		params.put("SignatureMethod", "HMAC-SHA1");

		params.put("Timestamp", getISO8601Time(null));
		params.put("TemplateCode", sms.getTemplate());
		params.put("TemplateParam", "{\"code\":\""+sms.getCode()+"\"}");
		params.put("Action", "SendSms");
		params.put("AccessKeyId", app_key);
		params.put("RegionId", "cn-hangzhou");
		params.put("PhoneNumbers", sms.getRec_num());
		params.put("Version", "2017-05-25");
		params.put("SignatureVersion", "1.0");
		params.put("SignatureNonce", UUID.randomUUID().toString());

		String sign = null;
		try {
			sign = SignKit.signShaHmac1(params, app_secret + "&");
		} catch (InvalidKeyException | IllegalStateException e1) {
			e1.printStackTrace();
			return null;
		}
		params.put("Signature", sign);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.put("x-sdk-client", "Java/2.0.0");
		try {
			return HttpKit.get("http://dysmsapi.aliyuncs.com/", params, headers);
		} catch (Exception e) {
			log.error("AlidayuSmsSender doSend http exception", e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		SmsMessage sms = new SmsMessage();

		sms.setContent("test");
		sms.setRec_num("15112220011");
		sms.setTemplate("SMS_119090227");
		sms.setParam("{\"code\":\"888888\"}");
		sms.setSign_name("收亿科技");

		boolean sendOk = new AliyunSmsSender().send(sms);

		System.out.println(sendOk);
		System.out.println("===============finished!===================");
	}

}
