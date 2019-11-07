package com.dbumama.market.web.controller;

import com.dbumama.market.service.api.PromotionService;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "promotion")
public class PromotionController extends BaseApiController {

    @RPCInject
    private PromotionService promotionService;

    public void list(){
        List<Record> productResultDtos = promotionService.getPromotionServiceMini(getAuthUserId(), getPageNo(), getPageSize());
        rendSuccessJson(productResultDtos);
    }

}
