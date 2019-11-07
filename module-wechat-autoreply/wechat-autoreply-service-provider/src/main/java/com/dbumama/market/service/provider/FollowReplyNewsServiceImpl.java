package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.FollowReplyNews;
import com.dbumama.market.service.api.FollowReplyNewsService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class FollowReplyNewsServiceImpl extends WxmServiceBase<FollowReplyNews> implements FollowReplyNewsService {

	@Override
	public List<FollowReplyNews> findNewsByConfigId(Long configId) {
		Columns columns = Columns.create();
		columns.add(Column.create("follow_config_id", configId));
		return DAO.findListByColumns(columns);
	}

}