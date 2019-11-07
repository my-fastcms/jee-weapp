package com.dbumama.market.web.core.shiro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import io.jboot.Jboot;

/**
 * 密码重试认证
 * @author Rlax
 *
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
	
	/** shiro 密码重试缓存 */
	public static final String CACHE_SHIRO_PASSWORDRETRY = "shiro-passwordRetryCache";

	/** 允许密码重试最大次数 */
	private int allowRetryCount = 10;
	
	/** 账户将被锁定的时间 */
	private int lockedSeconds = 3600;

	@Override
	public boolean doCredentialsMatch(AuthenticationToken _token, AuthenticationInfo info) {
		
		if(_token instanceof WechatLoginToken){
			return true;
		}
		
		if(_token instanceof WeimoLoginToken){
			return true;
		}
		
		if(_token instanceof YouzanLoginToken){
			return true;
		}
		
		UsernamePasswordToken token = (UsernamePasswordToken) _token;
		
		String username = (String) token.getPrincipal();
		AtomicInteger atomicInteger = Jboot.getCache().get(CACHE_SHIRO_PASSWORDRETRY, username);

		if (atomicInteger == null) {
			atomicInteger = new AtomicInteger(0);
		} else {
			atomicInteger.incrementAndGet();
		}
		Jboot.getCache().put(CACHE_SHIRO_PASSWORDRETRY, username, atomicInteger, lockedSeconds);

		if (atomicInteger.get() > allowRetryCount) {
			throw new ExcessiveAttemptsException("账户密码错误次数过多，账户已被限制登录1小时");
		}

		boolean matches = super.doCredentialsMatch(token, info);
		
		if (matches) {
			Jboot.getCache().remove(CACHE_SHIRO_PASSWORDRETRY, username);
			atomicInteger = new AtomicInteger(0);
		}
		
		if(atomicInteger.get()>=4 ){
			throw new ExcessiveAttemptsException("您还有" + (allowRetryCount - atomicInteger.get()) + "次错误密码输入机会，账户将会被限制登录");
		}
		
		return matches;
	}

	public int getAllowRetryCount() {
		return allowRetryCount;
	}

	public void setAllowRetryCount(int allowRetryCount) {
		this.allowRetryCount = allowRetryCount;
	}

	public int getLockedSeconds() {
		return lockedSeconds;
	}

	public void setLockedSeconds(int lockedSeconds) {
		this.lockedSeconds = lockedSeconds;
	}
}
