package com.dbumama.market.service.provider;

import com.dbumama.market.model.ProductImage;
import com.dbumama.market.service.api.ProductImageService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductImageServiceImpl extends WxmServiceBase<ProductImage> implements ProductImageService {

}