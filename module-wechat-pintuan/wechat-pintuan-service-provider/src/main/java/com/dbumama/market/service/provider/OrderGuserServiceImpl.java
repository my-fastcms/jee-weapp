package com.dbumama.market.service.provider;

import com.dbumama.market.model.OrderGuser;
import com.dbumama.market.service.api.OrderGuserService;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class OrderGuserServiceImpl extends WxmServiceBase<OrderGuser> implements OrderGuserService {

}