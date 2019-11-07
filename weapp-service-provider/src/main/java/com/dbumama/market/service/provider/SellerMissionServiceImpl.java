package com.dbumama.market.service.provider;

import com.dbumama.market.model.SellerMission;
import com.dbumama.market.service.api.SellerMissionService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class SellerMissionServiceImpl extends WxmServiceBase<SellerMission> implements SellerMissionService {

}