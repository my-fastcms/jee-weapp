package com.dbumama.market.service.provider;

import com.dbumama.market.model.CashbackProduct;
import com.dbumama.market.service.api.CashbackProductService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class CashbackProductServiceImpl extends WxmServiceBase<CashbackProduct> implements CashbackProductService {

}