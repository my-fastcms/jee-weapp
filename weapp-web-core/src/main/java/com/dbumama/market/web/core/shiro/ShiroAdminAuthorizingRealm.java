package com.dbumama.market.web.core.shiro;


import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
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
import org.apache.shiro.util.ByteSource;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.Menu;
import com.dbumama.market.model.Role;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.MenuService;
import com.dbumama.market.service.api.RoleService;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.WeappConstants;
import com.jfinal.kit.StrKit;

import io.jboot.Jboot;

/**
 * 
 * @author wangjun
 *
 */
public class ShiroAdminAuthorizingRealm extends AuthorizingRealm{

	private SellerUserService sellerUserService = Jboot.service(SellerUserService.class);
	
	private RoleService roleService = Jboot.service(RoleService.class);
	
	private MenuService menuService = Jboot.service(MenuService.class);
	
	/**
	 * 构造函数，设置安全的初始化信息
	 */
	public ShiroAdminAuthorizingRealm() {
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
		
		String username = "";
		SellerUser admin = null;
		
		if(token instanceof WechatLoginToken){
			WechatLoginToken userToken = (WechatLoginToken) token;
			username = userToken.getOpenid();
			
			if(StrKit.isBlank(userToken.getAccessToken()))
				throw new UnknownAccountException("No account found for user [" + username + "] accessToken is null");
			
		}else if(token instanceof UsernamePasswordToken){
			UsernamePasswordToken userToken = (UsernamePasswordToken) token;
			username = userToken.getUsername();
			
			if(userToken.getPassword() == null)
				throw new UnknownAccountException("No account found for user [" + username + "] password is null");
			
		}else if(token instanceof WeimoLoginToken){
			WeimoLoginToken userToken = (WeimoLoginToken) token;
			username = userToken.getPid();
			
			if(StrKit.isBlank(userToken.getAccessToken()))
				throw new UnknownAccountException("No account found for user [" + username + "]  accessToken is null");
		}else if(token instanceof YouzanLoginToken){
			YouzanLoginToken userToken = (YouzanLoginToken) token;
			username = userToken.getKdtId();
			if(StrKit.isBlank(userToken.getAccessToken()))
				throw new UnknownAccountException("No account found for user [" + username + "] accessToken is null");
		}
		else {
			throw new AccountException("unsupport token type");
		}
		
		if (StrKit.isBlank(username))
            throw new AccountException("Null usernames are not allowed by this realm.");
		
		if(token instanceof WechatLoginToken){
			admin = sellerUserService.findByOpenid(username);
		}
		
		if(token instanceof WeimoLoginToken){
			admin = sellerUserService.findByWeimoPid(username);
		}
		
		if(token instanceof UsernamePasswordToken){
			admin = sellerUserService.findByPhone(username);
		}
		
		if(token instanceof YouzanLoginToken){
			YouzanLoginToken youzanToken = (YouzanLoginToken) token;
			admin = sellerUserService.findByKdtId(username, youzanToken.getPhone());
		}
		
		if(admin == null)
			throw new UnknownAccountException("No account found for user [" + username + "]");
		
		SimpleAuthenticationInfo info = null; 
		
		if(token instanceof UsernamePasswordToken){
			info = new SimpleAuthenticationInfo(admin, admin.getPassword().toCharArray(), getName());
		}
		
		if(token instanceof WechatLoginToken){
			info = new SimpleAuthenticationInfo(admin, admin.getOpenId(), getName());
		}
		
		if(token instanceof WeimoLoginToken){
			info = new SimpleAuthenticationInfo(admin, admin.getWeimoPid(), getName());
		}
		
		if(token instanceof YouzanLoginToken){
			info = new SimpleAuthenticationInfo(admin, admin.getKdtId(), getName());
		}
		
		if(info == null) throw new AccountException("SimpleAuthenticationInfo is null.");
		
		if(StrKit.notBlank(admin.getSalt())) info.setCredentialsSalt(ByteSource.Util.bytes(admin.getSalt()));
		
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
		SellerUser admin = ((SellerUser) principals.fromRealm(getName()).iterator().next());
		if(admin == null){
			SecurityUtils.getSubject().logout();
			return null;
		}
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		
		AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getSession().getAttribute(WeappConstants.WEB_WEAPP_IN_SESSION);
		if(authUser != null){
			Role role = roleService.getRoleBySeller(admin.getId(), authUser.getId());
			if(role != null)
				info.addRole(role.getName());
			
			List<Menu> menus = menuService.getMenusPerms(admin.getId(), authUser.getId());
			if(menus != null && menus.size()>0){
				for(Menu menu : menus){
					if(StrKit.notBlank(menu.getMenuUrl()))
						info.addStringPermission(menu.getMenuUrl());
				}				
			}
		}
		
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

	/* (non-Javadoc)
	 * @see org.apache.shiro.realm.AuthenticatingRealm#supports(org.apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken 
				|| token instanceof WechatLoginToken 
				|| token instanceof WeimoLoginToken
				|| token instanceof YouzanLoginToken;
	}

}
