package com.dbumama.market.web.core.fixedinterceptor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import io.jboot.Jboot;
import io.jboot.support.shiro.JbootShiroConfig;
import io.jboot.support.shiro.JbootShiroManager;
import io.jboot.support.shiro.processer.AuthorizeResult;
import io.jboot.web.fixedinterceptor.FixedInterceptor;

/**
 * Shiro 拦截器
 */
@Deprecated
public class WxmallShiroInterceptor implements FixedInterceptor {


    private JbootShiroConfig config = Jboot.config(JbootShiroConfig.class);


    @Override
    public void intercept(Invocation inv) {

        AuthorizeResult result = JbootShiroManager.me().invoke(inv.getActionKey());

        if (result == null || result.isOk()) {
            inv.invoke();
            return;
        }

        int errorCode = result.getErrorCode();
        switch (errorCode) {
            case AuthorizeResult.ERROR_CODE_UNAUTHENTICATED:
                doProcessUnauthenticated(inv.getController());
                break;
            case AuthorizeResult.ERROR_CODE_UNAUTHORIZATION:
                doProcessuUnauthorization(inv.getController());
                break;
            default:
                inv.getController().renderError(404);
        }
    }


    /**
     * 未认证处理
     *
     * @param controller
     */
    private void doProcessUnauthenticated(Controller controller) {
        if (StringUtils.isBlank(config.getLoginUrl())) {
            controller.renderError(401);
            return;
        }
        controller.redirect(config.getLoginUrl());
    }


    /**
     * 未授权处理
     *
     * @param controller
     */
    private void doProcessuUnauthorization(Controller controller) {
        if (StringUtils.isBlank(config.getUnauthorizedUrl())) {
            controller.renderError(403);
            return;
        }
        controller.redirect(config.getUnauthorizedUrl());
    }


}
