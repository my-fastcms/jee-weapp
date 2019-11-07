package com.dbumama.market.service.provider;

import com.dbumama.market.model.WeappTester;
import com.dbumama.market.service.api.WeappTesterService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeappTesterServiceImpl extends WxmServiceBase<WeappTester> implements WeappTesterService {

}