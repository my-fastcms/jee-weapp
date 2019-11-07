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
package com.dbumama.market.service.listener;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.Order;
import com.dbumama.market.model.WxamsgTemplate;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.BuyerUserService;
import com.dbumama.market.service.api.OrderService;
import com.dbumama.market.service.api.WxamsgTemplateService;
import com.dbumama.market.service.enmu.PaymentStatus;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.api.CompWxaTemplateApi;
import com.dbumama.weixin.api.WxaTemplate;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;

import java.util.Date;

/**
 * @author wangjun
 * 2018年5月27日
 */
public abstract class AbstractOrderTemplateMsgListener implements JbootEventListener{
	
	static final Log logger = Log.getLog(AbstractOrderTemplateMsgListener.class);
	
	protected WxamsgTemplateService wxamsgTemplateService = Jboot.service(WxamsgTemplateService.class);
	protected AuthUserService authUserService = Jboot.service(AuthUserService.class);
	protected BuyerUserService buyerUserService = Jboot.service(BuyerUserService.class);
	protected OrderService orderService = Jboot.service(OrderService.class);
	
	private Integer msgType;
	
	public AbstractOrderTemplateMsgListener(Integer msgType){
		this.msgType = msgType;
	}
	
	protected WxamsgTemplate getTemplate(Long appId){
		return wxamsgTemplateService.getTemplate(appId, msgType);
	}
	
	protected AuthUser getAuthUser(Order order){
		return authUserService.findById(order.getAppId());
	}
	
	protected BuyerUser getBuyer(Order order){
		return buyerUserService.findById(order.getBuyerId());
	}
	
	protected String getAccessToken(AuthUser authUser){
		return authUserService.getAccessToken(authUser);
	}
	
	protected String format(Date date){
		return DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.format(date);
	}
	
	@Override
	public void onEvent(JbootEvent event) {
		Order order = event.getData();
		
		AuthUser authUser = getAuthUser(order);
		if(authUser == null) {
			logger.error("订单事件:" + event.getAction() + ", 系统异常，公众账号不存在");
			return;	
		}
		
		WxamsgTemplate template = getTemplate(authUser.getId());
		if(template == null) {
			logger.error("==================template is null, appId:" + authUser.getAppId() + ",订单事件:" + event.getAction());
			return;
		}
		
		if(template.getActive() !=null && template.getActive() == false){
			logger.info("===================template is not enable ...template:" + template.getTemplateKuId() + ",订单事件:" + event.getAction());
			return;
		}
		
		//购买者
		BuyerUser buyer = getBuyer(order);
		if(buyer == null){
			logger.error("======================订单数据异常，找不到购买者, buyer is null" + ",订单事件:" + event.getAction());
			return;
		}
		
		//获取模板
		String templateId = getTemplate(authUser.getId()).getTemplateId();
		if(StrKit.isBlank(templateId)) return;

		WxaTemplate wxaTemplate = new WxaTemplate();
		wxaTemplate.setTemplate_id(templateId);
		wxaTemplate.setForm_id(order.getFormId());
		wxaTemplate.setTouser(buyer.getOpenId());
		 
		
		doEvent(order, authUser, buyer, wxaTemplate);
	}
	
	protected abstract void doEvent(Order order, AuthUser authUser, BuyerUser buyer, WxaTemplate wxaTemplate);
	
	protected void send(Order order, WxaTemplate wxaTemplate){
		ApiResult res = CompWxaTemplateApi.send(getAccessToken(getAuthUser(order)), wxaTemplate);
		
		if(!res.isSucceed()){
			logger.error("error_code:" + res.getErrorCode() + ",error_msg" + res.getErrorMsg());
			
			if(order.getPaymentStatus() == PaymentStatus.paid.ordinal()){
				//已支付订单，使用prepayid 可以多发3条模板消息
				wxaTemplate.setForm_id(order.getPrepayId());//最后消费formId，formId有7天有效期
				res = CompWxaTemplateApi.send(getAccessToken(getAuthUser(order)), wxaTemplate);
				if(!res.isSucceed()){
					logger.error("formId:" + order.getPrepayId() + ", error_code:" + res.getErrorCode() + ",error_msg" + res.getErrorMsg());
				}
			}
		}
	}
	
}
