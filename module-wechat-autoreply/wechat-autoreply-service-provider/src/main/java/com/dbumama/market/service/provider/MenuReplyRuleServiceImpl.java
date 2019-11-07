package com.dbumama.market.service.provider;

import com.dbumama.market.model.MenuReplyRule;
import com.dbumama.market.service.api.MenuReplyRuleService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class MenuReplyRuleServiceImpl extends WxmServiceBase<MenuReplyRule> implements MenuReplyRuleService {

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyRuleService#findByMenuKey(java.lang.Long, java.lang.String)
	 */
	@Override
	public MenuReplyRule findByMenuKey(Long shopId, String menuKey) {
		Columns columns = Columns.create();
		columns.add(Column.create("menu_key", menuKey));
		columns.add(Column.create("app_id", shopId));
		columns.add(Column.create("active", 1));
		return DAO.findFirstByColumns(columns);
	}

}