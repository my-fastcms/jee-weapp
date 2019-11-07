package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.Keywords;
import com.dbumama.market.model.KeywordsReplyConfig;
import com.dbumama.market.model.KeywordsReplyNews;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.KeywordsReplyConfigService;
import com.dbumama.market.service.api.KeywordsReplyNewsService;
import com.dbumama.market.service.api.KeywordsService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.MsgType;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.dbumama.weixin.api.CompCustomApi;
import com.dbumama.weixin.api.CompCustomApi.Articles;
import com.dbumama.weixin.api.CompTagApi;
import com.dbumama.weixin.api.CompUserApi;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class KeywordsServiceImpl extends WxmServiceBase<Keywords> implements KeywordsService {

	@Inject
	private KeywordsReplyNewsService keywordsReplyNewsService;
	
	@Inject
	private KeywordsReplyConfigService keywordsReplyConfigService;
	
	@Inject
	private AuthUserService authUserService;
	
	@Override
	public List<Keywords> findByAppId(Long shopId) {
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", shopId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);	
	}
	
	@Override
	public Keywords save(Long appId, String keywordsText, String keywordsConfig, Long keywordsId, Boolean enableKeywords, Integer autoTagid, Integer cancelTagid) throws Exception {
		if(appId == null) throw new Exception("shopId is null");
		if(StrKit.isBlank(keywordsText)) throw new Exception("keywordsText is null");
		if(StrKit.isBlank(keywordsConfig)) throw new Exception("keywordsConfig is null");
		if(enableKeywords == null) throw new Exception("enableKeywords is null");
		
		JSONArray keywordsConfigArr = null;
		try {
			keywordsConfigArr = JSONArray.parseArray(keywordsConfig);
		} catch (Exception e) {
			throw new Exception("keywordsConfig json parse error");
		}
		
		if(keywordsConfigArr == null || keywordsConfigArr.size() <=0)
			throw new Exception("keywordsConfig arr is null or size == 0");
		
		Keywords keyword = findById(keywordsId);
		//修改关键字，删除旧的回复配置
		//保存新的关键字
		if(keyword == null){
			keyword = new Keywords();
			keyword.setAppId(appId).setKeywords(keywordsText).setCreated(new Date()).setUpdated(new Date()).setActive(true).setEnableKeywords(enableKeywords);
			keyword.save();
		}
		
		keyword.setAutoTagId(autoTagid).setCancelTagId(cancelTagid);
		
		//删除旧的菜单回复配置
		List<KeywordsReplyConfig> replyConfigs = keywordsReplyConfigService.findKeywordsByKeywordsId(keyword.getId());
		for(KeywordsReplyConfig mrconfig: replyConfigs){
			//删除菜单回复的图文消息配置
			List<KeywordsReplyNews> replyNews = keywordsReplyNewsService.findNewsByConfigId(mrconfig.getId());
			for(KeywordsReplyNews mrnews: replyNews){
				mrnews.delete();
			}
			mrconfig.delete();
		}
		keyword.setKeywords(keywordsText).setUpdated(new Date()).setEnableKeywords(enableKeywords);
		keyword.update();
		
		
		//保存新的菜单消息配置
		for(int i=0; i<keywordsConfigArr.size(); i++){
			JSONObject keywordscJsonObj = (JSONObject) keywordsConfigArr.get(i);
			
			Integer msgType = keywordscJsonObj.getInteger("msg_type");
			if(msgType == null) continue;
			
			if(msgType == MsgType.text.ordinal()){
				//纯文本
				KeywordsReplyConfig replyConfig = new KeywordsReplyConfig();
				String text = keywordscJsonObj.getString("msg_text_content");
				if(StrKit.isBlank(text)) throw new Exception("纯文本消息，请输入消息文本内容");
				
				replyConfig.setKeywordsId(keyword.getId()).setMsgType(MsgType.text.ordinal()).setMsgTextContent(keywordscJsonObj.getString("msg_text_content")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}else if (msgType == MsgType.image.ordinal()){
				KeywordsReplyConfig replyConfig = new KeywordsReplyConfig();
				String mediaId = keywordscJsonObj.getString("media_id");
				if(StrKit.isBlank(mediaId)) throw new Exception("图片不能为空");
				
				replyConfig.setKeywordsId(keyword.getId()).setMsgType(MsgType.image.ordinal()).setMediaId(keywordscJsonObj.getString("media_id")).setMediaPic(keywordscJsonObj.getString("media_pic")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}else if(msgType == MsgType.news.ordinal()){
				//图文消息
				JSONArray replyNewsJsonArr = null;
				try {
					replyNewsJsonArr = keywordscJsonObj.getJSONArray("replyNews");
				} catch (Exception e) {
					throw new Exception("有图文消息json parser error,请检查配置");
				}
				
				if(replyNewsJsonArr == null || replyNewsJsonArr.size() <=0){
					throw new Exception("图文消息只要有一条数据,请检查配置");
				}
				
				KeywordsReplyConfig replyConfig = new KeywordsReplyConfig();
				replyConfig.setKeywordsId(keyword.getId()).setMsgType(MsgType.news.ordinal()).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
				
				for(int k=0; k<replyNewsJsonArr.size();k++){
					JSONObject replyNewsJson = replyNewsJsonArr.getJSONObject(k);
					
					String msgTitle = replyNewsJson.getString("msg_title");
					String msgDesc = replyNewsJson.getString("msg_desc");
					String msgPic = replyNewsJson.getString("msg_pic");
					String msgUrl = replyNewsJson.getString("msg_url");
					Integer msgOrgType = replyNewsJson.getInteger("msg_org_type"); //商品，微信素材都可以当成图文消息发送，此字段用来区分
					
					if(StrKit.isBlank(msgTitle) || StrKit.isBlank(msgPic) || StrKit.isBlank(msgUrl)){
						throw new Exception("图文消息有内容为空，请检查配置");
					}
					
					KeywordsReplyNews mreplyNews = new KeywordsReplyNews();
					mreplyNews.setKeywordsConfigId(replyConfig.getId()).setMsgTitle(msgTitle).setMsgDesc(msgDesc).setMsgPic(msgPic).setMsgUrl(msgUrl).setMsgOrgType(msgOrgType).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					mreplyNews.save();
				}
				
			}else if(msgType == MsgType.miniprogrampage.ordinal()){
				KeywordsReplyConfig replyConfig = new KeywordsReplyConfig();
				String minAppId = keywordscJsonObj.getString("app_id");
				String appPath = keywordscJsonObj.getString("app_path");
				String title = keywordscJsonObj.getString("title");
				String thumbMediaId = keywordscJsonObj.getString("thumb_media_id");
				String thumbMediaPic = keywordscJsonObj.getString("thumb_media_pic");
				if(StrKit.isBlank(minAppId)) throw new Exception("小程序Appid不能为空");
				if(StrKit.isBlank(appPath)) throw new Exception("小程序路径不能为空");
				if(StrKit.isBlank(title)) throw new Exception("小程序标题不能为空");
				if(StrKit.isBlank(thumbMediaId)) throw new Exception("小程序封面图片不能为空");
				if(StrKit.isBlank(thumbMediaPic)) throw new Exception("小程序封面图片不能为空");
				
				replyConfig.setKeywordsId(keyword.getId()).setMsgType(MsgType.miniprogrampage.ordinal()).setAppId(keywordscJsonObj.getString("app_id")).setAppPath(keywordscJsonObj.getString("app_path")).setTitle(keywordscJsonObj.getString("title")).setMediaId(keywordscJsonObj.getString("thumb_media_id")).setMediaPic(keywordscJsonObj.getString("thumb_media_pic")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}
			
		}
		return keyword;
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.KeywordsService#findShopKeywordsByText(java.lang.Long, java.lang.String)
	 */
	@Override
	public Keywords findKeywordsByText(Long appId, String keywords) {
		return DAO.findFirst("select * from " + Keywords.table + " where app_id=? and keywords=?", appId, keywords);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.KeywordsService#reply(com.dbumama.market.model.AuthUser, java.lang.String)
	 */
	@Override
	public void reply(AuthUser authUser, String keywordsText, String openid) throws WxmallBaseException {
		final String accessToken = authUserService.getAccessToken(authUser);
		Keywords keywords = findKeywordsByText(authUser.getId(), keywordsText);
		if(keywords != null && keywords.getEnableKeywords() != null && keywords.getEnableKeywords() == true){
			
			//自动打标签
			if(keywords.getAutoTagId() != null){
				List<String> openIdList = new ArrayList<String>();
				openIdList.add(openid);
				CompTagApi.batchAddTag(keywords.getAutoTagId(), openIdList, accessToken);	
			}
			
			if(keywords.getCancelTagId() != null){
				List<String> openIdList = new ArrayList<String>();
				openIdList.add(openid);
				CompTagApi.batchDelTag(keywords.getCancelTagId(), openIdList, accessToken);				
			}
			
			//加载自动回复配置
			List<KeywordsReplyConfig> keywordsReplyCfgs = keywordsReplyConfigService.findKeywordsByKeywordsId(keywords.getId());
			
			for(KeywordsReplyConfig config : keywordsReplyCfgs){
				if(config.getMsgType() == MsgType.text.ordinal()){
					//文本回复
					String text = config.getMsgTextContent();
					ApiResult res = CompUserApi.getUserInfo(accessToken, openid);
					final String nick = res.getStr("nickname");
					String newText = text.replaceAll("#微信昵称#", "@"+nick);
					CompCustomApi.sendText(accessToken, openid, newText, authUser.getAppId());
				}else if(config.getMsgType() == MsgType.news.ordinal()){
					//图文消息
					List<Articles> articlesList = new ArrayList<Articles>();
					//查询出图文消息
					List<KeywordsReplyNews> replyNews = keywordsReplyNewsService.findNewsByConfigId(config.getId());
					for(KeywordsReplyNews menuReplyNews : replyNews){
						Articles articles = new Articles();
						articles.setTitle(menuReplyNews.getMsgTitle());
						articles.setDescription(menuReplyNews.getMsgDesc());
						articles.setPicurl(menuReplyNews.getMsgPic());
						articles.setUrl(menuReplyNews.getMsgUrl());
						articlesList.add(articles);
					}

					boolean hasNick = false;
					for(Articles articles : articlesList){
						if(articles.getTitle().contains("#微信昵称#")){
							hasNick = true;
							break;
						}
					}
					
					if(hasNick){
						ApiResult res = CompUserApi.getUserInfo(accessToken, openid);
						final String nick = res.getStr("nickname");
						
						List<Articles> newArticlesList = new ArrayList<Articles>();
						for(Articles articles : articlesList){
							if(articles.getTitle().contains("#微信昵称#")){
								Articles newArticles = new Articles();
								String title = articles.getTitle();
								String newTitle = title.replaceAll("#微信昵称#", "@"+nick);
								newArticles.setTitle(newTitle);
								newArticles.setDescription(articles.getDescription());
								newArticles.setPicurl(articles.getPicurl());
								newArticles.setUrl(articles.getUrl());
								newArticlesList.add(newArticles);
							}else{
								newArticlesList.add(articles);
							}
						}
						
						CompCustomApi.sendNews(accessToken, openid, newArticlesList, authUser.getAppId());
					}else{
						CompCustomApi.sendNews(accessToken, openid, articlesList, authUser.getAppId());
					}
				}else if(config.getMsgType() == MsgType.image.ordinal()){
					//回复图片，声音等
					CompCustomApi.sendImage(accessToken, openid, config.getMediaId(), authUser.getAppId());
				}else if(config.getMsgType() == MsgType.miniprogrampage.ordinal()){
					if(StrKit.notBlank(config.getTitle()) 
							&& StrKit.notBlank(config.getAppId())
							&& StrKit.notBlank(config.getAppPath())
							&& StrKit.notBlank(config.getMediaId())){
						CompCustomApi.sendMiniprog(accessToken, openid, config.getTitle(), config.getAppId(), config.getAppPath(), config.getMediaId(), authUser.getAppId());						
					}
				}
			}
		}		
	}

	

}