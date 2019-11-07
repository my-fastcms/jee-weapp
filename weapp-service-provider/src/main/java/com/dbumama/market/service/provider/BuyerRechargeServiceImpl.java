package com.dbumama.market.service.provider;

import com.dbumama.market.model.BuyerRecharge;
import com.dbumama.market.service.api.BuyerRechargeService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class BuyerRechargeServiceImpl extends WxmServiceBase<BuyerRecharge> implements BuyerRechargeService {

}