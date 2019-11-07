package com.dbumama.market.service.provider;

import com.dbumama.market.model.PurchaseOrder;
import com.dbumama.market.service.api.PurchaseOrderService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class PurchaseOrderServiceImpl extends WxmServiceBase<PurchaseOrder> implements PurchaseOrderService {

}