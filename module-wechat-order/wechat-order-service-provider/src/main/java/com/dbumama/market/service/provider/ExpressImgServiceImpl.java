package com.dbumama.market.service.provider;

import com.dbumama.market.model.ExpressImg;
import com.dbumama.market.service.api.ExpressImgService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ExpressImgServiceImpl extends WxmServiceBase<ExpressImg> implements ExpressImgService {

}