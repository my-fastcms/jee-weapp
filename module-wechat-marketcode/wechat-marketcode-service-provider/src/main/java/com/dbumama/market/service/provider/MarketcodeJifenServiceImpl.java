package com.dbumama.market.service.provider;

import com.dbumama.market.service.api.MarketcodeJifenService;
import com.dbumama.market.model.MarketcodeJifen;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MarketcodeJifenServiceImpl extends WxmServiceBase<MarketcodeJifen> implements MarketcodeJifenService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeJifenService#findByCodeactive(java.lang.Long)
	 */
	@Override
	public MarketcodeJifen findByCodeactive(Long codeactiveId) {
		return DAO.findFirst("select * from " + MarketcodeJifen.table + " where codeactive_id=? ", codeactiveId);
	}

}