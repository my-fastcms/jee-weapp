package com.dbumama.market.service.provider;

import com.dbumama.market.model.PromotionSet;
import com.dbumama.market.service.api.PromotionSetService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class PromotionSetServiceImpl extends WxmServiceBase<PromotionSet> implements PromotionSetService {

}