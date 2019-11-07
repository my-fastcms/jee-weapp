package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.WeappStyleTabbarItem;
import com.dbumama.market.service.api.WeappStyleTabbarItemService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeappStyleTabbarItemServiceImpl extends WxmServiceBase<WeappStyleTabbarItem> implements WeappStyleTabbarItemService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeappStyleTabbarItemService#getStyleItems(java.lang.Long)
	 */
	@Override
	public List<WeappStyleTabbarItem> getStyleItems(Long styleId) {
		return DAO.find("select * from " + WeappStyleTabbarItem.table + " where style_id=? and active=1", styleId);
	}

}