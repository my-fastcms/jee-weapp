package com.dbumama.market.service.provider;

import com.dbumama.market.model.ProductSpecValue;
import com.dbumama.market.service.api.ProductSpecValueService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductSpecValueServiceImpl extends WxmServiceBase<ProductSpecValue> implements ProductSpecValueService {

}