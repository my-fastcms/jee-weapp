package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.FollowConfig;
import com.dbumama.market.model.FollowReplyConfig;
import com.dbumama.market.model.FollowReplyNews;
import com.dbumama.market.service.api.FollowConfigService;
import com.dbumama.market.service.api.FollowReplyConfigResDto;
import com.dbumama.market.service.api.FollowReplyConfigService;
import com.dbumama.market.service.api.FollowReplyNewsService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.MsgType;
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
public class FollowReplyConfigServiceImpl extends WxmServiceBase<FollowReplyConfig> implements FollowReplyConfigService {

	@Inject
	private FollowReplyNewsService followReplyNewsService;
	
	@Inject
	private FollowConfigService followConfigService;
	
	@Override
	@Before(Tx.class)
	public void save(Long shopId, String followConfig, Boolean enableConfig) throws WxmallBaseException {
		if(StrKit.isBlank(followConfig)) throw new WxmallBaseException("followConfig is null");
		if(enableConfig == null) throw new WxmallBaseException("enableConfig is null");
		
		JSONArray followConfigArr = null;
		try {
			followConfigArr = JSONArray.parseArray(followConfig);
		} catch (Exception e) {
			throw new WxmallBaseException("followConfig json parse error");
		}
		
		if(followConfigArr == null || followConfigArr.size() <=0)
			throw new WxmallBaseException("followConfig arr is null or size == 0");
		
		//删除配置启用停用状态
		FollowConfig follow =followConfigService.findByAppId(shopId);
		if(follow == null){
			follow = new FollowConfig();
			follow.setEnableConfig(enableConfig).setAppId(shopId).setCreated(new Date()).setUpdated(new Date()).setActive(true);
			follow.save();
		}else{
			follow.setEnableConfig(enableConfig).setUpdated(new Date());
			follow.update();
		}
		
		//check//每次保存都删除旧的菜单回复配置
		List<FollowReplyConfig> replyConfigs = findConfigByFollowId(follow.getId());
		for(FollowReplyConfig replyConfig : replyConfigs){
			
			//删除菜单回复的图文消息配置
			List<FollowReplyNews> replyNews = followReplyNewsService.findNewsByConfigId(replyConfig.getId());
			for(FollowReplyNews mrnews: replyNews){
				mrnews.delete();
			}
			
			//删除当前回复消息本身
			replyConfig.delete();
		}
		
		//保存新的菜单消息配置
		for(int i=0; i<followConfigArr.size(); i++){
			JSONObject followcJsonObj = (JSONObject) followConfigArr.get(i);
			
			Integer msgType = followcJsonObj.getInteger("msg_type");

			if(msgType == null) continue;
			
			if(msgType == MsgType.text.ordinal()){
				//纯文本
				FollowReplyConfig replyConfig = new FollowReplyConfig();
				String text = followcJsonObj.getString("msg_text_content");
				if(StrKit.isBlank(text)) throw new WxmallBaseException("纯文本消息，请输入消息文本内容");
				
				replyConfig.setFollowId(follow.getId()).setMsgType(MsgType.text.ordinal()).setMsgTextContent(followcJsonObj.getString("msg_text_content")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}else if (msgType == MsgType.image.ordinal()){
				FollowReplyConfig replyConfig = new FollowReplyConfig();
				String mediaId = followcJsonObj.getString("media_id");
				if(StrKit.isBlank(mediaId)) throw new WxmallBaseException("图片不能为空");
				
				replyConfig.setFollowId(follow.getId()).setMsgType(MsgType.image.ordinal()).setMediaId(followcJsonObj.getString("media_id")).setMediaPic(followcJsonObj.getString("media_pic")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}else if(msgType == MsgType.news.ordinal()){
				//图文消息
				JSONArray replyNewsJsonArr = null;
				try {
					replyNewsJsonArr = followcJsonObj.getJSONArray("replyNews");
				} catch (Exception e) {
					throw new WxmallBaseException("有图文消息json parser error,请检查配置");
				}
				
				if(replyNewsJsonArr == null || replyNewsJsonArr.size() <=0){
					throw new WxmallBaseException("图文消息只要有一条数据,请检查配置");
				}
				
				FollowReplyConfig replyConfig = new FollowReplyConfig();
				replyConfig.setFollowId(follow.getId()).setMsgType(MsgType.news.ordinal()).setCreated(new Date()).setUpdated(new Date()).setActive(true);
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
					
					FollowReplyNews mreplyNews = new FollowReplyNews();
					mreplyNews.setFollowConfigId(replyConfig.getId()).setMsgTitle(msgTitle).setMsgDesc(msgDesc).setMsgPic(msgPic).setMsgUrl(msgUrl).setMsgOrgType(msgOrgType).setCreated(new Date()).setUpdated(new Date()).setActive(true);
					mreplyNews.save();
				}
				
			}else if(msgType == MsgType.miniprogrampage.ordinal()){
				FollowReplyConfig replyConfig = new FollowReplyConfig();
				String appId = followcJsonObj.getString("app_id");
				String appPath = followcJsonObj.getString("app_path");
				String title = followcJsonObj.getString("title");
				String thumbMediaId = followcJsonObj.getString("thumb_media_id");
				String thumbMediaPic = followcJsonObj.getString("thumb_media_pic");
				if(StrKit.isBlank(appId)) throw new WxmallBaseException("小程序Appid不能为空");
				if(StrKit.isBlank(appPath)) throw new WxmallBaseException("小程序路径不能为空");
				if(StrKit.isBlank(title)) throw new WxmallBaseException("小程序标题不能为空");
				if(StrKit.isBlank(thumbMediaId)) throw new WxmallBaseException("小程序封面图片不能为空");
				if(StrKit.isBlank(thumbMediaPic)) throw new WxmallBaseException("小程序封面图片不能为空");
				
				replyConfig.setFollowId(follow.getId()).setMsgType(MsgType.miniprogrampage.ordinal()).setAppId(followcJsonObj.getString("app_id")).setAppPath(followcJsonObj.getString("app_path")).setTitle(followcJsonObj.getString("title")).setMediaId(followcJsonObj.getString("thumb_media_id")).setMediaPic(followcJsonObj.getString("thumb_media_pic")).setCreated(new Date()).setUpdated(new Date()).setActive(true);
				replyConfig.save();
			}
			
		}
	}


	@Override
	public List<FollowReplyConfig> findConfigByFollowId(Long followId) {
		Columns columns = Columns.create();
		columns.add(Column.create("follow_id", followId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);	
	}


	@Override
	public List<FollowReplyConfigResDto> findFollowReplyConfig(Long followId) {
		List<FollowReplyConfigResDto> followReplyConfigDtos = new ArrayList<FollowReplyConfigResDto>();
		
		List<FollowReplyConfig> followReplyConfigs = findConfigByFollowId(followId);
		for(FollowReplyConfig replyConfig : followReplyConfigs){
			FollowReplyConfigResDto replyCfgResDto = new FollowReplyConfigResDto();
			replyCfgResDto.setReplyConfig(replyConfig);	
			if(replyConfig.getMsgType() == MsgType.news.ordinal()){
				//图文消息
				List<FollowReplyNews> replyNews = followReplyNewsService.findNewsByConfigId(replyConfig.getId());
				replyCfgResDto.setReplyNews(replyNews);
			}
			followReplyConfigDtos.add(replyCfgResDto);
		}
		return followReplyConfigDtos;
	}





}