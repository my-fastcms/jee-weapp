package com.dbumama.market.service.provider;

import com.dbumama.market.model.WeipageCategory;
import com.dbumama.market.service.api.WeipageCategoryService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeipageCategoryServiceImpl extends WxmServiceBase<WeipageCategory> implements WeipageCategoryService {

}