package com.dbumama.market.service.provider;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.MenuNotifyConfig;
import com.dbumama.market.model.MenuNotifyRule;
import com.dbumama.market.service.api.MenuNotifyConfigService;
import com.dbumama.market.service.api.MenuNotifyRuleService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class MenuNotifyConfigServiceImpl extends WxmServiceBase<MenuNotifyConfig> implements MenuNotifyConfigService {

	@Inject
	private MenuNotifyRuleService menuNotifyRuleService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyConfigService#findByMenuKey(java.lang.Long, java.lang.String)
	 */
	@Override
	public List<MenuNotifyConfig> findByMenuKey(Long shopId, String menuKey) {
		Columns columns = Columns.create(Column.create("app_id", shopId));
		columns.add(Column.create("menu_key", menuKey));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyConfigService#findByOpenId(java.lang.Long, java.lang.String)
	 */
	@Override
	public List<MenuNotifyConfig> findByOpenId(Long shopId, String openId) {
		Columns columns = Columns.create(Column.create("app_id", shopId));
		columns.add(Column.create("open_id", openId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyConfigService#save(java.lang.Long, java.lang.String, java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyerService#save(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Before(Tx.class)
	public void save(Long shopId, String menuKey, String openids, String notifyRuleConfig) throws Exception{
		if(shopId == null) throw new Exception("shopId is null");
		if(StrKit.isBlank(menuKey)) throw new Exception("menuKey is null");
		if(StrKit.isBlank(openids)) throw new Exception("openids is null");
		if(StrKit.isBlank(notifyRuleConfig)) throw new Exception("notifyRuleConfig is null");
		
		JSONObject ruleCfgJson = null;
		try {
			ruleCfgJson = JSONObject.parseObject(notifyRuleConfig);
		} catch (Exception e) {
			throw new Exception("replyRuleConfig json parse error");
		}
		
		if(ruleCfgJson == null) throw new Exception("replyRuleConfig is null");
		
		Long id = ruleCfgJson.getLong("id");
		Integer ruleType = ruleCfgJson.getInteger("cfgType");
		if(ruleType == null) throw new Exception("请选择回复规则类型");
		Integer expiresIn = ruleCfgJson.getInteger("expiresIn");
		if(ruleType == 3){
			if(expiresIn == null || expiresIn ==0){
				throw new Exception("回复规则配置中，按时间间隔回复，请填写时间间隔值");
			}
		}
		
		MenuNotifyRule menuNotifyRule = menuNotifyRuleService.findById(id);
		if(menuNotifyRule == null){
			menuNotifyRule = new MenuNotifyRule();
			menuNotifyRule.setMenuKey(menuKey).setAppId(shopId).setRuleType(ruleType).setExpiresIn(expiresIn).setCreated(new Date()).setUpdated(new Date()).setActive(true);
		}else{
			menuNotifyRule.setRuleType(ruleType).setExpiresIn(expiresIn).setUpdated(new Date());
		}
		menuNotifyRuleService.saveOrUpdate(menuNotifyRule);
		
		JSONArray openIdsArr = null;
		try {
			openIdsArr = JSONArray.parseArray(openids);
		} catch (Exception e) {
			throw new Exception("openids json parse error");
		}
		
		if(openIdsArr == null || openIdsArr.size() <=0)
			throw new Exception("openIds arr is null or size == 0");
		
		//检查该菜单旧的通知接收者数据
		List<MenuNotifyConfig> menuNotifyCfgs = findByMenuKey(shopId, menuKey);
		for(MenuNotifyConfig notifycfg : menuNotifyCfgs){
			notifycfg.delete();
		}
		
		for(int i=0; i<openIdsArr.size(); i++){
			JSONObject openIdJson = openIdsArr.getJSONObject(i);
			Long notifyerId = openIdJson.getLong("notifyerId");
			if(notifyerId != null){
				MenuNotifyConfig notifyCfg = new MenuNotifyConfig();
				notifyCfg.setAppId(shopId).setMenuKey(menuKey).setNotifyerId(notifyerId).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				notifyCfg.save();
			}
		}
		
	}

}