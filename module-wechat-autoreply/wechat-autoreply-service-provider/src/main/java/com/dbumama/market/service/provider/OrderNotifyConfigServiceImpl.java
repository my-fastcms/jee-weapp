package com.dbumama.market.service.provider;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.OrderNotifyConfig;
import com.dbumama.market.model.OrderNotifyer;
import com.dbumama.market.service.api.OrderNotifyConfigService;
import com.dbumama.market.service.api.OrderNotifyerService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class OrderNotifyConfigServiceImpl extends WxmServiceBase<OrderNotifyConfig> implements OrderNotifyConfigService {
	
	@Inject
	private OrderNotifyerService orderNotifyerService;

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.OrderNotifyConfigService#findByShop(java.lang.Long)
	 */
	@Override
	public OrderNotifyConfig findByConfigType(Long appId, String notifyType) {
		return DAO.findFirst("select * from " + OrderNotifyConfig.table + " where app_id=? and notify_type=? ", appId, notifyType);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.OrderNotifyConfigService#save(com.dbumama.zanm.model.OrderNotifyConfig, java.lang.String)
	 */
	@Override
	@Before(Tx.class)
	public OrderNotifyConfig save(OrderNotifyConfig config, String notifyers) throws Exception {
		if(config == null) throw new Exception("config is null");
		if(StrKit.isBlank(notifyers)) throw new Exception("notifyers is null");
		
		JSONArray openIdsArr = null;
		try {
			openIdsArr = JSONArray.parseArray(notifyers);
		} catch (Exception e) {
			throw new Exception("notifyers json parse error");
		}
		
		if(openIdsArr == null || openIdsArr.size() <=0)
			throw new Exception("notifyers arr is null or size == 0");
		
		saveOrUpdate(config);
		
		//删除原来的通知者配置
		List<OrderNotifyer> olderNotifyers = orderNotifyerService.findByOrderConfig(config.getId());
		if(olderNotifyers !=null && olderNotifyers.size()>0){
			for(OrderNotifyer notifyer: olderNotifyers){
				notifyer.delete();
			}
		}
		
		for(int i=0;i<openIdsArr.size();i++){
			JSONObject json = openIdsArr.getJSONObject(i);
			Long notifyerId = json.getLong("notifyerId");
			if(notifyerId != null){
				OrderNotifyer notifyer = new OrderNotifyer();
				notifyer.setOrderConfigId(config.getId())
				.setNotifyerId(notifyerId).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				notifyer.save();
			}
		}
		
		return config;
		
	}

}