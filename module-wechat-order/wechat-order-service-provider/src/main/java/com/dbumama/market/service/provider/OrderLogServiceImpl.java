package com.dbumama.market.service.provider;

import com.dbumama.market.model.OrderLog;
import com.dbumama.market.service.api.OrderLogService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class OrderLogServiceImpl extends WxmServiceBase<OrderLog> implements OrderLogService {

}