package com.dbumama.market.service.provider;

import java.util.Date;

import com.dbumama.market.model.WeimoAppUser;
import com.dbumama.market.service.api.WeimoAppUserService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeimoAppUserServiceImpl extends WxmServiceBase<WeimoAppUser> implements WeimoAppUserService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeimoAppUserService#findWeimoAppUser(java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public WeimoAppUser findWeimoAppUser(Long sellerId, Long appId, String version, Date endDate) {
		return DAO.findFirst("select * from " + WeimoAppUser.table + " where seller_id=? and app_id=? and version=? and end_date=? ", sellerId, appId, version, endDate);
	}

}