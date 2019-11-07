package com.dbumama.market.service.provider;

import com.dbumama.market.model.SelleruserBalanceRcd;
import com.dbumama.market.service.api.SelleruserBalanceRcdService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class SelleruserBalanceRcdServiceImpl extends WxmServiceBase<SelleruserBalanceRcd> implements SelleruserBalanceRcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SelleruserBalanceRcdService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<SelleruserBalanceRcd> list(Long sellerId, Integer pageNo, Integer pageSize) {
		Columns columns = new Columns();
		columns.add(Column.create("seller_id", sellerId));
		return DAO.paginateByColumns(pageNo, pageSize, columns, " created desc ");	
	}

}