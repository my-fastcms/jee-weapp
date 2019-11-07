package com.dbumama.market.service.provider;

import com.dbumama.market.model.CompTicket;
import com.dbumama.market.service.api.CompTicketService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class CompTicketServiceImpl extends WxmServiceBase<CompTicket> implements CompTicketService {

}