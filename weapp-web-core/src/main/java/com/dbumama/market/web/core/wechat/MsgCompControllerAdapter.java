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
package com.dbumama.market.web.core.wechat;

import com.dbumama.market.web.core.wechat.msg.in.*;
import com.dbumama.market.web.core.wechat.msg.in.card.*;
import com.dbumama.market.web.core.wechat.msg.in.event.*;
import com.dbumama.market.web.core.wechat.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.dbumama.market.web.core.wechat.msg.in.weapp.InUserEnterTempsessionEvent;
import com.dbumama.market.web.core.wechat.msg.in.weapp.InViewMiniprogramEvent;
import com.dbumama.market.web.core.wechat.msg.in.weapp.InWeappAuditFailEvent;
import com.dbumama.market.web.core.wechat.msg.in.weapp.InWeappAuditSuccessEvent;

/**
 * @author wangjun
 * 2019年6月7日
 */
public class MsgCompControllerAdapter extends MsgCompController{

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInTextMsg(com.jfinal.weixin.sdk.msg.in.InTextMsg)
	 */
	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInImageMsg(com.jfinal.weixin.sdk.msg.in.InImageMsg)
	 */
	@Override
	protected void processInImageMsg(InImageMsg inImageMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInVoiceMsg(com.jfinal.weixin.sdk.msg.in.InVoiceMsg)
	 */
	@Override
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInVideoMsg(com.jfinal.weixin.sdk.msg.in.InVideoMsg)
	 */
	@Override
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInShortVideoMsg(com.jfinal.weixin.sdk.msg.in.InShortVideoMsg)
	 */
	@Override
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInLocationMsg(com.jfinal.weixin.sdk.msg.in.InLocationMsg)
	 */
	@Override
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInLinkMsg(com.jfinal.weixin.sdk.msg.in.InLinkMsg)
	 */
	@Override
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInCustomEvent(com.jfinal.weixin.sdk.msg.in.event.InCustomEvent)
	 */
	@Override
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInFollowEvent(com.jfinal.weixin.sdk.msg.in.event.InFollowEvent)
	 */
	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInQrCodeEvent(com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent)
	 */
	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInLocationEvent(com.jfinal.weixin.sdk.msg.in.event.InLocationEvent)
	 */
	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInMassEvent(com.jfinal.weixin.sdk.msg.in.event.InMassEvent)
	 */
	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInMenuEvent(com.jfinal.weixin.sdk.msg.in.event.InMenuEvent)
	 */
	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInSpeechRecognitionResults(com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults)
	 */
	@Override
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInTemplateMsgEvent(com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent)
	 */
	@Override
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInShakearoundUserShakeEvent(com.jfinal.weixin.sdk.msg.in.event.InShakearoundUserShakeEvent)
	 */
	@Override
	protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInVerifySuccessEvent(com.jfinal.weixin.sdk.msg.in.event.InVerifySuccessEvent)
	 */
	@Override
	protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInVerifyFailEvent(com.jfinal.weixin.sdk.msg.in.event.InVerifyFailEvent)
	 */
	@Override
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInPoiCheckNotifyEvent(com.jfinal.weixin.sdk.msg.in.event.InPoiCheckNotifyEvent)
	 */
	@Override
	protected void processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInWifiEvent(com.jfinal.weixin.sdk.msg.in.event.InWifiEvent)
	 */
	@Override
	protected void processInWifiEvent(InWifiEvent inWifiEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUserCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserCardEvent)
	 */
	@Override
	protected void processInUserCardEvent(InUserCardEvent inUserCardEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUpdateMemberCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUpdateMemberCardEvent)
	 */
	@Override
	protected void processInUpdateMemberCardEvent(InUpdateMemberCardEvent inUpdateMemberCardEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUserPayFromCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserPayFromCardEvent)
	 */
	@Override
	protected void processInUserPayFromCardEvent(InUserPayFromCardEvent inUserPayFromCardEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInMerChantOrderEvent(com.jfinal.weixin.sdk.msg.in.card.InMerChantOrderEvent)
	 */
	@Override
	protected void processInMerChantOrderEvent(InMerChantOrderEvent inMerChantOrderEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processIsNotDefinedEvent(com.jfinal.weixin.sdk.msg.in.event.InNotDefinedEvent)
	 */
	@Override
	protected void processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processIsNotDefinedMsg(com.jfinal.weixin.sdk.msg.in.InNotDefinedMsg)
	 */
	@Override
	protected void processIsNotDefinedMsg(InNotDefinedMsg inNotDefinedMsg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUserGiftingCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserGiftingCardEvent)
	 */
	@Override
	protected void processInUserGiftingCardEvent(InUserGiftingCardEvent msg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUserGetCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserGetCardEvent)
	 */
	@Override
	protected void processInUserGetCardEvent(InUserGetCardEvent msg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUserConsumeCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserConsumeCardEvent)
	 */
	@Override
	protected void processInUserConsumeCardEvent(InUserConsumeCardEvent msg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInCardSkuRemindEvent(com.jfinal.weixin.sdk.msg.in.card.InCardSkuRemindEvent)
	 */
	@Override
	protected void processInCardSkuRemindEvent(InCardSkuRemindEvent msg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInCardPayOrderEvent(com.jfinal.weixin.sdk.msg.in.card.InCardPayOrderEvent)
	 */
	@Override
	protected void processInCardPayOrderEvent(InCardPayOrderEvent msg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInCardPassCheckEvent(com.jfinal.weixin.sdk.msg.in.card.InCardPassCheckEvent)
	 */
	@Override
	protected void processInCardPassCheckEvent(InCardPassCheckEvent msg) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInEqubindEvent(com.jfinal.weixin.sdk.iot.msg.InEqubindEvent)
	 */
//	@Override
//	protected void processInEqubindEvent(InEqubindEvent msg) {
//
//	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInEquDataMsg(com.jfinal.weixin.sdk.iot.msg.InEquDataMsg)
	 */
//	@Override
//	protected void processInEquDataMsg(InEquDataMsg msg) {
//
//	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInWeappAduitSuccessEvent(com.jfinal.weixin.sdk.msg.in.weapp.InWeappAuditSuccessEvent)
	 */
	@Override
	protected void processInWeappAduitSuccessEvent(InWeappAuditSuccessEvent event) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInWeappAduitFailEvent(com.jfinal.weixin.sdk.msg.in.weapp.InWeappAuditFailEvent)
	 */
	@Override
	protected void processInWeappAduitFailEvent(InWeappAuditFailEvent event) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInViewMiniprogramEvent(com.jfinal.weixin.sdk.msg.in.weapp.InViewMiniprogramEvent)
	 */
	@Override
	protected void processInViewMiniprogramEvent(InViewMiniprogramEvent event) {
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.web.core.wechat.MsgCompController#processInUserEnterTempsessionEvent(com.jfinal.weixin.sdk.msg.in.weapp.InUserEnterTempsessionEvent)
	 */
	@Override
	protected void processInUserEnterTempsessionEvent(InUserEnterTempsessionEvent event) {
		
	}

}
