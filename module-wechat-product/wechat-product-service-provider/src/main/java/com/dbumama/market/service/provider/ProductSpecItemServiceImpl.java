package com.dbumama.market.service.provider;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.ProductSpecItem;
import com.dbumama.market.service.api.ProductSpecItemService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductSpecItemServiceImpl extends WxmServiceBase<ProductSpecItem> implements ProductSpecItemService {

	private static final ProductSpecItem prodSpecItemdao = new ProductSpecItem().dao();
	
	@Override
	public ProductSpecItem getProductSpecItemByPIDAndSFV(Long proudctId, String sfv) {
		//转为JSON数组
		String sfvalue="";
		JSONArray jsonArr = JSON.parseArray(sfv);
		//拼接规格数据
		if(jsonArr.size()>0){
    		for(int i=0;i<jsonArr.size();i++){
    			JSONObject json = jsonArr.getJSONObject(i);
    			sfvalue+=json.getString("spvId")+",";
    		}
    		sfvalue=sfvalue.substring(0,sfvalue.length()-1);
		}
		//根据规格和商品ID查询商品明细
		ProductSpecItem productSpecItem = prodSpecItemdao.findFirst(
				"select * FROM "+ProductSpecItem.table+" WHERE product_id = ? and specification_value = ?", proudctId, sfvalue);
		//返回查询到的商品明细对象
		if(productSpecItem != null){
			return productSpecItem;
		}
		return null;
	}

	@Override
	public List<ProductSpecItem> findByProductId(Long productId) {
		String sql = "select * from " +ProductSpecItem.table+" where product_id = ? and active = 1";
		return DAO.find(sql,productId);
	}

	@Override
	public ProductSpecItem getProductSpecItem(Long proudctId, String sfv) {
		String sql = "select * from " +ProductSpecItem.table+" where product_id = ? and specification_value = ? and active = 1";
		return DAO.findFirst(sql,proudctId,sfv);
	}

}