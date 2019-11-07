package com.dbumama.market.service.provider;

import com.dbumama.market.model.ProductGroupSet;
import com.dbumama.market.service.api.ProductGroupSetService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductGroupSetServiceImpl extends WxmServiceBase<ProductGroupSet> implements ProductGroupSetService {

}