package com.dbumama.market.service.provider;

import com.dbumama.market.model.OrderPressRcd;
import com.dbumama.market.service.api.OrderPressRcdService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class OrderPressRcdServiceImpl extends WxmServiceBase<OrderPressRcd> implements OrderPressRcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderPressRcdService#findByOrderId(java.lang.Long)
	 */
	@Override
	public OrderPressRcd findByOrderId(Long orderId) {
		return DAO.findFirst("select * from " + OrderPressRcd.table + " where order_id=? ", orderId);
	}

}