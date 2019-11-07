package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.MemberRank;
import com.dbumama.market.service.api.CustomerException;
import com.dbumama.market.service.api.MemberRankService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MemberRankServiceImpl extends WxmServiceBase<MemberRank> implements MemberRankService {
	
	@Override
	public List<MemberRank> list(Long appid) throws CustomerException {
		return DAO.find("select * from " + MemberRank.table + " where app_id=? and active=1", appid);
	}

	@Override
	public List<MemberRank> getAppMemberRanks(Long appid) {
		return DAO.find("select * from " + MemberRank.table + " where app_id=?", appid);
	}
	
}