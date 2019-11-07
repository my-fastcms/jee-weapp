/**
 * 文件名:BaseInterceptor.java
 * 版本信息:1.0
 * 日期:2015-10-9
 * 版权所有
 */
package com.dbumama.market.web.core.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.service.api.FileService;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import io.jboot.Jboot;

/**
 * 调试接口使用，不签名，不加密
 * 只支持json跟form-data方式的请求
 * @author: wjun.java@gmail.com
 * @date:2015-10-9
 */
public class BaseApiDebugInterceptor implements Interceptor{
	
	public Logger log = Logger.getLogger(getClass());
	
	FileService fileService = Jboot.service(FileService.class);
	
	@Override
	public void intercept(Invocation ai) {
		String uri = ai.getController().getRequest().getRequestURI();
		
		log.debug("uri:" + uri);
		if("/".equals(uri)){
			ai.invoke();
			return;
		}
		
		Controller controller = ai.getController();
		
		/*/test......
		if(!"application/json".equals(controller.getRequest().getContentType())){
			Enumeration<String> params=ai.getController().getRequest().getParameterNames();
			JSONObject jsonObj = new JSONObject();
			while(params.hasMoreElements()){
			  String p=	params.nextElement();
			  jsonObj.put(p, ai.getController().getRequest().getParameter(p));
			}
		    ((BaseController)controller).setMessageJson(jsonObj);
		   ai.invoke();
		   return;
		}*/
		//test end....
		
		log.debug(controller.getRequest().getContentType());
		if(controller instanceof BaseController == false){
			throw new RuntimeException("must extends BaseController");
		}
		JSONObject jsonMessageObj = new JSONObject();
		JSONObject jsonObj = null;
		
		if(StrKit.notBlank(controller.getRequest().getContentType()) 
				&& controller.getRequest().getContentType().contains("application/json")){
			try {
				String json = inputStream2String(controller.getRequest().getInputStream());
				jsonObj = JSON.parseObject(json);
			} catch (Exception e) {
				e.printStackTrace();
				jsonMessageObj.put("error", "true");
			}
			
			if(jsonObj != null){
				/*String message = jsonObj.getString("message");
				String decryMessage = "";
				try {
					decryMessage = DESUtil.decryptDES(message, Constants.MIMI);
				} catch (Exception e) {
					e.printStackTrace();
					jsonMessageObj.put("error", "true");
				}
				
				if(StrKit.notBlank(decryMessage)){
					jsonMessageObj = JSON.parseObject(decryMessage);
					log.debug(jsonMessageObj);
				}*/
				jsonMessageObj = jsonObj;
			}
		}else if(controller.getRequest().getContentType()!=null 
				&& controller.getRequest().getContentType().contains("multipart/form-data")){
			//获取userid
			String filePath = "/" + controller.getRequest().getHeader("userid");
			//controller.getFile(parameterName, filePath)
			List<UploadFile> files = controller.getFiles(PropKit.get("upload_file_path") + filePath);
			
			
			Enumeration<String> paramNames = controller.getRequest().getParameterNames();
			while(paramNames.hasMoreElements()){
				String paramName = paramNames.nextElement();
				if(StrKit.notBlank(paramName)){
					jsonMessageObj.put(paramName, controller.getRequest().getParameter(paramName));
				}
			}
			
            if(uri.equalsIgnoreCase("/diyproduct/save")){
            	if(files!=null&& files.size()>0){
	            	 for(UploadFile f:files){
	            		 String url = "";
	            		 try {
							url = fileService.upload(f.getFile());
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
		            	 if(f.getOriginalFileName().indexOf("effectBit")>0){//1474510387312_effectBit
		            		 //jsonMessageObj.put("picURL", filePath+"/"+ f.getFileName());
		            		jsonMessageObj.put("picURL", url);
		            	 }else if(f.getOriginalFileName().indexOf("resultBit")>0){//1474510386906_resultBit.png
		            		 //jsonMessageObj.put("fpicURL",filePath+"/"+f.getFileName());
		            		 jsonMessageObj.put("fpicURL", url);
		            	 }
	            	 }
            	}	 
			}
			
			StringBuffer filePaths = new StringBuffer();
			for(UploadFile file : files){
				String url = "";
				try {
					url = fileService.upload(file.getFile());
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
		        //filePaths.append(filePath).append("/").append(file.getFileName()).append("#");
				filePaths.append(url).append("#");
			}
			
			if(filePaths.length()>0){
				filePaths.deleteCharAt(filePaths.length()-1);
			}
			
			jsonMessageObj.put("filepath", filePaths.toString());
		}else {
			throw new RuntimeException(
					"req contentType:" + controller.getRequest().getContentType() +
					";ip:" + controller.getRequest().getRemoteAddr());
		}
		
		if(jsonMessageObj.get("error")!=null && jsonMessageObj.getBooleanValue("error")==true){
			//((BaseApiController)controller).rendFailedJson(jsonMessageObj.get("error").toString());
		}else {
			//签名校验
			//List<Parameter> list = new ArrayList<Parameter>(jsonMessageObj.size());
			
			
			/*String signParm = "";
			for(String key : jsonMessageObj.keySet()){
				if("sign".equals(key)){
					signParm = jsonMessageObj.getString("sign");
					continue;
				} 
				if("filepath".equals(key)||"fpicURL".equals(key)||"picURL".equals(key)) continue;
				
				Parameter parameter = new Parameter(key, jsonMessageObj.getString(key));
                list.add(parameter);
			}
			
			String sign = null;
            try {
                sign = WirelessSecretUtil.getToken(list, PropKit.get("wirelessSecret"));
            } catch (Exception e) {
                throw new RuntimeException("生成签名错误");
            }
            if (!sign.equals(signParm)) {
                throw new RuntimeException("sign校验错误");
            }*/
			
			//获取header
			final Enumeration<String> headerNames = controller.getRequest().getHeaderNames();
			while(headerNames.hasMoreElements()){
				String headerName = headerNames.nextElement();
				if(StrKit.notBlank(headerName)){
					jsonMessageObj.put(headerName.toLowerCase(), controller.getRequest().getHeader(headerName));
				}
			}
			((BaseApiController)controller).setMessageJson(jsonMessageObj);
			ai.invoke();
		}
	}

	private String inputStream2String(InputStream in) throws UnsupportedEncodingException, IOException{
		if(in == null)
			return "";
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n, "UTF-8"));
		}
		return out.toString();
	}

}
