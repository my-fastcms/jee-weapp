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
package com.dbumama.market.web.core.interceptor;

import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.utils.SignKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.dbumama.weixin.api.CompJsTicket;
import com.dbumama.weixin.api.CompJsTicketApi;
import com.dbumama.weixin.api.CompJsTicketApi.JsApiType;
import io.jboot.Jboot;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.TreeMap;

/**
 * 准备调用微信JS SDK接口需要的参数
 * @author wangjun
 * 2018年12月14日
 */
public class WechatJssdkInterceptor implements Interceptor {

	private AuthUserService authUserService = Jboot.service(AuthUserService.class);

	/* (non-Javadoc)
	 * @see com.jfinal.aop.Interceptor#intercept(com.jfinal.aop.Invocation)
	 */
	@Override
	public void intercept(Invocation inv) {
		HttpServletRequest request = inv.getController().getRequest();
		
		String mainUrl = "http://" + request.getServerName();
        String rurl = mainUrl + request.getServletPath();
        if (request.getQueryString() != null) {
            rurl += "?" + request.getQueryString();
        }
		String nonceStr = SignKit.genRandomString32();
		if(authUserService.getAuthUserByAppId(getAppId(request)) != null){
			CompJsTicket compJsTicket = CompJsTicketApi.getTicket(JsApiType.jsapi, getAppId(request), authUserService.getAccessToken(authUserService.getAuthUserByAppId(getAppId(request))));
			if(compJsTicket != null && compJsTicket.isSucceed()){
				final String jsapiTicket = compJsTicket.getTicket();
				final String timestamp = String.valueOf(new Date().getTime());

				// 准备调用支付js接口的参数
				TreeMap<String, Object> params = new TreeMap<String, Object>();
				params.put("jsapi_ticket", jsapiTicket);
				params.put("noncestr", nonceStr);
				params.put("timestamp", timestamp);
				params.put("url", rurl);
				
				request.setAttribute("signature", SignKit.signSHA1(params));
				request.setAttribute("nonceStr", nonceStr);
				request.setAttribute("timestamp", timestamp);
				request.setAttribute("appId", getAppId(request));
				request.setAttribute("requestUrl", rurl);
			}
		}
		
		inv.invoke();
	}
	
	protected String getAppId(HttpServletRequest request){
		if(JFinal.me().getConstants().getDevMode()){
			return WeappConstants.WECHAT_LOGIN_APPID;
		}
		String serverName = request.getServerName();
		serverName = serverName.substring(0, serverName.indexOf("."));
		return serverName;
	}

}
