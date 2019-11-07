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
package com.dbumama.market.web.core.handler;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.MenuItem;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.service.enmu.AuthUserType;
import com.dbumama.market.web.core.menu.AdminMenuManager;
import com.jfinal.handler.Handler;
import io.jboot.Jboot;
import io.jboot.utils.RequestUtil;
import org.apache.shiro.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wangjun
 * 2017年11月8日
 */
public class WeappSessionHandler extends Handler{
	
	private AuthUserService authUserService = Jboot.service(AuthUserService.class);
	
	private static final ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public static String getCurrentTarget() {
        return threadLocal.get();
    }
	
	/* (non-Javadoc)
	 * @see com.jfinal.handler.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean[])
	 */
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		
		if(target.contains("/resources/") || target.contains(".css") || target.contains(".js") || target.contains(".ico")
				|| target.contains(".jpg") || target.contains(".png") || target.contains(".txt") || target.contains(".php")  
				|| target.contains(".html") || target.contains(".htm") || target.contains(".gif")){
			return;
		}
		
		if(RequestUtil.isAjaxRequest(request) || RequestUtil.isMultipartRequest(request)){
			next.handle(target, request, response, isHandled);
			return;
		}
		
		boolean hasSessionIdInURL = request.isRequestedSessionIdFromURL() || request.isRequestedSessionIdFromCookie();
		if (hasSessionIdInURL) {
		    int index = target.indexOf(";");
    		if (index > 0) {
    		   target = target.substring(0, index);
    		}
		}
		
		try {
			threadLocal.set(target);
			
			if(SecurityUtils.getSubject()!=null 
					&& SecurityUtils.getSubject().getPrincipal()!=null
					&& SecurityUtils.getSubject().isAuthenticated()){

				AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getSession().getAttribute(WeappConstants.WEB_WEAPP_IN_SESSION);
				if(authUser !=null){
					List<MenuItem> menuItems = null;
					if(authUser.getServiceType() == AuthUserType.dingyue.ordinal()){
						menuItems = AdminMenuManager.me.getDyhMenus();
					}else if(authUser.getServiceType() == AuthUserType.fuwu.ordinal()){
						menuItems = AdminMenuManager.me.getFwhMenus();
					}else if(authUser.getServiceType() == AuthUserType.xiaochengxu.ordinal()){
						menuItems = AdminMenuManager.me.getWeappMenus();
					}
					request.setAttribute("menus", menuItems);
				}
				SellerUser seller = (SellerUser) SecurityUtils.getSubject().getPrincipal();
				List<AuthUser> authUsers = authUserService.getSellerAuthUser(seller.getId());
				List<AuthUser> weappUsers = authUserService.getSellerAuthUserWeapp(seller.getId());
				request.setAttribute("authUsers", authUsers);
				request.setAttribute("weappUsers", weappUsers);
			}
			
			next.handle(target, request, response, isHandled);
			
		} finally{
			threadLocal.remove();
		}
	}
	
}
