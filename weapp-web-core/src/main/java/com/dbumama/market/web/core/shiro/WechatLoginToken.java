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
package com.dbumama.market.web.core.shiro;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * 支持微信第三方登陆
 * @author wangjun
 * 2018年6月13日
 */
@SuppressWarnings("serial")
public class WechatLoginToken implements HostAuthenticationToken, RememberMeAuthenticationToken{
	
	private String openid;
	
	private String accessToken;
	
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
     * Whether or not 'rememberMe' should be enabled for the corresponding login attempt;
     * default is <code>false</code>
     */
    private boolean rememberMe = false;

    /**
     * The location from where the login attempt occurs, or <code>null</code> if not known or explicitly
     * omitted.
     */
    private String host;
    
    public WechatLoginToken(){}
    
    public WechatLoginToken(final String openid, final String accessToken, final boolean rememberMe, final String host) {
		this.openid = openid;
		this.accessToken = accessToken;
		this.rememberMe = rememberMe;
		this.host = host;
	}
    
    public WechatLoginToken(final String openid, final String accessToken) {
		this(openid, accessToken, false, null);
	}
	
	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}

	/**
	 * @param openid the openid to set
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}

	/**
	 * @param rememberMe the rememberMe to set
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.authc.AuthenticationToken#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return getOpenid();
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.authc.AuthenticationToken#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return getAccessToken();
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.authc.RememberMeAuthenticationToken#isRememberMe()
	 */
	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.authc.HostAuthenticationToken#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}
	
	/**
     * Clears out (nulls) the username, password, rememberMe, and inetAddress.  The password bytes are explicitly set to
     * <tt>0x00</tt> before nulling to eliminate the possibility of memory access at a later time.
     */
    public void clear() {
        this.openid = null;
        this.host = null;
        this.accessToken = null;
    }

    /**
     * Returns the String representation.  It does not include the password in the resulting
     * string for security reasons to prevent accidentially printing out a password
     * that might be widely viewable).
     *
     * @return the String representation of the <tt>UsernamePasswordToken</tt>, omitting
     *         the password.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" - ");
        sb.append(openid);
        sb.append(", rememberMe=").append(rememberMe);
        if (host != null) {
            sb.append(" (").append(host).append(")");
        }
        return sb.toString();
    }

}
