package com.dbumama.market.service.provider;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.Payee;
import com.dbumama.market.service.api.OrderException;
import com.dbumama.market.service.api.PayeeService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class PayeeServiceImpl extends WxmServiceBase<Payee> implements PayeeService {

	@Override
	public List<Record> list(Long appId) {
		if(appId == null) throw new WxmallBaseException("appId is null");
		String sql = "SELECT p.*,bu.nickname,bu.headimgurl from "+Payee.table+" p LEFT JOIN "+BuyerUser.table
						+" bu ON p.open_id = bu.open_id where p.app_id = ? and p.active = 1;";
		
		return Db.find(sql, appId);
	}

	@Override
	public void deletePayee(Long id) {
		if(id == null) throw new WxmallBaseException("id is null");
		Payee payee = findById(id);
		if(payee == null) throw new WxmallBaseException("payee is null");
		payee.setActive(false);
		
		try {
			update(payee);
		} catch (Exception e) {
			new WxmallBaseException(e.getMessage());
		}
	}

	@Override
	public void updatePayee(String payeeList) {
		if(StrKit.isBlank(payeeList)) throw new WxmallBaseException("更新收款人参数出错");

		JSONArray jsonArray = null;
		try {
			 jsonArray = JSONArray.parseArray(payeeList);
		} catch (Exception e) {
			throw new OrderException(e.getMessage());
		}
		
		if(jsonArray==null || jsonArray.size()<=0) throw new WxmallBaseException("请选择收款人");
		
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			final String id = jsonObj.getString("id");
			final String payeeName = jsonObj.getString("payee_name");
			Payee payee = findById(id);
			if(payee == null) throw new WxmallBaseException("收款人信息出错");
			
			payee.setPayeeName(payeeName);
			try {
				update(payee);
			} catch (Exception e) {
				throw new WxmallBaseException(e.getMessage());
			}
		}
			
	}

	@Override
	public Payee findByOpenId(Long appId, String openId) {
		return DAO.findFirst("select * from " + Payee.table + " where app_id=? and open_id=? ", appId, openId);
	}

}