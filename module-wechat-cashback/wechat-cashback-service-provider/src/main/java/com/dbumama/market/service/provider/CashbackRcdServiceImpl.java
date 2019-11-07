package com.dbumama.market.service.provider;

import com.dbumama.market.model.CashbackRcd;
import com.dbumama.market.service.api.CashbackRcdService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class CashbackRcdServiceImpl extends WxmServiceBase<CashbackRcd> implements CashbackRcdService {

}