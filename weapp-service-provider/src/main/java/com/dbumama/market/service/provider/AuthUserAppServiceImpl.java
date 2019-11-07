package com.dbumama.market.service.provider;

import java.util.Date;
import java.util.List;

import com.dbumama.market.model.AuthUserApp;
import com.dbumama.market.service.api.AuthUserAppService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class AuthUserAppServiceImpl extends WxmServiceBase<AuthUserApp> implements AuthUserAppService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserAppService#findByApp(java.lang.Long, java.lang.Long)
	 */
	@Override
	public AuthUserApp findByApp(Long authUserId, Long appId) {
		return DAO.findFirst("select * from " + AuthUserApp.table + " where auth_user_id=? and app_id=? and active=1 ", authUserId, appId);
	}

	@Override
	public List<AuthUserApp> list(Long authUserId) {
		return DAO.find("select id,app_id,end_date from "+AuthUserApp.table+" where auth_user_id = ? and end_date >= ? and active = 1 ", authUserId,new Date());
	}

}