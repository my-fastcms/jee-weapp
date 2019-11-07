package com.dbumama.market.service.provider;

import com.dbumama.market.model.FullCutSet;
import com.dbumama.market.service.api.FullCutSetService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class FullCutSetServiceImpl extends WxmServiceBase<FullCutSet> implements FullCutSetService {

}