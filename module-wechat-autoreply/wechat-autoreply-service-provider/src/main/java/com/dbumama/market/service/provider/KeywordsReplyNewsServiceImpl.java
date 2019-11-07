package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.KeywordsReplyNews;
import com.dbumama.market.service.api.KeywordsReplyNewsService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class KeywordsReplyNewsServiceImpl extends WxmServiceBase<KeywordsReplyNews> implements KeywordsReplyNewsService {


	@Override
	public List<KeywordsReplyNews> findNewsByConfigId(Long configId) {
		Columns columns = Columns.create();
		columns.add(Column.create("keywords_config_id", configId));
		return DAO.findListByColumns(columns);
	}

}
