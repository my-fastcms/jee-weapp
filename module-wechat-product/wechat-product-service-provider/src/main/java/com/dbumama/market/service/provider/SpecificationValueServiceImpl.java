package com.dbumama.market.service.provider;

import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.api.SpecificationValueService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class SpecificationValueServiceImpl extends WxmServiceBase<SpecificationValue> implements SpecificationValueService {

}