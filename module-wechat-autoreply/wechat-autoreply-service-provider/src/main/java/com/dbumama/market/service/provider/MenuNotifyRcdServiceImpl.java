package com.dbumama.market.service.provider;

import com.dbumama.market.model.MenuNotifyRcd;
import com.dbumama.market.service.api.MenuNotifyRcdService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class MenuNotifyRcdServiceImpl extends WxmServiceBase<MenuNotifyRcd> implements MenuNotifyRcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyRcdService#findByNotifyer(java.lang.Long, java.lang.String)
	 */
	@Override
	public MenuNotifyRcd findByNotifyer(Long notifyerId, String openId) {
		Columns columns = Columns.create();
		columns.add(Column.create("notifyer_id", notifyerId));
		columns.add(Column.create("open_id", openId));
		columns.add(Column.create("active", 1));
		return DAO.findFirstByColumns(columns);
	}

}