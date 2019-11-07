package com.dbumama.market.service.provider;

import com.dbumama.market.model.MultiGroupSet;
import com.dbumama.market.service.api.MultiGroupSetService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MultiGroupSetServiceImpl extends WxmServiceBase<MultiGroupSet> implements MultiGroupSetService {

}