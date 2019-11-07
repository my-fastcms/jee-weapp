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
package com.dbumama.market.web.controller.wechat;

import com.dbumama.market.base.ApiConfig;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.encrypt.AesException;
import com.dbumama.market.encrypt.WXBizMsgCrypt;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.CompTicket;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.CompTicketService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.dbumama.weixin.api.GetPreAuthCodeApi;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import io.jboot.Jboot;
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.URLEncoder;
import java.util.Date;

/**
 * 微信第三方平台扫码授权事件接收
 * @author wangjun
 * 2018年6月14日
 */
@RequestMapping(value="weixin")
public class WechatAuthController extends BaseAdminController {
	
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private CompTicketService compTicketService;
	
	public void fallback(){
		renderText("");
	}
	
	//接收到微信推送的verify_ticket消息
	@Clear
	@Before(POST.class)
	@EnableLimit(rate=1, fallback="fallback")
	public void msg(){
		log.debug("===================接收到微信推送的消息");
		String resultXml = null;
		try {
			resultXml = HttpKit.readData(getRequest());
		} catch (Exception e) {
			renderText("success");
			return;
		}
		
		if(StrKit.isBlank(resultXml)){
			renderText("success");
			return;
		}
		
		String msg = "";
		try {
			WXBizMsgCrypt wxmc = new WXBizMsgCrypt(Jboot.config(ApiConfig.class).getToken(), Jboot.config(ApiConfig.class).getEncodingAesKey(), Jboot.config(ApiConfig.class).getAppId());
			msg = wxmc.decryptMsg(getPara("msg_signature"),
					getPara("timestamp"),
					getPara("nonce"), resultXml);
		} catch (AesException e) {
			e.printStackTrace();
			renderText("success");
			return;
		}
		
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(msg);
		} catch (DocumentException e) {
			e.printStackTrace();
			renderText("success");
			return;
		}
		Element root = doc.getRootElement();
		
		String infoType = root.elementText("InfoType");
		if("unauthorized".equals(infoType)){
			//用户取消了公众号授权
			log.debug("公众号：[" + root.elementText("AuthorizerAppid") + "] 取消了绑定");
			AuthUser pwu = authUserService.getAuthUserByAppId(root.elementText("AuthorizerAppid"));
			if(pwu != null){
				pwu.setActive(0);
				authUserService.update(pwu);
			}
		}else if ("authorized".equals(infoType)){
			//用户绑定公众号到本服务
//			log.debug("公众号：[" + root.elementText("AuthorizerAppid") + "] 绑定了");
//			AuthUser pwu = authUserService.getAuthUserByAppId(root.elementText("AuthorizerAppid"));
//			if(pwu != null){
//				pwu.setActive(1);
//				authUserService.update(pwu);
//				//清除auth user cache
//			}
		}else if("updateauthorized".equals(infoType)){
			//用户更新了授权后通知
			/**
			 * <xml>
				<AppId>第三方平台appid</AppId>
				<CreateTime>1413192760</CreateTime>
				<InfoType>updateauthorized</InfoType>
				<AuthorizerAppid>公众号appid</AuthorizerAppid>
				<AuthorizationCode>授权码（code）</AuthorizationCode>
				<AuthorizationCodeExpiredTime>过期时间</AuthorizationCodeExpiredTime>
				</xml>
			 */
			log.debug("公众号：[" + root.elementText("AuthorizerAppid") + "] 更新了授权信息");
			
		}else {
			//每隔10分中微信服务器推送ticket到本地服务器
			final String componentVerifyTicket = root.elementText("ComponentVerifyTicket");
			log.debug("=======componentVerifyTicket:" + componentVerifyTicket);
			
			CompTicket compTicket = authUserService.getCompTicket();
			if(compTicket == null) {
				compTicket = new CompTicket();
				compTicket.setCompVerifyTicket(componentVerifyTicket);
				compTicket.setCreated(new Date());
				compTicket.setUpdated(new Date());
				compTicketService.save(compTicket);
			}else{
				compTicket.setCompVerifyTicket(componentVerifyTicket);
				compTicket.setUpdated(new Date());
				compTicketService.update(compTicket);
			}
		}
		renderText("success");
	}
	
	public void auth2(){
		String mainUrl = "http://" + getRequest().getServerName();
        String callbackUrl = mainUrl + getRequest().getContextPath() + "/weixin/callback";
		ApiResult apiResult = GetPreAuthCodeApi.getPreAuthCode(authUserService.getCompAccessToken());
		final String pre_auth_code = apiResult.getStr("pre_auth_code");
		@SuppressWarnings("deprecation")
		final String oathUrl = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+Jboot.config(ApiConfig.class).getAppId()+"&pre_auth_code="+pre_auth_code+"&redirect_uri="+URLEncoder.encode(callbackUrl);
		redirect(oathUrl);
	}
	
	//授权回调
	public void callback(){
		try {
			AuthUser authUser = authUserService.bind(getSellerId(), getPara("auth_code"));
			if(authUser.getServiceType() == 0){
				redirect("/weapp/set?weappId="+authUser.getId());
			}else{
				redirect("/authuser/set?weappId="+authUser.getId());				
			}
		} catch (WxmallBaseException e) {
			log.error(e.getMessage());
			//跳到微信公众号绑定错误界面
			setAttr("error", e.getMessage());
			render("/setting/st_error.html");
		}
		
	}
	
}	
