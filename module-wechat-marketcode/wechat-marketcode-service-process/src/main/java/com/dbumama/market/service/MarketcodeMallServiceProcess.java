package com.dbumama.market.service;

import com.dbumama.market.model.MarketcodeCodeactive;
import com.dbumama.market.service.api.MarketcodeException;

/**
 * 一物一码微信商城
 */
public class MarketcodeMallServiceProcess implements MarketcodeService{
    @Override
    public void doProcess(String wxaAppId, Integer applicationId, String isvApplicationId, String openid, String code) throws MarketcodeException {

    }

    @Override
    public String render(MarketcodeCodeactive marketcodeCodeactive) throws MarketcodeException {
        return null;
    }
}
