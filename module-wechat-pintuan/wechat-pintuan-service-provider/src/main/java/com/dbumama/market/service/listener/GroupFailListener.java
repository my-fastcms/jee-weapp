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

import com.dbumama.market.model.*;
import com.dbumama.market.service.api.OrderGroupService;
import com.dbumama.market.service.api.ProductService;
import com.dbumama.market.service.enmu.MsgTplType;
import com.dbumama.market.service.enmu.OrderType;
import com.dbumama.weixin.api.WxaTemplate;
import io.jboot.Jboot;
import io.jboot.components.event.annotation.EventConfig;

import java.util.List;

/**
 * @author wangjun
 * 2018年5月29日
 */
@EventConfig(action="group_fail")
public class GroupFailListener extends AbstractOrderTemplateMsgListener{

	private ProductService productService = Jboot.service(ProductService.class);
	private OrderGroupService orderGroupService = Jboot.service(OrderGroupService.class);
	
	/**
	 */
	public GroupFailListener() {
		super(MsgTplType.groupfail.ordinal());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.listener.AbstractOrderTemplateMsgListener#doEvent(com.dbumama.market.model.Order, com.dbumama.market.model.AuthUser, com.dbumama.market.model.BuyerUser, com.jfinal.weixin.sdk.api.WxaTemplate)
	 */
	@Override
	protected void doEvent(Order order, AuthUser authUser, BuyerUser buyer, WxaTemplate wxaTemplate) {
		if(order.getOrderType() != OrderType.pintuan.ordinal()) return;
		
		//团
		OrderGroup orderGroup = orderGroupService.findById(order.getGroupId());
		
		//团商品
		Product product = productService.findById(orderGroup.getProductId());
		
		//团订单
		List<Order> orders = orderService.getOrdersByGroup(orderGroup.getId());
		
		//通知该团成员，本次拼团失败
		for(Order _order : orders){
			wxaTemplate.setForm_id(_order.getPrepayId());
			wxaTemplate.setTouser(buyerUserService.findById(_order.getBuyerId()).getOpenId());
			wxaTemplate.setPage("pages/order/detail?id="+_order.getId());
			wxaTemplate.add("keyword1", product.getName())
						.add("keyword2", "您的拼团已结束，本次拼团失败")
						.add("keyword3", "没拼成，别灰心！首页有更多免费商品等你领,已支付订单金额会原路返回到您的微信账户")
						.add("keyword4", _order.getOrderSn());
			
			send(_order, wxaTemplate);
		}
	}

}
