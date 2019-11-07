package com.dbumama.market.service.provider;

import com.dbumama.market.model.SellerAddr;
import com.dbumama.market.service.api.SellerAddrService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class SellerAddrServiceImpl extends WxmServiceBase<SellerAddr> implements SellerAddrService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerAddrService#getSendAddr(java.lang.Long)
	 */
	@Override
	public SellerAddr getSendAddr(Long sellerId) {
		return DAO.findFirst("select * from " + SellerAddr.table + " where seller_id=? ", sellerId);
	}

}