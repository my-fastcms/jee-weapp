package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.FollowConfig;
import com.dbumama.market.model.FollowReplyConfig;
import com.dbumama.market.model.FollowReplyNews;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.FollowConfigService;
import com.dbumama.market.service.api.FollowReplyConfigService;
import com.dbumama.market.service.api.FollowReplyNewsService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.MsgType;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.dbumama.weixin.api.CompCustomApi;
import com.dbumama.weixin.api.CompCustomApi.Articles;
import com.dbumama.weixin.api.CompUserApi;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class FollowConfigServiceImpl extends WxmServiceBase<FollowConfig> implements FollowConfigService {

	@Inject
	private FollowReplyConfigService followReplyConfigService;
	
	@Inject
	private AuthUserService authUserService;
	
	@Inject
	private FollowReplyNewsService followReplyNewsService;
	
	@Override
	public FollowConfig findByAppId(Long appId) {
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", appId));
		columns.add(Column.create("active", 1));
		return DAO.findFirstByColumns(columns);	
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.FollowConfigService#reply(com.dbumama.market.model.AuthUser)
	 */
	@Override
	public void reply(AuthUser authUser, String openid) {
		
		if(authUser == null || StrKit.isBlank(openid)) return;
		
		final String accessToken = authUserService.getAccessToken(authUser);
		
		FollowConfig followConfig = findByAppId(authUser.getId());
		if(followConfig !=null && followConfig.getEnableConfig() != null && followConfig.getEnableConfig() == true){
			List<FollowReplyConfig> followReplyConfigs = followReplyConfigService.findConfigByFollowId(followConfig.getId());
			for(FollowReplyConfig config : followReplyConfigs){
				if(config.getMsgType() == null) continue;
				
				if(config.getMsgType() == MsgType.text.ordinal()){
					//发送菜单文本回复
					String text = Matcher.quoteReplacement(config.getMsgTextContent());
					ApiResult res = CompUserApi.getUserInfo(accessToken, openid);
					final String nick = res.getStr("nickname");
					String newText = text.replaceAll("#微信昵称#", "@"+nick);
					CompCustomApi.sendText(accessToken, openid, newText, authUser.getAppId());
				}else if(config.getMsgType() == MsgType.news.ordinal()){
					//图文消息
					List<Articles> articlesList = new ArrayList<Articles>();
					//查询出图文消息
					List<FollowReplyNews> replyNews = followReplyNewsService.findNewsByConfigId(config.getId());
					for(FollowReplyNews followReplyNews : replyNews){
						Articles articles = new Articles();
						articles.setTitle(followReplyNews.getMsgTitle());
						articles.setDescription(followReplyNews.getMsgDesc());
						articles.setPicurl(followReplyNews.getMsgPic());
						articles.setUrl(followReplyNews.getMsgUrl());
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
						String nick = res.getStr("nickname");
						
						if(StrKit.isBlank(nick)) continue;
						
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
					//回复小程序
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