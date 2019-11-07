package com.dbumama.market.web.core.interceptor;

import java.util.Date;

import org.apache.shiro.SecurityUtils;

import com.dbumama.market.model.App;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.AuthUserApp;
import com.dbumama.market.service.api.AppService;
import com.dbumama.market.service.api.AuthUserAppService;
import com.dbumama.market.service.api.PurchaseService;
import com.dbumama.market.WeappConstants;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;

import io.jboot.Jboot;

/**
 * 检查用户购买的应用是否在使用期限内
 * wjun_java@163.com
 * 2016年7月7日
 */
public class ForbidInterceptor implements Interceptor {
	
	private AppService appService = Jboot.service(AppService.class);
	
	private AuthUserAppService authUserAppService = Jboot.service(AuthUserAppService.class);
	
	private PurchaseService purchaseService = Jboot.service(PurchaseService.class);
	
	private final static String APP_ID_COOKIENAME = "app_id_in_cookie";

	/* (non-Javadoc)
	 * @see io.jboot.web.fixedinterceptor.FixedInterceptor#intercept(io.jboot.web.fixedinterceptor.FixedInvocation)
	 */
	@Override
	public void intercept(Invocation inv) {
		
		final String app_id_cookie = inv.getController().getCookie(APP_ID_COOKIENAME);
		
		if(StrKit.isBlank(app_id_cookie)){
			render403(inv);
		}
		
		AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getSession().getAttribute(WeappConstants.WEB_WEAPP_IN_SESSION);
		
		if(authUser == null){
			inv.getController().redirect("/authuser");
			return;
		}
		
		Long appIdInCookie = null;
		try {
			appIdInCookie = Long.valueOf(app_id_cookie);
		} catch (Exception e) {
			render403(inv);
		}
		
		if(appIdInCookie == null){
			render403(inv);
		}
		
		App app = appService.findById(appIdInCookie);
		if(app == null) render403(inv);;
		
		if(app.getIsfree()!=null && app.getIsfree()){
			inv.invoke();
		}else{
			
			inv.getController().setAttr("app", app);
			
			AuthUserApp authUserApp = authUserAppService.findByApp(authUser.getId(), appIdInCookie);
			
			if(authUserApp == null){
				inv.getController().setAttr("purchases", purchaseService.findByAppId(appIdInCookie));
				inv.getController().forwardAction("/forbid");
				return;
			}
			
			if(!check(authUserApp)){
				inv.getController().setAttr("purchases", purchaseService.findByAppId(appIdInCookie));
				inv.getController().setAttr("authUserApp", authUserApp);
				inv.getController().forwardAction("/forbid");
				return;
			}
			
			inv.invoke();
			
		}
		
	}
	
	private boolean check(AuthUserApp authUserApp){
		
		if(authUserApp.getStartDate() == null || authUserApp.getEndDate() == null) return false;
		if(authUserApp.getStartDate().after(authUserApp.getEndDate())) return false;
		if(authUserApp.getEndDate().before(new Date())) return false;
		
		return true;
	}
	
	private void render403(Invocation inv) {
		inv.getController().renderError(403);
		return;
	}
	
}
