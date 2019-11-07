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

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.Order;
import com.dbumama.market.service.enmu.MsgTplType;
import com.jfinal.log.Log;
import com.dbumama.weixin.api.WxaTemplate;
import io.jboot.components.event.annotation.EventConfig;

/**
 * @author wangjun
 * 2018年5月18日
 */
@EventConfig(action = {"order_closed"})
public class OrderClosedListener extends AbstractOrderTemplateMsgListener {

	public OrderClosedListener() {
		super(MsgTplType.closed.ordinal());
	}

	static final Log logger = Log.getLog(OrderClosedListener.class);


	/* (non-Javadoc)
	 * @see com.dbumama.market.service.listener.AbstractOrderTemplateMsgListener#doEvent(com.dbumama.market.model.Order)
	 */
	@Override
	protected void doEvent(Order order, AuthUser authUser, BuyerUser buyer, WxaTemplate wxaTemplate) {
		wxaTemplate.setPage("pages/order/detail?id="+order.getId());
		wxaTemplate.add("keyword1", order.getOrderSn()).add("keyword2", order.getPayFee().toString()).add("keyword3", format(order.getCreated()))
					.add("keyword4", format(order.getUpdated())).add("keyword5", "已关闭").add("keyword6", "您可以重新下单，请在1小时内完成支付");
		
		send(order, wxaTemplate);
	}

}
