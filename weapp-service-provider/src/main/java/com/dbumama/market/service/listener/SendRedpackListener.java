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

import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.event.annotation.EventConfig;

/**
 * 发送现金红包
 * @author wangjun
 * 2018年6月3日
 */
@EventConfig(action = "send_redpack")
public class SendRedpackListener implements JbootEventListener{

	/* (non-Javadoc)
	 * @see io.jboot.event.JbootEventListener#onEvent(io.jboot.event.JbootEvent)
	 */
	@Override
	public void onEvent(JbootEvent event) {
//		BigDecimal cashbackFee = null;//给用户返现的金额
//		try {
//			cashbackFee = cashbackService.cash2Buyer(buyerUser, order, orderItems, product, cashback);	
//		} catch (UmpException e) {
//			e.printStackTrace();
//			//记录返现错误信息
//			CashbackRcd cashbackRcd = new CashbackRcd();
//			cashbackRcd.setOrderId(order.getId());
//			cashbackRcd.setProductId(product.getId());
//			cashbackRcd.setBuyerId(buyerUser.getId());
//			cashbackRcd.setCashBackFee(cashbackFee);
//			cashbackRcd.setSendLog(e.getMessage());//发送成功
//			cashbackRcd.setActive(true);
//			cashbackRcd.setCreated(new Date());
//			cashbackRcd.setUpdated(new Date());
//			cashbackRcd.save();
//		}
	}

}
