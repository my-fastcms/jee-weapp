package com.dbumama.market.service.provider;

import com.dbumama.market.model.AuthUserStyle;
import com.dbumama.market.model.WeappStyle;
import com.dbumama.market.service.api.AuthUserStyleService;
import com.dbumama.market.service.api.WeappStyleService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class AuthUserStyleServiceImpl extends WxmServiceBase<AuthUserStyle> implements AuthUserStyleService {

	@Inject
	private WeappStyleService weappStyleService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserStyleService#getAuthUserStyle(java.lang.Long)
	 */
	@Override
	public WeappStyle getAuthUserStyle(Long authUserId) {
		
		AuthUserStyle userStyle = DAO.findFirst("select * from " + AuthUserStyle.table + " where app_id=? ", authUserId);
		
		if(userStyle == null || userStyle.getStyleId() == null) return null;
		
		return weappStyleService.findById(userStyle.getStyleId());
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserStyleService#getAuthUserStyleByAppandStyle(java.lang.Long, java.lang.Long)
	 */
	@Override
	public AuthUserStyle getAuthUserStyleByAppandStyle(Long authAppId) {
		return DAO.findFirst("select * from " + AuthUserStyle.table + " where app_id=?", authAppId);
	}

}