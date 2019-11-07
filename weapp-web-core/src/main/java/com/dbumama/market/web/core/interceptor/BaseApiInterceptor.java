/**
 * 文件名:BaseInterceptor.java
 * 版本信息:1.0
 * 日期:2015-10-9
 * 版权所有
 */
package com.dbumama.market.web.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.security.Parameter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 只支持json跟form-data方式的请求
 * api全局拦截器，不能被clear清除
 * @author: wjun.java@gmail.com
 * @date:2015-10-9
 */
public class BaseApiInterceptor implements Interceptor{
	
	public Logger log = Logger.getLogger(getClass());
	
	//private FileService fileService = Jboot.service(FileService.class);

	@Override
	public void intercept(Invocation ai) {

		Controller controller = ai.getController();
		log.debug(controller.getRequest().getContentType());
		if(controller instanceof BaseApiController == false){
			throw new RuntimeException("must extends BaseApiController");
		}
		
		JSONObject jsonMessageObj = new JSONObject();
		
		if(StrKit.notBlank(controller.getRequest().getContentType()) 
				&& controller.getRequest().getContentType().contains("application/json")){
			try {
				String json = ((BaseApiController) controller).getRawData();
				
				if(StrKit.notBlank(json)){
					jsonMessageObj = JSON.parseObject(json);
				}
				
			} catch (Exception e) {
				jsonMessageObj.put("error", "true");
			}
			
		}else if(controller.getRequest().getContentType()!=null 
				&& controller.getRequest().getContentType().contains("multipart/form-data")){
			
			//获取userid
			//String filePath = "/" + controller.getRequest().getHeader("userid");
			//List<UploadFile> files = controller.getFiles(PropKit.get("upload_file_path") + filePath);
			
			Enumeration<String> paramNames = controller.getRequest().getParameterNames();
			while(paramNames.hasMoreElements()){
				String paramName = paramNames.nextElement();
				if(StrKit.notBlank(paramName)){
					jsonMessageObj.put(paramName, controller.getRequest().getParameter(paramName));
				}
			}
			
		}else {
//			throw new RuntimeException(
//					"req contentType:" + controller.getRequest().getContentType() +
//					";ip:" + controller.getRequest().getRemoteAddr());
			controller.renderError(404);
			return;
		}
		
		if(jsonMessageObj.get("error")!=null && jsonMessageObj.getBooleanValue("error")==true){
			((BaseApiController)controller).rendFailedJson(jsonMessageObj.get("error").toString());
		}else {
			//签名校验
			List<Parameter> list = new ArrayList<Parameter>(jsonMessageObj.size());
			
			String signParm = "";
			for(String key : jsonMessageObj.keySet()){
				if("sign".equals(key)){
					signParm = jsonMessageObj.getString("sign");
					continue;
				} 
				//图片文件去掉...
				if("filepath".equals(key)) continue;
				
				Parameter parameter = new Parameter(key, jsonMessageObj.getString(key));
                list.add(parameter);
			}
			
//			if(list.size() >0){
				//ios端签名校验有问题，暂时屏蔽掉签名校验
//				String sign = null;
//	            try {
//	                sign = WirelessSecretUtil.getToken(list, wxmallConfig.getWirelessSecret());
//	            } catch (Exception e) {
//	                throw new RuntimeException("生成签名错误");
//	            }
//	            if (!sign.equals(signParm.toUpperCase())) {
//	                throw new RuntimeException("sign校验错误");
//	            }
//			}
			
			//获取header
			final Enumeration<String> headerNames = controller.getRequest().getHeaderNames();
			while(headerNames.hasMoreElements()){
				String headerName = headerNames.nextElement();
				if(StrKit.notBlank(headerName)){
					jsonMessageObj.put(headerName.toLowerCase(), controller.getRequest().getHeader(headerName));
				}
			}
			((BaseApiController)controller).setMessageJson(jsonMessageObj);
			
			//check appId是否有值
			if(StrKit.isBlank(((BaseApiController)controller).getAppId())){
				((BaseApiController)controller).rendFailedJson("appId is null");
			}else{
				ai.invoke();				
			}
		}
	}
	
}
