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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.render.TextRender;

import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;

/**
 * @author wangjun
 * 2019年6月6日
 */
public class CSRFInterceptor implements Interceptor {

    public static final String CSRF_ATTR_KEY = "CSRF_TOKEN";
    public static final String CSRF_KEY = "csrf_token";


    public void intercept(Invocation inv) {
    	
    	final String method = inv.getController().getRequest().getMethod();
    	
    	//WEAPP中所有数据操作都必须使用POST请求
        if (StrKit.isBlank(method) || "GET".equals(method)) {
            renderNormal(inv);
            return;
        }

        //从cookie中读取token，因为 第三方网站 无法修改 和 获得 cookie
        //所以从cookie获取存储的token是安全的
        String cookieToken = inv.getController().getCookie(CSRF_KEY);
        if (StrUtil.isBlank(cookieToken)) {
            renderBad(inv);
            return;
        }

        //url参数里的csrf_token
        String paraToken = inv.getController().getPara(CSRF_KEY);
        if (StrUtil.isBlank(paraToken)) {
            renderBad(inv);
            return;
        }

        if (cookieToken.equals(paraToken) == false) {
            renderBad(inv);
            return;
        }

        renderNormal(inv);
    }


    private void renderNormal(Invocation inv) {
        // 不是 ajax 请求，才需要重置本地 的token
        // ajax 请求，需要保证之前的token可以继续使用
        if (RequestUtil.isAjaxRequest(inv.getController().getRequest()) == false) {
            String uuid = StrUtil.uuid();
            inv.getController().setCookie(CSRF_KEY, uuid, -1);
            inv.getController().setAttr(CSRF_ATTR_KEY, uuid);
        }

        inv.invoke();
    }


    private void renderBad(Invocation inv) {
        if (RequestUtil.isAjaxRequest(inv.getController().getRequest())) {
            inv.getController().renderJson(Ret.fail().set("message", "bad or mission token!"));
        } else {
            inv.getController().renderError(403, new TextRender("bad or missing token!"));
        }
    }

}
