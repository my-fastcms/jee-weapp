package com.dbumama.market.service.provider;

import com.dbumama.market.model.DeliverySet;
import com.dbumama.market.service.api.DeliverySetService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class DeliverySetServiceImpl extends WxmServiceBase<DeliverySet> implements DeliverySetService {

}