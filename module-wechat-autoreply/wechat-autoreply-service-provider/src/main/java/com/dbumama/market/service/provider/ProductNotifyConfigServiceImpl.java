package com.dbumama.market.service.provider;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.ProductNotifyConfig;
import com.dbumama.market.model.ProductNotifyer;
import com.dbumama.market.service.api.ProductNotifyConfigService;
import com.dbumama.market.service.api.ProductNotifyerService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductNotifyConfigServiceImpl extends WxmServiceBase<ProductNotifyConfig> implements ProductNotifyConfigService {

	@Inject
	private ProductNotifyerService productNotifyerService;

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.ProductNotifyConfigService#findByShop(java.lang.Long)
	 */
	@Override
	public ProductNotifyConfig findByConfigType(Long shopId, String notifyType) {
		return DAO.findFirst("select * from " + ProductNotifyConfig.table + " where app_id=? and notify_type=? ", shopId, notifyType);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.ProductNotifyConfigService#save(com.dbumama.zanm.model.ProductNotifyConfig, java.lang.String)
	 */
	@Override
	@Before(Tx.class)
	public ProductNotifyConfig save(ProductNotifyConfig config, String notifyers) throws Exception {
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
		List<ProductNotifyer> oldProductNotifyers = productNotifyerService.findByProductConfig(config.getId());
		if(oldProductNotifyers !=null && oldProductNotifyers.size()>0){
			for(ProductNotifyer notifyer: oldProductNotifyers){
				notifyer.delete();
			}
		}
		
		for(int i=0;i<openIdsArr.size();i++){
			JSONObject json = openIdsArr.getJSONObject(i);
			Long notifyerId = json.getLong("notifyerId");
			if(notifyerId != null){
				ProductNotifyer notifyer = new ProductNotifyer();
				notifyer.setProductConfigId(config.getId())
				.setNotifyerId(notifyerId).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				notifyer.save();
			}
		}
		
		return config;
		
	}

}