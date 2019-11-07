/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.web.controller.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.WeappAudit;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.enmu.WeappAuditStatus;
import com.dbumama.market.utils.JsonUtils;
import com.dbumama.market.web.core.utils.IpKit;
import com.dbumama.market.web.core.wechat.MsgCompControllerAdapter;
import com.dbumama.market.web.core.wechat.msg.in.InNotDefinedMsg;
import com.dbumama.market.web.core.wechat.msg.in.InTextMsg;
import com.dbumama.market.web.core.wechat.msg.in.card.InCardPassCheckEvent;
import com.dbumama.market.web.core.wechat.msg.in.card.InCardPayOrderEvent;
import com.dbumama.market.web.core.wechat.msg.in.card.InCardSkuRemindEvent;
import com.dbumama.market.web.core.wechat.msg.in.card.InUserGetCardEvent;
import com.dbumama.market.web.core.wechat.msg.in.event.*;
import com.dbumama.market.web.core.wechat.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.dbumama.market.web.core.wechat.msg.in.weapp.InWeappAuditFailEvent;
import com.dbumama.market.web.core.wechat.msg.in.weapp.InWeappAuditSuccessEvent;
import com.dbumama.market.web.core.wechat.msg.out.OutTextMsg;
import com.dbumama.weixin.api.CompCustomApi;
import com.dbumama.weixin.api.CompUserApi;
import com.dbumama.weixin.api.CompWxaCodeApi;
import com.dbumama.weixin.api.QueryCompUserAuthApi;
import com.dbumama.weixin.utils.HttpUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.rpc.annotation.RPCInject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信第三方平台事件接收
 * @author wangjun
 * 2018年6月14日
 */
public class WechatCommonMsgController extends MsgCompControllerAdapter {
	
	private static final Log logger = Log.getLog(WechatCommonMsgController.class);

	@RPCInject
	private CardService cardService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private BuyerUserService buyerUserService;
	@RPCInject
	private SellerUserService sellerUserService;
	
	@RPCInject
	private MenuReplyConfigService menuReplyConfigService;

	private static final String testAppid = "wx570bc396a51b8ff8";
    private static String customMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    
	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInTextMsg(com.jfinal.weixin.sdk.msg.in.InTextMsg)
	 */
	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		logger.debug("处理文本消息,消息内容:" + inTextMsg.getContent());
		String appId = getAuthAppId();
    	logger.debug("appId:" + appId + ",openId:" + inTextMsg.getFromUserName());
    	
    	if(testAppid.equals(appId)){
    		if(inTextMsg.getContent().contains("QUERY_AUTH_CODE")){
    			renderNull();
    			final String queryAuthCode = inTextMsg.getContent().split(":")[1];
    			ApiResult result = QueryCompUserAuthApi.queryAuth(authUserService.getCompAccessToken(), queryAuthCode);
    			if(!result.isSucceed()){
    				logger.error("QueryCompUserAuthApi.queryAuth is fail...");
    				return;
    			}
    				
    			logger.debug("=============result:" + result.getJson());
    			JSONObject jsonObject = JSONObject.parseObject(result.getJson());
    			jsonObject = jsonObject.getJSONObject("authorization_info");
    			Map<String, Object> message = new HashMap<String, Object>();
    			message.put("touser", inTextMsg.getFromUserName());
    			message.put("msgtype", "text");
    	        Map<String, Object> textObj = new HashMap<String, Object>();
    	        textObj.put("content", queryAuthCode+"_from_api");
    	        message.put("text", textObj);
    			String jsonResult = HttpUtils.post(customMessageUrl + jsonObject.getString("authorizer_access_token"), JsonUtils.toJson(message));
    			logger.debug("调用客服接口：jsonResult:" + jsonResult);
    		}else{
    			OutTextMsg outMsg = new OutTextMsg(inTextMsg);
        		outMsg.setContent("TESTCOMPONENT_MSG_TYPE_TEXT_callback");
        		render(outMsg);
    		}
    	}else{
    		AuthUser authUser = authUserService.getAuthUserByAppId(getAuthAppId());
    		if(authUser == null){
    			renderDefault();
    			return;
    		}
    		final String accessToken = authUserService.getAccessToken(authUser);
    		if(StrKit.isBlank(accessToken)){
    			renderDefault();
    			return;
    		}
    		
    		final String openid = inTextMsg.getFromUserName();
    		
    		ApiResult res = CompUserApi.getUserInfo(accessToken, openid);
			if(res.isSucceed()){
				buyerUserService.saveOrUpdate(openid, authUser.getAppId(), res.getJson(), IpKit.getRealIpV2(getRequest()));
			}
    		
    		final String keywordsText = inTextMsg.getContent();
    		if("openid".equals(keywordsText)){
    			CompCustomApi.sendText(accessToken, openid, openid, authUser.getAppId());
    		}
    		
    	}
		
