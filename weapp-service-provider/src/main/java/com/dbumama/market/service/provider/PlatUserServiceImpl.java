package com.dbumama.market.service.provider;

import com.dbumama.market.model.PlatUser;
import com.dbumama.market.service.api.PlatUserService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class PlatUserServiceImpl extends WxmServiceBase<PlatUser> implements PlatUserService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.user.PlatUserService#findByAccount(java.lang.String)
	 */
	@Override
	public PlatUser findByAccount(String account) {
		return DAO.findFirst("select * from " + PlatUser.table + " where account=?", account);
	}
	
}