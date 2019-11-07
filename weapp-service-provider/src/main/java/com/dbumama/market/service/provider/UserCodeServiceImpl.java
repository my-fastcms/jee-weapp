package com.dbumama.market.service.provider;

import com.dbumama.market.model.UserCode;
import com.dbumama.market.service.api.UserCodeService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class UserCodeServiceImpl extends WxmServiceBase<UserCode> implements UserCodeService {

}