package com.dbumama.market.service.provider;

import com.dbumama.market.model.RefundError;
import com.dbumama.market.service.api.RefundErrorService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class RefundErrorServiceImpl extends WxmServiceBase<RefundError> implements RefundErrorService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RefundErrorService#findByOrderId(java.lang.Long)
	 */
	@Override
	public RefundError findByOrderId(Long orderId) {
		return DAO.findFirst("select * from " + RefundError.table + " where order_id=? ", orderId);
	}

}