package com.dbumama.market.service.provider;

import com.dbumama.market.model.ProductBrand;
import com.dbumama.market.service.api.ProductBrandService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductBrandServiceImpl extends WxmServiceBase<ProductBrand> implements ProductBrandService {

}