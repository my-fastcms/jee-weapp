package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.jfinal.kit.HttpKit;

/**
 * 微信小程序代码管理相关接口
 * @author wangjun
 *
 */
public class CompWxaCodeApi {
	
	//绑定体验者
	static final String bindtest_url = "https://api.weixin.qq.com/wxa/bind_tester?access_token=";
	
	public static ApiResult bindtest(String accessToken, String tester){
		String jsonResult = HttpKit.post(bindtest_url + accessToken, tester);
		return new ApiResult(jsonResult);
	}

	//解绑体验者
	static final String unbindtest_url = "https://api.weixin.qq.com/wxa/unbind_tester?access_token=";
	
	public static ApiResult unbindtest(String accessToken, String tester){
		String jsonResult = HttpKit.post(unbindtest_url + accessToken, tester);
		return new ApiResult(jsonResult);
	}
	
	static final String commit_url = "https://api.weixin.qq.com/wxa/commit?access_token=";
	
	public static ApiResult commit(String accessToken, String jsonStr){
		String jsonResult = HttpKit.post(commit_url + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
	//审核通过前，获取小程序体验二维码，需要绑定体验者账号方可体验
	static final String get_qrcode_url = "https://api.weixin.qq.com/wxa/get_qrcode?access_token=";
	
	public static String getQrcode(String accessToken){
		return get_qrcode_url + accessToken;
	}
	
	//获取授权小程序帐号的可选类目
	static final String get_category_url = "https://api.weixin.qq.com/wxa/get_category?access_token=";
	
	/**
	 * {
		"errcode":0,
		"errmsg": "ok",
		"category_list" : [
			{
				"first_class":"工具",
				"second_class":"备忘录"
			}
			{
				"first_class":"教育",
				"second_class":"学历教育",
				"third_class":"高等"
			}
		]
	}
	 * @param accessToken
	 * @return
	 */
	public static ApiResult getCategory(String accessToken){
		String jsonResult = HttpKit.get(get_category_url + accessToken);
		return new ApiResult(jsonResult);
	}
	
	//获取小程序的第三方提交代码的页面配置（仅供第三方开发者代小程序调用）
	static final String get_page_url = "https://api.weixin.qq.com/wxa/get_page?access_token=";
	
	/**
	 * {
		"errcode":0,
		"errmsg":"ok",
		"page_list":[
			"index",
			"page\/list",
			"page\/detail"
		]
	 }
	 * @param accessToken
	 * @return
	 */
	public static ApiResult getPage(String accessToken){
		String jsonResult = HttpKit.get(get_page_url + accessToken);
		return new ApiResult(jsonResult);
	}
	
	static final String submit_audit_url = "https://api.weixin.qq.com/wxa/submit_audit?access_token=";
	
	/**
	 * {
	"item_list": [
			{
				"address":"index",
				"tag":"学习 生活",
				"first_class": "文娱",
				"second_class": "资讯",
				"title": "首页"
			}
			{
				"address":"page/logs/logs",
				"tag":"学习 工作",
				"first_class": "教育",
				"second_class": "学历教育",
				"third_class": "高等",
				"title": "日志"
			}
		]
	}
	 * @param accessToken
	 * @param jsonStr
	 * @return
	 */
	public static ApiResult submitAudit(String accessToken, String jsonStr){
		String jsonResult = HttpKit.post(submit_audit_url + accessToken, jsonStr);
		return new ApiResult(jsonResult);
	}
	
	//查询最新一次提交的审核状态（仅供第三方代小程序调用）
	static final String get_latest_auditstatus_url = "https://api.weixin.qq.com/wxa/get_latest_auditstatus?access_token=";
	public static ApiResult getLastAuditStatus(String accessToken){
		String jsonResult = HttpKit.get(get_latest_auditstatus_url + accessToken);
		return new ApiResult(jsonResult);
	}
	
	//获取第三方提交的审核版本的审核状态（仅供第三方代小程序调用）
	static final String get_auditstatus_url = "https://api.weixin.qq.com/wxa/get_auditstatus?access_token=";
	
	/**
	 * {
           "auditid":1234567
       }
	 * @param accessToken
	 * @param auditid
	 * @return
	 */
	public static ApiResult getAuditStatus(String accessToken, String auditid){
		ParaMap map = ParaMap.create().put("auditid", auditid);
		String jsonResult = HttpKit.post(get_auditstatus_url + accessToken, JsonUtils.toJson(map.getData()));
		return new ApiResult(jsonResult);
	}
	
	//发布已通过审核的小程序（仅供第三方代小程序调用）
	static final String release_url = "https://api.weixin.qq.com/wxa/release?access_token=";
	
	public static ApiResult release(String accessToken){
		String jsonResult = HttpKit.post(release_url + accessToken, "{}");
		return new ApiResult(jsonResult);
	}
	
	//修改小程序线上代码的可见状态（仅供第三方代小程序调用）
	static final String change_visitstatus_url = "https://api.weixin.qq.com/wxa/change_visitstatus?access_token=";
	
	public static ApiResult changeVisitstatus(String accessToken){
		ParaMap map = ParaMap.create().put("action", "close");
		String jsonResult = HttpKit.post(change_visitstatus_url + accessToken, JsonUtils.toJson(map.getData()));
		return new ApiResult(jsonResult);
	}
}
