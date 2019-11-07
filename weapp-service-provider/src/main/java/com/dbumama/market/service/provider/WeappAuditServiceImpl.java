package com.dbumama.market.service.provider;

import com.dbumama.market.model.WeappAudit;
import com.dbumama.market.service.api.WeappAuditService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeappAuditServiceImpl extends WxmServiceBase<WeappAudit> implements WeappAuditService {

}