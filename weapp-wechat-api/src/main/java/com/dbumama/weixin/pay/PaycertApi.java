package com.dbumama.weixin.pay;

import java.util.TreeMap;

import com.dbumama.weixin.utils.HttpUtils;
import com.jfinal.kit.StrKit;

/**
 * 使用证书支付api 基类
 * wjun_java@163.com
 * 2015年12月11日
 */
public abstract class PaycertApi extends PayApi{

	@Override
	public BaseResData post(BaseReq reqData, byte [] certs) throws Exception {
		TreeMap<String, Object> map = reqData.toMap();
		String mchId = map.get("mch_id") == null ? "" : map.get("mch_id").toString();
		
		if(StrKit.isBlank(mchId)) {
			mchId = (String) map.get("mchid");
		}
		
		String postDataXML = getRequestXml(map);
		String result = HttpUtils.postSSL(getApiUrl(), postDataXML, certs, mchId);
		return getRespone(getResponseRoot(result));
	}
	
}
