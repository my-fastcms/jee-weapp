package com.dbumama.market.service.provider;

import com.dbumama.market.model.Refund;
import com.dbumama.market.service.api.RefundService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class RefundServiceImpl extends WxmServiceBase<Refund> implements RefundService {

}