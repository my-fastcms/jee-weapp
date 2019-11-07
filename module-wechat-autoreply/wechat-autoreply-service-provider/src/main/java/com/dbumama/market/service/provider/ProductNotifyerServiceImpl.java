package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.ProductNotifyer;
import com.dbumama.market.service.api.ProductNotifyerService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductNotifyerServiceImpl extends WxmServiceBase<ProductNotifyer> implements ProductNotifyerService {

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.ProductNotifyerService#findByProductConfig(java.lang.Long)
	 */
	@Override
	public List<ProductNotifyer> findByProductConfig(Long configId) {
		return DAO.find("select * from " + ProductNotifyer.table + " where product_config_id=? ", configId);
	}

}