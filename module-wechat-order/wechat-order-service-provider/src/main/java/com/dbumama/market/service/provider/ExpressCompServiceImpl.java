package com.dbumama.market.service.provider;

import com.dbumama.market.model.ExpressComp;
import com.dbumama.market.service.api.ExpressCompService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ExpressCompServiceImpl extends WxmServiceBase<ExpressComp> implements ExpressCompService {

}