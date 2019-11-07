package com.dbumama.market.service.provider;

import com.dbumama.market.service.api.AppOrderService;
import com.dbumama.market.model.AppOrder;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class AppOrderServiceImpl extends WxmServiceBase<AppOrder> implements AppOrderService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AppOrderService#findByTradeNo(java.lang.String)
	 */
	@Override
	public AppOrder findByTradeNo(String tradeNO) {
		return DAO.findFirst("select * from " + AppOrder.table + " where trade_no=?", tradeNO);
	}

}