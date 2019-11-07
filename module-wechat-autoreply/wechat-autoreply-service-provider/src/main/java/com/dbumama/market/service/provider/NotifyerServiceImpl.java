package com.dbumama.market.service.provider;

import com.dbumama.market.model.MenuNotifyConfig;
import com.dbumama.market.model.Notifyer;
import com.dbumama.market.model.OrderNotifyer;
import com.dbumama.market.model.ProductNotifyer;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
@RPCBean
public class NotifyerServiceImpl extends WxmServiceBase<Notifyer> implements NotifyerService {

	@Inject
	private MenuNotifyConfigService menuNotifyConfigService;
	@Inject
	private OrderNotifyerService orderNotifyerService;
	@Inject
	private ProductNotifyerService productNotifyerService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyerService#findByOpenId(java.lang.Long, java.lang.String)
	 */
	@Override
	public Notifyer findByOpenId(Long shopId, String openId) {
		return findByOpenId(shopId, openId, 1);
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyerService#findByOpenId(java.lang.Long, java.lang.String, java.lang.Integer)
	 */
	@Override
	public Notifyer findByOpenId(Long shopId, String openId, Integer active) {
		Columns columns = Columns.create();
		columns.add(Column.create("openid", openId));
		columns.add(Column.create("app_id", shopId));
		columns.add(Column.create("active", active));
		return DAO.findFirstByColumns(columns);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyerService#findByShop(java.lang.Long)
	 */
	@Override
	public List<Notifyer> findByShop(Long appId) {
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", appId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns, " created desc ");
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyerService#findByMenuKey(java.lang.Long, java.lang.String)
	 */
	@Override
	public List<NotifyerResDto> findByMenuKey(Long shopId, String menuKey) {
		List<Notifyer> shopNotifyers = findByShop(shopId);
		
		List<MenuNotifyConfig> menuCfgs = menuNotifyConfigService.findByMenuKey(shopId, menuKey);
		
		List<NotifyerResDto> notifyerResDtos = new ArrayList<NotifyerResDto>();
		
		for(Notifyer notifyer : shopNotifyers){
			NotifyerResDto notifyerRes = new NotifyerResDto();
			notifyerRes.setId(notifyer.getId());
			notifyerRes.setNickname(notifyer.getNickname());
			notifyerRes.setHeadimgurl(notifyer.getHeadimgurl());
			notifyerRes.setOpenid(notifyer.getOpenid());
			
			for(MenuNotifyConfig cfg : menuCfgs){
				if(notifyer.getId().intValue() == cfg.getNotifyerId().intValue()){
					notifyerRes.setChecked(true);
					break;
				}
			}
			
			notifyerResDtos.add(notifyerRes);
		}
		
		return notifyerResDtos;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuNotifyerService#findAllTimeoutNotifyers()
	 */
	@Override
	public List<Notifyer> findAllTimeoutNotifyers() {
		List<Notifyer> notifyerList = new ArrayList<Notifyer>();
		
		//提醒快超过48小时的通知接收者，与公众号互动一次，刷新状态
		List<Notifyer> notifyerDbList = DAO.find("select * from " + Notifyer.table + " where active=1");
		for(Notifyer notifyer : notifyerDbList){
			Date expiredDate = notifyer.getUpdated() == null ? notifyer.getCreated() : notifyer.getUpdated();
			Integer expiresIn = 48*60*60; //转换成秒
			
			Long expiredTime = expiredDate.getTime() + ((expiresIn -5) * 1000);
			
			//与当前时间差
			long currTime = System.currentTimeMillis();
			
			int diffTime = (int) ((expiredTime.longValue() - currTime) / 1000 / 60);
			
			if(diffTime<=30){//包含已过48小时未与公众号互动的数据
				notifyerList.add(notifyer);
			}
			
		}
		
		return notifyerList;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.NotifyerService#findByOrderType(java.lang.Long, java.lang.String)
	 */
	@Override
	public List<NotifyerResDto> findByOrderNotifyConfig(Long shopId, Long configId) {
		List<Notifyer> shopNotifyers = findByShop(shopId);
		
		List<OrderNotifyer> orderNotifyers = orderNotifyerService.findByOrderConfig(configId);
		
		List<NotifyerResDto> notifyerResDtos = new ArrayList<NotifyerResDto>();
		
		for(Notifyer notifyer : shopNotifyers){
			NotifyerResDto notifyerRes = new NotifyerResDto();
			notifyerRes.setId(notifyer.getId());
			notifyerRes.setNickname(notifyer.getNickname());
			notifyerRes.setHeadimgurl(notifyer.getHeadimgurl());
			notifyerRes.setOpenid(notifyer.getOpenid());
			
			for(OrderNotifyer cfg : orderNotifyers){
				if(notifyer.getId().intValue() == cfg.getNotifyerId().intValue()){
					notifyerRes.setChecked(true);
					break;
				}
			}
			
			notifyerResDtos.add(notifyerRes);
		}
		
		return notifyerResDtos;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.NotifyerService#findByProductNotifyConfig(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<NotifyerResDto> findByProductNotifyConfig(Long shopId, Long configId) {
		List<Notifyer> shopNotifyers = findByShop(shopId);
		
		List<ProductNotifyer> productNotifyers = productNotifyerService.findByProductConfig(configId);
		
		List<NotifyerResDto> notifyerResDtos = new ArrayList<NotifyerResDto>();
		
		for(Notifyer notifyer : shopNotifyers){
			NotifyerResDto notifyerRes = new NotifyerResDto();
			notifyerRes.setId(notifyer.getId());
			notifyerRes.setNickname(notifyer.getNickname());
			notifyerRes.setHeadimgurl(notifyer.getHeadimgurl());
			notifyerRes.setOpenid(notifyer.getOpenid());
			
			for(ProductNotifyer cfg : productNotifyers){
				if(notifyer.getId().intValue() == cfg.getNotifyerId().intValue()){
					notifyerRes.setChecked(true);
					break;
				}
			}
			
			notifyerResDtos.add(notifyerRes);
		}
		
		return notifyerResDtos;
	}

}