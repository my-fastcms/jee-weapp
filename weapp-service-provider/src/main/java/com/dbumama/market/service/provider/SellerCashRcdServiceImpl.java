package com.dbumama.market.service.provider;

import com.dbumama.market.model.SellerCashRcd;
import com.dbumama.market.service.api.SellerCashRcdService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class SellerCashRcdServiceImpl extends WxmServiceBase<SellerCashRcd> implements SellerCashRcdService {

}