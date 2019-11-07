package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.OrderNotifyer;
import com.dbumama.market.service.api.OrderNotifyerService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class OrderNotifyerServiceImpl extends WxmServiceBase<OrderNotifyer> implements OrderNotifyerService {

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.OrderNotifyerService#findByOrderConfig(java.lang.Long)
	 */
	@Override
	public List<OrderNotifyer> findByOrderConfig(Long configId) {
		return DAO.find("select * from " + OrderNotifyer.table + " where order_config_id=? ", configId);
	}

}