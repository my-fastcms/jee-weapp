package com.dbumama.market.service.provider;

import com.dbumama.market.service.api.AuthShopOrderService;
import com.dbumama.market.model.AuthShopOrder;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class AuthShopOrderServiceImpl extends WxmServiceBase<AuthShopOrder> implements AuthShopOrderService {

}