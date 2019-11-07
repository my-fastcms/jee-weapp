package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.Shop;
import com.dbumama.market.service.api.ShopService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class ShopServiceImpl extends WxmServiceBase<Shop> implements ShopService {
	
	@Override
	public Shop findByApp(Long appId) {
		return DAO.findFirst(" select * from " + Shop.table + " where app_id=? ", appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.ShopService#list(java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<Shop> list(Long appId, Integer pageNo, Integer pageSize, Integer active) {
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", appId));
		columns.add(Column.create("active", active));
		return DAO.paginateByColumns(pageNo, pageSize, columns, " updated desc ");
	}

	@Override
	public List<Shop> getShopByAppId(Long appId) {
		List<Shop> list = DAO.find("select * from " + Shop.table + " where app_id=? and active=1 order by id desc", appId);
		return list;
	}

}