package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.WeimoApp;
import com.dbumama.market.service.api.WeimoAppService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeimoAppServiceImpl extends WxmServiceBase<WeimoApp> implements WeimoAppService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeimoAppService#findList()
	 */
	@Override
	public List<WeimoApp> findList() {
		return DAO.find("select * from " + WeimoApp.table + " where active=1 ");
	}

}