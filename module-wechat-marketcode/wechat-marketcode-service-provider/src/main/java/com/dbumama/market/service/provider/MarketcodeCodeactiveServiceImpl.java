package com.dbumama.market.service.provider;

import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.AuthUserTemplate;
import com.dbumama.market.model.MarketcodeApply;
import com.dbumama.market.model.MarketcodeCodeactive;
import com.dbumama.market.model.MarketcodeCodeactiveRcd;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.AuthUserTemplateService;
import com.dbumama.market.service.api.MarketcodeApplyService;
import com.dbumama.market.service.api.MarketcodeCodeactiveRcdService;
import com.dbumama.market.service.api.MarketcodeCodeactiveService;
import com.dbumama.market.service.api.MarketcodeException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.dbumama.weixin.api.CompMarketcodeApi;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class MarketcodeCodeactiveServiceImpl extends WxmServiceBase<MarketcodeCodeactive> implements MarketcodeCodeactiveService {

	@Inject
	private AuthUserService authUserService;
	@Inject
	private MarketcodeApplyService marketcodeApplyService;
	@Inject
	private MarketcodeCodeactiveRcdService marketcodeCodeactiveRcdService;
	@Inject
	private AuthUserTemplateService authUserTemplateService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeCodeactiveService#codeactive(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Long, java.lang.Long)
	 */
	@Override
	@Before(Tx.class)
	public void codeactive(Long id, Long appId, String applicationId, String activityName, String productBrand,
			String productTitle, String productCode, String wxaAppid, String wxaPath, Integer wxaType) throws MarketcodeException {
		
		MarketcodeApply entry = marketcodeApplyService.findByAppApplicationId(appId, applicationId);
        if(entry ==null){
        	throw new MarketcodeException("激活前请先去微信申请生成二维码");
        }
        final AuthUser authUser = authUserService.findById(appId);
        if(authUser == null) throw new MarketcodeException("authUser is null");
		final String accessToken = authUserService.getAccessToken(authUser);
		if(StrKit.isBlank(accessToken)) throw new MarketcodeException("accessToken is null");

		MarketcodeCodeactive _mcca = findByWxaAppId(wxaAppid);
		if(_mcca != null && _mcca.getAppId().intValue() != appId.intValue()){
			AuthUser _mccaAuthUser = authUserService.findById(_mcca.getAppId()); 
			throw new MarketcodeException("该小程序已经在公众号["+_mccaAuthUser.getNickName()+"]上存在激活记录，请解除之后方可在新的公众号上激活");
		}
		
		final AuthUser weapp = authUserService.findByAppId(wxaAppid);
		if(weapp == null) throw new MarketcodeException("系统异常，小程序不存在");
		
		//检查该小程序是否设置过模板
		AuthUserTemplate authUserTemplate = authUserTemplateService.getAuthUserTemplate(weapp.getId());
		if(authUserTemplate == null) throw new MarketcodeException("该小程序未设置模板，请先设置模板，方可激活");
		
        final ApiResult apiResult = marketcodeApplyService.applyStatus(entry.getAppId(), entry.getApplicationId(), entry.getIsvApplicationId());
    	if(!apiResult.isSucceed() && StrKit.isBlank(apiResult.getStr("status"))){
    		throw new MarketcodeException("error code:" + apiResult.getErrorCode() + ",error msg:" + apiResult.getErrorMsg());
    	}
    	if(!"FINISH".equals(apiResult.getStr("status"))){
			throw new MarketcodeException("二维码生成完成才可以进行激活操作");
		}
    	
    	MarketcodeCodeactive codeActiveEntry = findById(id);
        if(codeActiveEntry == null){
        	codeActiveEntry = new MarketcodeCodeactive();
        	codeActiveEntry.setCreated(new Date());
        	codeActiveEntry.setAppId(appId);
        	codeActiveEntry.setActive(true);
        }
        codeActiveEntry.setApplicationId(applicationId);
        codeActiveEntry.setActivityName(activityName);
        codeActiveEntry.setProductBrand(productBrand);
        codeActiveEntry.setProductTitle(productTitle);
        codeActiveEntry.setProductCode(productCode);
        codeActiveEntry.setWxaAppid(wxaAppid);
        codeActiveEntry.setWxaPath(wxaPath);
        codeActiveEntry.setWxaType(wxaType);
        codeActiveEntry.setUpdated(new Date());
        saveOrUpdate(codeActiveEntry);
    	
    	JSONArray code_generate_list = apiResult.get("code_generate_list");
		for(int i=0;i<code_generate_list.size();i++){
			JSONObject json = code_generate_list.getJSONObject(i);
			final Long codeStart = json.getLong("code_start");
			final Long codeEnd = json.getLong("code_end");
			ApiResult codeActiveApiResult = CompMarketcodeApi.codeActive(accessToken, applicationId, activityName, productBrand, productTitle, productCode, wxaAppid, wxaPath, wxaType, codeStart, codeEnd);
			if(!codeActiveApiResult.isSucceed()){
				throw new MarketcodeException("error code:" + codeActiveApiResult.getErrorCode() + ",error msg:" + codeActiveApiResult.getErrorMsg());
			}
			MarketcodeCodeactiveRcd marketcodeActiveRcd = marketcodeCodeactiveRcdService.findByAppCodeIndex(codeActiveEntry.getId(), codeStart, codeEnd);
			if(marketcodeActiveRcd == null){
				marketcodeActiveRcd = new MarketcodeCodeactiveRcd();
				marketcodeActiveRcd.setCodeactiveId(codeActiveEntry.getId())
				.setApplicationId(codeActiveEntry.getApplicationId()).setCreated(new Date());
			}
			marketcodeActiveRcd.setCodeStart(codeStart.intValue()).setCodeEnd(codeEnd.intValue()).setActive(true).setUpdated(new Date());
			marketcodeCodeactiveRcdService.saveOrUpdate(marketcodeActiveRcd);
		}
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeCodeactiveService#findByAppApplicationId(java.lang.Long, java.lang.String)
	 */
	@Override
	public MarketcodeCodeactive findByAppApplicationId(Long appId, String applicationId) {
		return DAO.findFirst("select * from " + MarketcodeCodeactive.table + " where app_id=? and application_id=?", appId, applicationId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeCodeactiveService#findByWxaAppApplicationId(java.lang.String, java.lang.String)
	 */
	@Override
	public MarketcodeCodeactive findByWxaAppApplicationId(String wxaAppId, Integer applicationId) {
		return DAO.findFirst("select * from " + MarketcodeCodeactive.table + " where application_id=? and wxa_appid=? ", applicationId, wxaAppId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeCodeactiveService#findByWxaAppId(java.lang.String)
	 */
	@Override
	public MarketcodeCodeactive findByWxaAppId(String wxaAppId) {
		return DAO.findFirst("select * from " + MarketcodeCodeactive.table + " where wxa_appid=? ", wxaAppId);
	}

}