		renderDefault();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInFollowEvent(com.jfinal.weixin.sdk.msg.in.event.InFollowEvent)
	 */
	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		
		AuthUser authUser = getAuthUser();
		final String accessToken = authUserService.getAccessToken(authUser);
		
		if(StrKit.isBlank(accessToken)){
			renderDefault();
			return;
		}
		
		final String openid = inFollowEvent.getFromUserName();
		ApiResult res = CompUserApi.getUserInfo(accessToken, openid);
		if(res.isSucceed()){
			buyerUserService.saveOrUpdate(openid, authUser.getAppId(), res.getJson(), IpKit.getRealIpV2(getRequest()));
		}
		
		renderDefault();
	}

	private int cacheSeconds = 600; //缓存时间，10分钟后清除缓存
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInQrCodeEvent(com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent)
	 */
	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		if(testAppid.equals(getAuthAppId())){
    		OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
    		outMsg.setContent(inQrCodeEvent.getEvent().toUpperCase()+"from_callback");
    		render(outMsg);
    	}else{
    		AuthUser authUser = authUserService.getAuthUserByAppId(getAuthAppId());
    		if(authUser == null){
    			renderDefault();
    			return;
    		}
    		
    		final String accessToken = authUserService.getAccessToken(authUser);
    		if(StrKit.isBlank(accessToken)){
    			renderDefault();
    			return;
    		}
    		
    		
    		String eventKey = inQrCodeEvent.getEventKey();
    		final String openid = inQrCodeEvent.getFromUserName();
    		final String ticket = inQrCodeEvent.getTicket();
    		
    		logger.debug("=========================openId:" + openid);
    		logger.debug("=========================eventKey:" + eventKey);
    		if("subscribe".equals(inQrCodeEvent.getEvent())){
    			//扫码后关注
    			eventKey = eventKey.substring(eventKey.indexOf("_")+1, eventKey.length());
    		}
    		
    		ApiResult res = CompUserApi.getUserInfo(accessToken, openid);
			if(res.isSucceed()){
				buyerUserService.saveOrUpdate(openid, authUser.getAppId(), res.getJson(), IpKit.getRealIpV2(getRequest()));
			}

    		
    		//特殊二维码事件处理==========================================================================================================
    		if(eventKey.startsWith(WeappConstants.QRCODE_NOTIFIY_PREFIX)){//扫描的是添加消息接收者二维码 或者是添加消息预览者的二维码
    			ApiResult userRes = CompUserApi.getUserInfo(accessToken, openid);
    			if(userRes.isSucceed()){
    				//获取用户信息失败，说明用户需要关注公众号
    				Jboot.getCache().put(WeappConstants.WECHAT_ADD_MENU_NOTIFIYER_CACHE, eventKey, userRes, cacheSeconds);//10分钟后清除缓存值
    			}else{
    				Jboot.getCache().remove(WeappConstants.WECHAT_ADD_MENU_NOTIFIYER_CACHE, eventKey);
    			}	
    		}else if(eventKey.startsWith(WeappConstants.QRCODE_ADD_PREVIEWER_PREFIX)){//群发预览者
    			ApiResult userRes = CompUserApi.getUserInfo(accessToken, openid);
    			if(userRes.isSucceed()){
    				//获取用户信息失败，说明用户需要关注公众号
    				Jboot.getCache().put(WeappConstants.WECHAT_ADD_PREVIEWER_CACHE, eventKey, userRes, cacheSeconds);//10分钟后清除缓存值
    			}else{
    				Jboot.getCache().remove(WeappConstants.WECHAT_ADD_PREVIEWER_CACHE, eventKey);
    			}	
    		}else if(eventKey.startsWith(WeappConstants.QRCODE_LOGIN_PREFIX)){
    			//二维码扫码登录事件处理
    			if(StrKit.notBlank(WeappConstants.WECHAT_LOGIN_APPID) && WeappConstants.WECHAT_LOGIN_APPID.equals(authUser.getAppId())){
    				sellerUserService.processInQrCode(openid, eventKey, accessToken);
    				CompCustomApi.sendText(accessToken, openid, "登录成功，点步软件欢迎您！请留意电脑端浏览器进入系统后台！客服微信：13533109940", authUser.getAppId());
    			}
    		}else if(eventKey.startsWith(WeappConstants.QRCODE_MULTIGROUP_PREFIX)){
    			logger.debug("==================================扫描拼团二维码关注:openid:" + openid + ",qrcodeKey:" + eventKey);
				//一定要用户扫码关注公众号才算
				
			}else if(eventKey.startsWith(WeappConstants.COMPANY_PAY_PREFIX)){//添加企业收款人
    			ApiResult userRes = CompUserApi.getUserInfo(accessToken, openid);
    			if(userRes.isSucceed()){
    				Jboot.getCache().put(WeappConstants.WECHAT_ADD_COMPANYPAY_CACHE, eventKey, userRes, cacheSeconds);//10分钟后清除缓存值
    			}else{
    				Jboot.getCache().remove(WeappConstants.WECHAT_ADD_COMPANYPAY_CACHE, eventKey);
    			}	
    		}
    		//特殊二维码事件处理结束================================================================================================================
    		renderDefault();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInLocationEvent(com.jfinal.weixin.sdk.msg.in.event.InLocationEvent)
	 */
	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		logger.debug("发送地理位置事件：" + inLocationEvent.getFromUserName());
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
            outMsg.setContent(inLocationEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
        	final String lat = inLocationEvent.getLatitude();
        	final String lng = inLocationEvent.getLongitude();
        	final String precision = inLocationEvent.getPrecision();
        	final String openid = inLocationEvent.getFromUserName();
        	BuyerUser buyer = buyerUserService.findByOpenId(openid);
        	if(buyer !=null){
        		buyer.setLat(lat).setLng(lng).setPrecision(precision);
        		buyerUserService.update(buyer);
        	}
        	
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInMassEvent(com.jfinal.weixin.sdk.msg.in.event.InMassEvent)
	 */
	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
		logger.debug("测试方法：processInMassEvent()");
        if(testAppid.equals(getAuthAppId())){
            OutTextMsg outMsg = new OutTextMsg(inMassEvent);
            outMsg.setContent(inMassEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);        	
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInMenuEvent(com.jfinal.weixin.sdk.msg.in.event.InMenuEvent)
	 */
	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
            outMsg.setContent(inMenuEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
    		
    		AuthUser authUser = authUserService.getAuthUserByAppId(getAuthAppId());
    		if(authUser == null){
    			renderDefault();
    			return;
    		}
    		
    		final String accessToken = authUserService.getAccessToken(authUser);
    		if(StrKit.isBlank(accessToken)){
    			renderDefault();
    			return;
    		}
    		
    		final String menuKey = inMenuEvent.getEventKey();
    		final String openId = inMenuEvent.getFromUserName();
    		
    		//=========================菜单回复开始======================================
    		menuReplyConfigService.reply(authUser, menuKey, openId);
    		//=========================菜单回复结束======================================

    		renderDefault();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInSpeechRecognitionResults(com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults)
	 */
	@Override
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
		logger.debug("语音识别事件：" + inSpeechRecognitionResults.getFromUserName());
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inSpeechRecognitionResults);
            outMsg.setContent(inSpeechRecognitionResults.getFromUserName().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInTemplateMsgEvent(com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent)
	 */
	@Override
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		logger.debug("测试方法：processInTemplateMsgEvent()");
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inTemplateMsgEvent);
            outMsg.setContent(inTemplateMsgEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInShakearoundUserShakeEvent(com.jfinal.weixin.sdk.msg.in.event.InShakearoundUserShakeEvent)
	 */
	@Override
	protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		logger.debug("摇一摇周边设备信息通知事件：" + inShakearoundUserShakeEvent.getFromUserName());
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inShakearoundUserShakeEvent);
            outMsg.setContent(inShakearoundUserShakeEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInVerifySuccessEvent(com.jfinal.weixin.sdk.msg.in.event.InVerifySuccessEvent)
	 */
	@Override
	protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {
		logger.debug("资质认证成功通知事件：" + inVerifySuccessEvent.getFromUserName());
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inVerifySuccessEvent);
            outMsg.setContent(inVerifySuccessEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInVerifyFailEvent(com.jfinal.weixin.sdk.msg.in.event.InVerifyFailEvent)
	 */
	@Override
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		logger.debug("资质认证失败通知事件：" + inVerifyFailEvent.getFromUserName());
        if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inVerifyFailEvent);
            outMsg.setContent(inVerifyFailEvent.getEvent().toUpperCase()+"from_callback");
            render(outMsg);
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInPoiCheckNotifyEvent(com.jfinal.weixin.sdk.msg.in.event.InPoiCheckNotifyEvent)
	 */
	@Override
	protected void processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent) {
		if(testAppid.equals(getAuthAppId())){
        	OutTextMsg outMsg = new OutTextMsg(inPoiCheckNotifyEvent);
            outMsg.setContent("from_callback");
            render(outMsg);
        }else{
        	renderNull();
        }
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgController#processInUserGetCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserGetCardEvent)
	 */
	@Override
	protected void processInUserGetCardEvent(InUserGetCardEvent msg) {
		renderNull();
	}

	@RPCInject
	private WeappAuditService weappAuditService;
	
	/**
	 * 小程序审核成功事件
	 */
	@Override
	protected void processInWeappAduitSuccessEvent(InWeappAuditSuccessEvent event) {
		logger.debug("小程序审核成功app id：" + event.getToUserName());
		
		AuthUser authUser = authUserService.getAuthUserByUserName(event.getToUserName());
		if(authUser == null){
			System.err.println("授权小程序不存在！原始Id：" + event.getToUserName());
			renderNull();
			return;
		}
		
		//查询正在审核的小程序记录
		List<WeappAudit> weappAudits = authUserService.getWeappAduitsByAppId(authUser.getAppId());
		if(weappAudits != null){
			for(WeappAudit weappAudit : weappAudits){
				weappAudit.setStatus(WeappAuditStatus.success.ordinal());
				weappAuditService.update(weappAudit);				
			}
		}
		ApiResult result = CompWxaCodeApi.release(authUserService.getAccessToken(authUser));
		if(!result.isSucceed()){
			//小程序发布失败
			System.err.println("小程序发布失败，appId:" + event.getToUserName());
		}
		renderNull();
	}

	/**
	 * 小程序审核失败事件
	 */
	@Override
	protected void processInWeappAduitFailEvent(InWeappAuditFailEvent event) {
		logger.debug("小程序审核失败app id：" + event.getToUserName());
		
		AuthUser authUser = authUserService.getAuthUserByUserName(event.getToUserName());
		if(authUser == null){
			System.err.println("授权小程序不存在！原始Id：" + event.getToUserName());
			renderNull();
			return;
		}
		
		//查询正在审核的小程序记录
		List<WeappAudit> weappAudits = authUserService.getWeappAduitsByAppId(authUser.getAppId());
		if(weappAudits != null){
			for(WeappAudit weappAudit : weappAudits){
				weappAudit.setStatus(WeappAuditStatus.fail.ordinal());
				weappAudit.setReason(event.getReason());
				weappAuditService.update(weappAudit);
			}
		}
		renderNull();
	}
	
	

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompControllerAdapter#processInCardSkuRemindEvent(com.jfinal.weixin.sdk.msg.in.card.InCardSkuRemindEvent)
	 */
	@Override
	protected void processInCardSkuRemindEvent(InCardSkuRemindEvent msg) {
		super.processInCardSkuRemindEvent(msg);
		renderNull();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompControllerAdapter#processInCardPayOrderEvent(com.jfinal.weixin.sdk.msg.in.card.InCardPayOrderEvent)
	 */
	@Override
	protected void processInCardPayOrderEvent(InCardPayOrderEvent msg) {
		super.processInCardPayOrderEvent(msg);
		renderNull();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompControllerAdapter#processInCardPassCheckEvent(com.jfinal.weixin.sdk.msg.in.card.InCardPassCheckEvent)
	 */
	@Override
	protected void processInCardPassCheckEvent(InCardPassCheckEvent msg) {
		super.processInCardPassCheckEvent(msg);
		renderNull();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompControllerAdapter#processIsNotDefinedEvent(com.jfinal.weixin.sdk.msg.in.event.InNotDefinedEvent)
	 */
	@Override
	protected void processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent) {
		super.processIsNotDefinedEvent(inNotDefinedEvent);
		renderNull();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompControllerAdapter#processIsNotDefinedMsg(com.jfinal.weixin.sdk.msg.in.InNotDefinedMsg)
	 */
	@Override
	protected void processIsNotDefinedMsg(InNotDefinedMsg inNotDefinedMsg) {
		super.processIsNotDefinedMsg(inNotDefinedMsg);
		renderNull();
	}
	
	
}
