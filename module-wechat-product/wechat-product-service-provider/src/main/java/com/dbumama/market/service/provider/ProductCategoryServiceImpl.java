package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.ProductCategory;
import com.dbumama.market.service.api.ProductCategoryService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductCategoryServiceImpl extends WxmServiceBase<ProductCategory> implements ProductCategoryService {
	
	@Override
	public List<ProductCategory> list(Long appId) {
		
		List<ProductCategory> categories_ =	DAO.find("select * from " + ProductCategory.table + " where active=1 and app_id=? order by created asc ", appId);
		for(ProductCategory pv :categories_){
			 pv.setImgPath(getImageDomain()+pv.getImgPath());
		}
		return categories_;
	}

	@Override
	public Page<ProductCategory> page(Long appId, Integer pageNo, Integer pageSize) {
		QueryHelper helper = new QueryHelper("select * ", "from " + ProductCategory.table);
		helper.addWhere("app_id", appId)
		.addWhere("active", 1)
		.addOrderBy("asc", "orders").build();
		
		return DAO.paginate(pageNo, pageSize, 
				helper.getSelect(), 
				helper.getSqlExceptSelect(), 
				helper.getParams());
		
	}

}