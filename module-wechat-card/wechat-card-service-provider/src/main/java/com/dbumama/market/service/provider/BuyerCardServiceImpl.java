package com.dbumama.market.service.provider;

import com.dbumama.market.model.BuyerCard;
import com.dbumama.market.service.api.BuyerCardService;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class BuyerCardServiceImpl extends WxmServiceBase<BuyerCard> implements BuyerCardService {

}