package com.dbumama.market.service.provider;

import com.dbumama.market.model.Order;
import com.dbumama.market.model.OrderItem;
import com.dbumama.market.service.api.OrderItemService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.List;

@Bean
@RPCBean
public class OrderItemServiceImpl extends WxmServiceBase<OrderItem> implements OrderItemService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.OrderItemService#getOrderItems(java.lang.Long)
	 */
	@Override
	public List<OrderItem> getOrderItems(Long orderId) {
		return DAO.find("select * from " + OrderItem.table + " where order_id=? ", orderId);
	}

	@Override
	public Integer getPurchaseCount(Long id, Long authUserId, Long pId) {
		
		final String sql = "select sum(i.quantity)  from " + Order.table + " o  LEFT JOIN " + OrderItem.table + " i ON o.id = i.order_id "
				+"where o.app_id=? and o.buyer_id=? and i.product_id=?";
		
		return Db.queryInt(sql,authUserId,id,pId);
	}

}