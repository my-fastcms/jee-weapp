package com.dbumama.market.service.provider;

import com.dbumama.market.model.FullCutProduct;
import com.dbumama.market.service.api.FullCutProductService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class FullCutProductServiceImpl extends WxmServiceBase<FullCutProduct> implements FullCutProductService {

}