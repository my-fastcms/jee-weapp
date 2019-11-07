package com.dbumama.weixin.api;

import com.alibaba.fastjson.JSON;
import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import io.jboot.Jboot;

import java.util.Map;

/**
 * 获取授权方的选项设置信息
 * @author wangjun
 *
 */
public class GetAuthorizerOptionApi {

	static final String url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_option?component_access_token=";
	
	/**
	 * 选项名和选项值表
		option_name	option_value	选项值说明
		location_report(地理位置上报选项)	0	无上报
										1	进入会话时上报
										2	每5s上报
		voice_recognize（语音识别开关选项）	0	关闭语音识别
										1	开启语音识别
		customer_service（多客服开关选项）	0	关闭多客服
										1	开启多客服
	 * 
	 * @param authAppId
	 * @param optionName
	 * @return
	 */
	public static ApiResult getAuthorOption(String compAccessToken, String authAppId, String optionName){
		final Map<String, String> queryParas = ParaMap.create("component_appid", Jboot.config(ApiConfig.class).getAppId())
				.put("authorizer_appid", authAppId)
				.put("option_name", optionName)
				.getData();
		String jsonResult = HttpUtils.post(url + compAccessToken, JSON.toJSONString(queryParas));
		return new ApiResult(jsonResult);
	}
	
}
