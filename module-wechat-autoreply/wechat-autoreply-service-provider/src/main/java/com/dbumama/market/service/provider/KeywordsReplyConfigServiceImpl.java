package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.KeywordsReplyConfig;
import com.dbumama.market.model.KeywordsReplyNews;
import com.dbumama.market.service.api.KeywordsReplyConfigResDto;
import com.dbumama.market.service.api.KeywordsReplyConfigService;
import com.dbumama.market.service.api.KeywordsReplyNewsService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.MsgType;
import com.jfinal.aop.Inject;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class KeywordsReplyConfigServiceImpl extends WxmServiceBase<KeywordsReplyConfig> implements KeywordsReplyConfigService {

	@Inject
	private KeywordsReplyNewsService keywordsReplyNewsService;
	
	
	
	@Override
	public List<KeywordsReplyConfig> findKeywordsByKeywordsId(Long keywordsId) {
		Columns columns = Columns.create();
		columns.add(Column.create("keywords_id", keywordsId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);
	}

	@Override
	public List<KeywordsReplyConfigResDto> findAllKeywordsReplyConfig(Long keywordsId) {
		List<KeywordsReplyConfigResDto> keywordsReplyConfigDtos = new ArrayList<KeywordsReplyConfigResDto>();
		
		List<KeywordsReplyConfig> keywordsReplyConfigs = findKeywordsByKeywordsId(keywordsId);
		for(KeywordsReplyConfig replyConfig : keywordsReplyConfigs){
			KeywordsReplyConfigResDto replyCfgResDto = new KeywordsReplyConfigResDto();
			replyCfgResDto.setKeywordsReplyConfig(replyConfig);	
			if(replyConfig.getMsgType() == MsgType.news.ordinal()){
				//图文消息
				List<KeywordsReplyNews> replyNews = keywordsReplyNewsService.findNewsByConfigId(replyConfig.getId());
				replyCfgResDto.setReplyNews(replyNews);
			}
			keywordsReplyConfigDtos.add(replyCfgResDto);
		}
		return keywordsReplyConfigDtos;
	}


}
