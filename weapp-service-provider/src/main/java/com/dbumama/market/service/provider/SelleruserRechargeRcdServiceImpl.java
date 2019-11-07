package com.dbumama.market.service.provider;

import com.dbumama.market.model.SelleruserRechargeRcd;
import com.dbumama.market.service.api.SelleruserRechargeRcdService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class SelleruserRechargeRcdServiceImpl extends WxmServiceBase<SelleruserRechargeRcd> implements SelleruserRechargeRcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SelleruserRechargeRcdService#findByTradeNo(java.lang.String)
	 */
	@Override
	public SelleruserRechargeRcd findByTradeNo(String tradeNo) {
		return DAO.findFirst("select * from " + SelleruserRechargeRcd.table + " where trade_no=? ", tradeNo);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SelleruserRechargeRcdService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<SelleruserRechargeRcd> list(Long sellerId, Integer pageNo, Integer pageSize) {
		Columns columns = new Columns();
		columns.add(Column.create("seller_id", sellerId));
		return DAO.paginateByColumns(pageNo, pageSize, columns, " created desc ");	
	}

}