/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.weixin.utils.HttpUtils;
import com.jfinal.kit.HttpKit;
import io.jboot.Jboot;
import org.apache.commons.lang3.StringUtils;

/**
 * 模板消息 API
 * 文档地址：http://mp.weixin.qq.com/wiki/17/304c1885ea66dbedf7dc170d84999a9d.html
 */
public class CompTemplateMsgApi {
	
	private static String sendApiUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
	
	private static String getTemplateIdUrl = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=";
	
	private static String getAllTemplateUrl = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=";
	
	/**
	 * 发送模板消息
	 */
	public static ApiResult send(String accessToken, String jsonStr) {
		String jsonResult = HttpUtils.post(sendApiUrl + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
	public static String getTemplateId(String accessToken, String jsonStr){
		String templateId = Jboot.getCache().get("template_msg_id", "template_msg_id");
		if(StringUtils.isBlank(templateId)){
			String jsonResult = HttpUtils.post(getTemplateIdUrl + accessToken, jsonStr);
			ApiResult result = new ApiResult(jsonResult);
			templateId = result.getStr("template_id");
			if(StringUtils.isNotBlank(templateId))
				Jboot.getCache().put("template_msg_id", "template_msg_id", templateId);
		}
		return templateId;
	}
	
	/**
     * 获取模板列表
     * @return {ApiResult}
     */
    public static ApiResult getAllTemplate(String accessToken) {
        return new ApiResult(HttpKit.get(getAllTemplateUrl + accessToken));
    }
	
	public static void main(String[] args) {
		/*JSONObject json = new JSONObject();
		json.put("template_id_short", "TM00785");
		String templateId = TemplateMsgApi.getTemplateId(json.toString());
		System.out.println("templateId1:" + templateId);
		
		TemplateData templateData = TemplateData.New().setTemplate_id(templateId)
			.setTouser("oQ774wnoZjqJt4UdAXusjT9WBvgI")
			.setUrl("http://m.dbumama.com/pay/luck")
			.add("first", "恭喜好农民中奖了", "#173177")
			.add("program", "疯狂抽奖", "#173177")
			.add("result", "抽中现金红包10元", "#173177")
			.add("remark", "疯狂抽奖，公平公正，点击去看看", "#173177");
		
		ApiResult apiResult = TemplateMsgApi.send(templateData.build());
		
		if(!apiResult.isSucceed()){
			System.out.println("error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
		}else{
			System.out.println("sucess:" + apiResult.isSucceed());
		}
		
		for(int i=0;i<30;i++){
			templateId = TemplateMsgApi.getTemplateId(json.toString());
			System.out.println("templateId"+i+":" + templateId);
			templateData = TemplateData.New().setTemplate_id(templateId)
					.setTouser("oQ774wnoZjqJt4UdAXusjT9WBvgI")
					.setUrl("http://m.dbumama.com/pay/luck")
					.add("first", "恭喜好农民中奖了", "#173177")
					.add("program", "疯狂抽奖", "#173177")
					.add("result", "抽中现金红包10元", "#173177")
					.add("remark", "疯狂抽奖，公平公正，点击去看看", "#173177");
			
			TemplateMsgApi.send(templateData.build());
			if(!apiResult.isSucceed()){
				System.out.println("error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
			}else{
				System.out.println("sucess:" + apiResult.isSucceed());
			}
		}*/
		
	}
	
}


