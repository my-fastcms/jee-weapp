package com.dbumama.market.service.provider;

import com.dbumama.market.service.api.MarketcodeCodeactiveRcdService;

import java.util.List;

import com.dbumama.market.model.MarketcodeCodeactiveRcd;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MarketcodeCodeactiveRcdServiceImpl extends WxmServiceBase<MarketcodeCodeactiveRcd> implements MarketcodeCodeactiveRcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeCodeactiveRcdService#findByAppCodeIndex(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public MarketcodeCodeactiveRcd findByAppCodeIndex(Long codeactiveId, Long codeStart, Long codeEnd) {
		return DAO.findFirst("select * from " + MarketcodeCodeactiveRcd.table + " where codeactive_id=? and code_start=? and code_end=? ", codeactiveId, codeStart, codeEnd);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeCodeactiveRcdService#findByCodeactiveId(java.lang.Long)
	 */
	@Override
	public List<MarketcodeCodeactiveRcd> findByCodeactiveId(Long codeactiveId) {
		return DAO.find("select * from " + MarketcodeCodeactiveRcd.table + " where codeactive_id=? ", codeactiveId);
	}

}