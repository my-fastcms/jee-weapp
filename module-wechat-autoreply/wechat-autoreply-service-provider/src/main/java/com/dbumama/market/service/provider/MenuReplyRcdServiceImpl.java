package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.MenuReplyRcd;
import com.dbumama.market.service.api.MenuReplyRcdService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class MenuReplyRcdServiceImpl extends WxmServiceBase<MenuReplyRcd> implements MenuReplyRcdService {

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyRcdService#findRcdByOpenId(java.lang.Long, java.lang.String)
	 */
	@Override
	public MenuReplyRcd findRcdByOpenId(Long replyCfigId, String openId) {
		Columns columns = Columns.create();
		columns.add(Column.create("reply_config_id", replyCfigId));
		columns.add(Column.create("open_id", openId));
		columns.add(Column.create("active", 1));
		return DAO.findFirstByColumns(columns);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyRcdService#findReplRcdsByCfgId(java.lang.Long)
	 */
	@Override
	public List<MenuReplyRcd> findReplRcdsByCfgId(Long configId) {
		Columns columns = Columns.create();
		columns.add(Column.create("reply_config_id", configId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);
	}

}