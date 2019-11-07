package com.dbumama.market.service.provider;

import com.dbumama.market.model.MenuEvent;
import com.dbumama.market.service.api.MenuEventService;
import com.dbumama.market.service.base.WxmServiceBase;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MenuEventServiceImpl extends WxmServiceBase<MenuEvent> implements MenuEventService {

}