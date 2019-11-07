package com.dbumama.market.service.provider;

import java.util.Date;

import com.dbumama.market.model.Shop;
import com.dbumama.market.model.ShopEmployee;
import com.dbumama.market.service.api.ShopEmployeeService;
import com.dbumama.market.service.api.WxmallMsgBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ShopEmployeeServiceImpl extends WxmServiceBase<ShopEmployee> implements ShopEmployeeService {
	
	@Inject
	private ShopEmployeeService shopEmployeeService;

	@Override
	public Page<Record> list(Long appId, Integer pageNo, Integer pageSize, String shopName,String emplName) {

		String select = " SELECT se.*,s.shop_name ";
		String sqlExceptSelect = " FROM "+ShopEmployee.table+" se "
				+ " left join " + Shop.table + " s on se.shop_id = s.id";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("se.app_id", appId);
		helper.addWhereLike("s.shop_name", shopName);
		helper.addWhereLike("se.empl_name", emplName);
		helper.addWhere("se.active", 1);
		helper.addOrderBy("desc", "se.updated");
		helper.build();
		
		return Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	
	}

	@Override
	public void saveEmployee(ShopEmployee employee) {
		if(employee.getShopId() == null) throw new WxmallMsgBaseException("shopId is null");
		if(employee.getAppId() == null) throw new WxmallMsgBaseException("appId is null");
		if(StrKit.isBlank(employee.getEmplName())) throw new WxmallMsgBaseException("name is null");
		if(StrKit.isBlank(employee.getPhone())) throw new WxmallMsgBaseException("phone is null");
		
		ShopEmployee shopEmployee = null;
		if(employee.getId() != null){
			shopEmployee = shopEmployeeService.findById(employee.getId());
			if(shopEmployee == null) throw new WxmallMsgBaseException("shopEmployee is null");
			shopEmployee.setShopId(employee.getShopId()).setPhone(employee.getPhone()).setEmplName(employee.getEmplName())
			.setActive(true).setUpdated(new Date());
		}else{
			shopEmployee = new ShopEmployee();
			shopEmployee.setShopId(employee.getShopId()).setPhone(employee.getPhone()).setEmplName(employee.getEmplName())
			.setActive(true).setUpdated(new Date()).setCreated(new Date()).setAppId(employee.getAppId());
		}
		
		try {
			saveOrUpdate(shopEmployee);
		} catch (Exception e) {
			 throw new WxmallMsgBaseException(e.getMessage());
		}
	}

}