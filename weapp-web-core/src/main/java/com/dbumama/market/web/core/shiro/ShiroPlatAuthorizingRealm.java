package com.dbumama.market.web.core.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.dbumama.market.model.PlatUser;
import com.dbumama.market.service.api.PlatUserService;

import io.jboot.Jboot;

/**
 * 
 * @author wangjun
 *
 */
public class ShiroPlatAuthorizingRealm extends AuthorizingRealm{

	private PlatUserService platUserService = Jboot.service(PlatUserService.class);
	
	/**
	 * 构造函数，设置安全的初始化信息
	 */
	public ShiroPlatAuthorizingRealm() {
		super();
	}
	
	/**
	 * 登录认证
	 *
	 * @param token
	 * @return
	 * @throws org.apache.shiro.authc.AuthenticationException
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		PlatUser admin = platUserService.findByAccount(userToken.getUsername());
		
		if(admin == null)
			throw new UnknownAccountException("No account found for user [" + userToken.getUsername() + "]");
		
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(admin, admin.getPassword().toCharArray(), getName());
		return info;
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 *
	 * @param principals
	 *            用户信息
	 * @return
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String loginName = ((PlatUser) principals.fromRealm(getName()).iterator().next()).getAccount();
		PlatUser admin = platUserService.findByAccount(loginName);
		if(admin == null) SecurityUtils.getSubject().logout();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		return info;
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(Object principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除所有用户授权信息缓存.
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}

}
