package com.dbumama.market.service.provider;

import com.dbumama.market.model.MenuReplyNews;
import com.dbumama.market.service.api.MenuReplyNewsService;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

import java.util.List;

@Bean
@RPCBean
public class MenuReplyNewsServiceImpl extends WxmServiceBase<MenuReplyNews> implements MenuReplyNewsService {

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyNewsService#findNewsByConfigId(java.lang.Long)
	 */
	@Override
	public List<MenuReplyNews> findNewsByConfigId(Long configId) {
		Columns columns = Columns.create();
		columns.add(Column.create("reply_config_id", configId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);
	}

}