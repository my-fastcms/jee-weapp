package com.dbumama.market.service.provider;

import com.dbumama.market.model.Shipping;
import com.dbumama.market.service.api.ShippingService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ShippingServiceImpl extends WxmServiceBase<Shipping> implements ShippingService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.ShippingService#findByOrder(java.lang.Long)
	 */
	@Override
	public Shipping findByOrder(Long orderId) {
		return DAO.findFirst("select * from " + Shipping.table + " where order_id=? ", orderId);
	}

}