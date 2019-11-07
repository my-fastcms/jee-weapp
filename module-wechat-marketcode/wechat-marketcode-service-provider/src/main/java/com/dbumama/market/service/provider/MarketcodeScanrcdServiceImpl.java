package com.dbumama.market.service.provider;

import com.dbumama.market.service.api.MarketcodeScanrcdService;
import com.dbumama.market.model.MarketcodeScanrcd;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MarketcodeScanrcdServiceImpl extends WxmServiceBase<MarketcodeScanrcd> implements MarketcodeScanrcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeScanrcdService#findByIsvAppAndCode(java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public MarketcodeScanrcd findByIsvAppAndCode(String isvApplicationId, Integer applicationId, String code) {
		return DAO.findFirst("select * from " + MarketcodeScanrcd.table 
				+ " where isv_application_id=? and application_id=? and code=?", 
				isvApplicationId, applicationId, code);
	}

}