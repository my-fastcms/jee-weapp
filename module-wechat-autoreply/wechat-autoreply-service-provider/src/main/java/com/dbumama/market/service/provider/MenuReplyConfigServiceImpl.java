package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.MsgEventType;
import com.dbumama.market.service.enmu.MsgType;
import com.dbumama.weixin.api.CompCustomApi;
import com.dbumama.weixin.api.CompCustomApi.Articles;
import com.dbumama.weixin.api.CompMenuApi;
import com.dbumama.weixin.api.CompUserApi;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Bean
@RPCBean
public class MenuReplyConfigServiceImpl extends WxmServiceBase<MenuReplyConfig> implements MenuReplyConfigService {


	@Inject
	private MenuReplyNewsService menuReplyNewsService;
	@Inject
	private MenuReplyRuleService menuReplyRuleService;
	@Inject
	private MenuReplyRcdService menuReplyRcdService;
	@Inject
	private MenuNotifyRcdService menuNotifyRcdService;
	@Inject
	private AuthUserService authUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyConfigService#findMenuReplyConfig(java.lang.String)
	 */
	@Override
	public List<MenuReplyConfig> findMenuReplyConfig(Long shopId, String menuKey) {
		Columns columns = Columns.create();
		columns.add(Column.create("menu_key", menuKey));
		columns.add(Column.create("app_id", shopId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyConfigService#save(java.lang.String, java.lang.String)
	 */
	@Override
	@Before(Tx.class)
	public void save(Long shopId, String menuKey, String menuConfig, String replyRuleConfig) throws WxmallBaseException{
		if(shopId == null) throw new WxmallBaseException("shopId is null");
		if(StrKit.isBlank(menuKey)) throw new WxmallBaseException("menuKey is null");
		if(StrKit.isBlank(menuConfig)) throw new WxmallBaseException("menuConfig is null");
		if(StrKit.isBlank(replyRuleConfig)) throw new WxmallBaseException("replyRuleConfig is null");
		
		JSONObject ruleCfgJson = null;
		try {
			ruleCfgJson = JSONObject.parseObject(replyRuleConfig);
		} catch (Exception e) {
			throw new WxmallBaseException("replyRuleConfig json parse error");
		}
		
		if(ruleCfgJson == null) throw new WxmallBaseException("replyRuleConfig is null");
		
		Long id = ruleCfgJson.getLong("id");
		Integer ruleType = ruleCfgJson.getInteger("cfgType");
		if(ruleType == null) throw new WxmallBaseException("请选择回复规则类型");
		Integer expiresIn = ruleCfgJson.getInteger("expiresIn");
		if(ruleType == 3){
			if(expiresIn == null || expiresIn ==0){
				throw new WxmallBaseException("回复规则配置中，按时间间隔回复，请填写时间间隔值");
			}
		}
		
		MenuReplyRule menuReplyRule = menuReplyRuleService.findById(id);
		if(menuReplyRule == null){
			menuReplyRule = new MenuReplyRule();
			menuReplyRule.setMenuKey(menuKey).setAppId(shopId).setRuleType(ruleType).setExpiresIn(expiresIn).setCreated(new Date()).setUpdated(new Date()).setActive(true);
		}else{
			menuReplyRule.setRuleType(ruleType).setExpiresIn(expiresIn).setUpdated(new Date());
		}
		menuReplyRuleService.saveOrUpdate(menuReplyRule);
		
		JSONArray menuConfigArr = null;
		try {
			menuConfigArr = JSONArray.parseArray(menuConfig);
		} catch (Exception e) {
			throw new WxmallBaseException("menuConfig json parse error");
		}
		
		if(menuConfigArr == null || menuConfigArr.size() <=0)
			throw new WxmallBaseException("menuConfig arr is null or size == 0");
		
		//check//每次保存都删除旧的菜单回复配置
		List<MenuReplyConfig> replyConfigs = findMenuReplyConfig(shopId, menuKey);
		for(MenuReplyConfig replyConfig : replyConfigs){
			
			//删除菜单回复的图文消息配置
			List<MenuReplyNews> replyNews = menuReplyNewsService.findNewsByConfigId(replyConfig.getId());
			for(MenuReplyNews mrnews: replyNews){
				mrnews.delete();
			}
			
			//删除菜单回复的消息发送记录数据
			List<MenuReplyRcd> replyRcds = menuReplyRcdService.findReplRcdsByCfgId(replyConfig.getId());
			for(MenuReplyRcd rcd : replyRcds){
				rcd.delete();
			}
			
			//删除当前回复消息本身
			replyConfig.delete();
		}
		
		//保存新的菜单消息配置
		for(int i=0; i<menuConfigArr.size(); i++){
			JSONObject menucJsonObj = (JSONObject) menuConfigArr.get(i);
			
			Integer msgType = menucJsonObj.getInteger("msg_type");
			if(msgType == null) continue;
			
			if(msgType == MsgType.text.ordinal()){
				//纯文本
				MenuReplyConfig replyConfig = new MenuReplyConfig();
				String text = menucJsonObj.getString("msg_text_content");
				if(StrKit.isBlank(text)) throw new WxmallBaseException("纯文本消息，请输入消息文本内容");
				
				replyConfig.setMenuKey(menuKey).setAppId(shopId).setMsgType(MsgType.text.ordinal()).setMsgTextContent(menucJsonObj.getString("msg_text_content")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}else if(msgType == MsgType.news.ordinal()){
				//图文消息
				JSONArray replyNewsJsonArr = null;
				try {
					replyNewsJsonArr = menucJsonObj.getJSONArray("replyNews");
				} catch (Exception e) {
					throw new WxmallBaseException("有图文消息json parser error,请检查配置");
				}
				
				if(replyNewsJsonArr == null || replyNewsJsonArr.size() <=0){
					throw new WxmallBaseException("图文消息只要有一条数据,请检查配置");
				}
				
				MenuReplyConfig replyConfig = new MenuReplyConfig();
				replyConfig.setAppId(shopId).setMenuKey(menuKey).setMsgType(MsgType.news.ordinal()).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
				
				for(int k=0; k<replyNewsJsonArr.size();k++){
					JSONObject replyNewsJson = replyNewsJsonArr.getJSONObject(k);
					
					String msgTitle = replyNewsJson.getString("msg_title");
					String msgDesc = replyNewsJson.getString("msg_desc");
					String msgPic = replyNewsJson.getString("msg_pic");
					String msgUrl = replyNewsJson.getString("msg_url");
					Integer msgOrgType = replyNewsJson.getInteger("msg_org_type"); //商品，微信素材都可以当成图文消息发送，此字段用来区分
					
					if(StrKit.isBlank(msgTitle) || StrKit.isBlank(msgPic) || StrKit.isBlank(msgUrl)){
						throw new WxmallBaseException("图文消息有内容为空，请检查配置");
					}
					
					MenuReplyNews mreplyNews = new MenuReplyNews();
					mreplyNews.setReplyConfigId(replyConfig.getId()).setMsgTitle(msgTitle).setMsgDesc(msgDesc).setMsgPic(msgPic).setMsgUrl(msgUrl).setMsgOrgType(msgOrgType).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					mreplyNews.save();
				}
			}
		}
	}

	@Override
	@Before(Tx.class)
	public void save(Long authUserId, String menuConfigMap, String accessToken, String authAppId, String menus) throws WxmallBaseException {
		if(!StrKit.isBlank(menuConfigMap)){
		
			JSONObject elements = null;
			try {
				elements = JSONObject.parseObject(menuConfigMap);	
			} catch (Exception e) {
				throw new WxmallBaseException("elements json parse error");
			}
			
			if(elements == null || elements.size() <=0)
				throw new WxmallBaseException("elements arr is null or size == 0");
			
			//转为map，拿到对应的value
			Map<String,Object> ConfigMap = elements;
			String value = null;
			for (Map.Entry<String, Object> entry : ConfigMap.entrySet()) {
				   value = entry.getValue().toString();
			}
				 
			JSONArray clickConfigArr = null;
			try {
				clickConfigArr = JSONArray.parseArray(value);	
			} catch (Exception e) {
				throw new WxmallBaseException("clickConfigArr json parse error");
			}
			
			if(clickConfigArr == null || clickConfigArr.size() <=0)
				throw new WxmallBaseException("clickConfigArr arr is null or size == 0");
			
			for(int i = 0; i<clickConfigArr.size(); i++){
				JSONObject  clickJsonObj = (JSONObject) clickConfigArr.get(i);
				String key = clickJsonObj.getString("key");
				MenuReplyConfig replyConfig = findByKey(key,authUserId);
				if(replyConfig != null){
					//删除菜单回复的图文消息配置
					List<MenuReplyNews> replyNews = menuReplyNewsService.findNewsByConfigId(replyConfig.getId());
					for(MenuReplyNews mrnews: replyNews){
						mrnews.delete();
					}
					replyConfig.delete();
				}
				String valueObj = clickJsonObj.getString("value");
				
				JSONObject clickmenuObj = JSONObject.parseObject(valueObj);
				Integer msgType =  clickmenuObj.getInteger("msg_type");
				if(msgType == null) continue;
				if(msgType == MsgType.text.ordinal()){
					//纯文本
					replyConfig = new MenuReplyConfig();
					String text = clickmenuObj.getString("msg_text_content");
					if(StrKit.isBlank(text)) throw new WxmallBaseException("纯文本消息，请输入消息文本内容");
					replyConfig.setAppId(authUserId).setMenuKey(key).setMsgType(MsgType.text.ordinal()).setMsgTextContent(clickmenuObj.getString("msg_text_content")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					replyConfig.save();						
				}else if (msgType == MsgType.image.ordinal()){
					//图片
					replyConfig = new MenuReplyConfig();
					String mediaId = clickmenuObj.getString("media_id");
					if(StrKit.isBlank(mediaId)) throw new WxmallBaseException("图片不能为空");					
					replyConfig.setAppId(authUserId).setMenuKey(key).setMsgType(MsgType.image.ordinal()).setMediaId(clickmenuObj.getString("media_id")).setMediaPic(clickmenuObj.getString("media_pic")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					replyConfig.save();
				}else if(msgType == MsgType.news.ordinal()){
					//图文消息
					JSONArray replyNewsJsonArr = null;
					try {
						replyNewsJsonArr = clickmenuObj.getJSONArray("replyNews");
					} catch (Exception e) {
						throw new WxmallBaseException("有图文消息json parser error,请检查配置");
					}
					
					if(replyNewsJsonArr == null || replyNewsJsonArr.size() <=0){
						throw new WxmallBaseException("图文消息只要有一条数据,请检查配置");
					}
					
					replyConfig = new MenuReplyConfig();
					replyConfig.setAppId(authUserId).setMenuKey(key).setMsgType(MsgType.news.ordinal()).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					replyConfig.save();
	
					for(int k=0; k<replyNewsJsonArr.size();k++){
						JSONObject replyNewsJson = replyNewsJsonArr.getJSONObject(k);
						
						String msgTitle = replyNewsJson.getString("msg_title");
						String msgDesc = replyNewsJson.getString("msg_desc");
						String msgPic = replyNewsJson.getString("msg_pic");
						String msgUrl = replyNewsJson.getString("msg_url");
						String mediaId = replyNewsJson.getString("mediaid");
						Integer msgOrgType = replyNewsJson.getInteger("msg_org_type"); //商品，微信素材都可以当成图文消息发送，此字段用来区分
						
						if(StrKit.isBlank(msgTitle) || StrKit.isBlank(msgPic) || StrKit.isBlank(msgUrl)){
							throw new WxmallBaseException("图文消息有内容为空，请检查配置");
						}
						MenuReplyNews mreplyNews = new MenuReplyNews();
						mreplyNews.setReplyConfigId(replyConfig.getId()).setMediaId(mediaId).setMsgTitle(msgTitle).setMsgDesc(msgDesc).setMsgPic(msgPic).setMsgUrl(msgUrl).setMsgOrgType(msgOrgType).setCreated(new Date()).setUpdated(new Date()).setActive(true);
						mreplyNews.save();
						
					}
					
				}else if(msgType == MsgType.event.ordinal()){
					//事件触发
					replyConfig = new MenuReplyConfig();
					Integer eventType = clickmenuObj.getInteger("event_type");
					if(eventType == null) throw new WxmallBaseException("事件触发，请选择触发事件");
					replyConfig.setAppId(authUserId).setMenuKey(key).setMsgType(MsgType.event.ordinal()).setEventType(eventType).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					replyConfig.save();	
				}
			}
		}
		
		JSONArray jsonArray = JSONArray.parseArray(menus);
		JSONObject postData = new JSONObject();
		postData.put("button", jsonArray);
		ApiResult result = CompMenuApi.createMenu(accessToken, authAppId, postData.toJSONString());
		
		if(result.isAccessTokenInvalid()){
			throw new WxmallBaseException("请重新绑定公众号或者退出重新登录");
		}
		if(!result.isSucceed()){
			throw new WxmallBaseException("调用菜单接口失败，error:" + result.getErrorCode() + ",msg:" + result.getErrorMsg());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MenuReplyConfigService#findAllMenuReplyConfig(java.lang.Long, java.lang.String)
	 */
	@Override
	public List<MenuReplyConfigResDto> findAllMenuReplyConfig(Long shopId, String menuKey) {
		List<MenuReplyConfigResDto> menuReplyConfigDtos = new ArrayList<MenuReplyConfigResDto>();
		List<MenuReplyConfig> menuReplyConfigs = findMenuReplyConfig(shopId, menuKey);
		for(MenuReplyConfig replyConfig : menuReplyConfigs){
			
			MenuReplyConfigResDto replyCfgResDto = new MenuReplyConfigResDto();
			replyCfgResDto.setMenuReplyConfig(replyConfig);
			if(replyConfig.getMsgType() == MsgType.news.ordinal()){
				//图文消息
				List<MenuReplyNews> replyNews = menuReplyNewsService.findNewsByConfigId(replyConfig.getId());
				replyCfgResDto.setReplyNews(replyNews);
			}
			
			menuReplyConfigDtos.add(replyCfgResDto);
		}
		
		return menuReplyConfigDtos;
	}

	private MenuReplyConfig findByKey(String key, Long appId) {
		Columns columns = Columns.create();
		columns.add(Column.create("menu_key", key));
		columns.add(Column.create("app_id", appId));
		return DAO.findFirstByColumns(columns);	
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MenuReplyConfigService#reply(com.dbumama.market.model.AuthUser, java.lang.String)
	 */
	@Override
	public void reply(AuthUser authUser, String menuKey, String openId) {
		
		String accessToken = authUserService.getAccessToken(authUser);
		
		//加载菜单回复规则配置
		List<MenuReplyConfig> configs = findMenuReplyConfig(authUser.getId(), menuKey);
		if(configs != null && configs.size() >0){
			for(MenuReplyConfig config : configs){
				if(config.getMsgType() == MsgType.text.ordinal()){
					//发送菜单文本回复
					String text = config.getMsgTextContent();
					ApiResult res = CompUserApi.getUserInfo(accessToken, openId);
					final String nick = res.getStr("nickname");
					String newText = text.replaceAll("#微信昵称#", "@"+nick);
					CompCustomApi.sendText(accessToken, openId, newText, authUser.getAppId());
				}
				else if(config.getMsgType() == MsgType.image.ordinal()){
					CompCustomApi.sendImage(accessToken, openId, config.getMediaId(), authUser.getAppId());
				}
				else if(config.getMsgType() == MsgType.news.ordinal()){
					//图文消息
					List<Articles> articlesList = new ArrayList<Articles>();
					//查询出图文消息
					List<MenuReplyNews> replyNews = menuReplyNewsService.findNewsByConfigId(config.getId());
					for(MenuReplyNews menuReplyNews : replyNews){
						Articles articles = new Articles();
						articles.setTitle(menuReplyNews.getMsgTitle());
						articles.setDescription(menuReplyNews.getMsgDesc());
						articles.setPicurl(menuReplyNews.getMsgPic());
						articles.setUrl(menuReplyNews.getMsgUrl());
						articlesList.add(articles);
					}
					CompCustomApi.sendNews(accessToken, openId, articlesList, authUser.getAppId());
				}
				else if(config.getMsgType() == MsgType.event.ordinal()){
					if(config.getEventType() == MsgEventType.task.ordinal()){
					}else if(config.getEventType() == MsgEventType.jifen.ordinal()){
					}
					else if(config.getEventType() == MsgEventType.assisfree.ordinal()){
						List<Articles> articlesList = new ArrayList<Articles>();
						Articles articles = new Articles();
						articles.setTitle("助力免单!");
						articles.setDescription("\n点击进入助力商品列表，邀请好友完成助力，即可免费领取商品。\n");
						//articles.setPicurl("#(wxbctx)/resources/img/assisfree.jpg");
						String url = "http://"+authUser.getAppId()+".dbumama.com/assisfree";
						String shortUrl = authUserService.getShortUrl(authUser,url);
						articles.setUrl(shortUrl);
						articlesList.add(articles);
						CompCustomApi.sendNews(accessToken, openId, articlesList, authUser.getAppId());
					}
					//签到事件
					else if(config.getEventType() == MsgEventType.qiandao.ordinal()){

					}
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void _setReplyRcd(Long configId, String openId){
		MenuReplyRcd rcd = menuReplyRcdService.findRcdByOpenId(configId, openId);
		if(rcd == null){
			rcd = new MenuReplyRcd();
			rcd.setReplyConfigId(configId).setOpenId(openId).setReplyCount(1).setCreated(new Date()).setUpdated(new Date()).setActive(true);
			rcd.save();
		}else{
			rcd.setReplyCount(rcd.getReplyCount()+1).setUpdated(new Date());
			rcd.update();
		}
	}
	@SuppressWarnings("unused")
	private void _setNotifyRcd(Long notifyerId, String openId){
		MenuNotifyRcd rcd = menuNotifyRcdService.findByNotifyer(notifyerId, openId);
		if(rcd == null){
			rcd = new MenuNotifyRcd();
			rcd.setNotifyerId(notifyerId).setOpenId(openId).setNotifyCount(1).setCreated(new Date()).setUpdated(new Date()).setActive(true);
			rcd.save();
		}else{
			rcd.setNotifyCount(rcd.getNotifyCount()+1).setUpdated(new Date());
			rcd.update();
		}
	}
	@SuppressWarnings("unused")
	private boolean _checkNeedNotify(MenuNotifyRule ruleCfg, Long notifyerId, String openId){
		if(ruleCfg.getRuleType() == 4){
			//关闭通知
			return false;
		}else if(ruleCfg.getRuleType() == 2){
			//只回复一次
			MenuNotifyRcd rcd = menuNotifyRcdService.findByNotifyer(notifyerId, openId);
			if(rcd != null) return false;
		}else if(ruleCfg.getRuleType() == 3){
			//按时间间隔回复
			MenuNotifyRcd rcd = menuNotifyRcdService.findByNotifyer(notifyerId, openId);
			if(rcd == null) return true;
			
			Integer expiresIn = ruleCfg.getExpiresIn();//单位分钟
			if(expiresIn == null)
				return false;
			
			expiresIn = expiresIn * 60; //转换成秒
			
			Date expiredDate = rcd.getUpdated() == null ? rcd.getCreated() : rcd.getUpdated();
			
			Long expiredTime = expiredDate.getTime() + ((expiresIn -5) * 1000);
			
			if(System.currentTimeMillis() < expiredTime)
				return false;
		}
		return true;
	}
	@SuppressWarnings("unused")
	private boolean _checkNeedReply(MenuReplyRule ruleCfg, Long replyCfgId, String openId){
		if(ruleCfg.getRuleType() == 4){
			//关闭回复
			return false;
		}else if(ruleCfg.getRuleType() == 2){
			//只回复一次
			MenuReplyRcd rcd = menuReplyRcdService.findRcdByOpenId(replyCfgId, openId);
			if(rcd != null) return false;
		}else if(ruleCfg.getRuleType() == 3){
			//按时间间隔回复
			MenuReplyRcd rcd = menuReplyRcdService.findRcdByOpenId(replyCfgId, openId);
			if(rcd == null) return true;
			
			Integer expiresIn = ruleCfg.getExpiresIn();//单位分钟
			if(expiresIn == null)
				return false;
			
			expiresIn = expiresIn * 60; //转换成秒
			
			Date expiredDate = rcd.getUpdated() == null ? rcd.getCreated() : rcd.getUpdated();
			
			Long expiredTime = expiredDate.getTime() + ((expiresIn -5) * 1000);
			
			if(System.currentTimeMillis() < expiredTime)
				return false;
		}
		return true;
	}

}
