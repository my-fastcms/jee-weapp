package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductGroup;
import com.dbumama.market.model.ProductGroupSet;
import com.dbumama.market.service.api.GroupException;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductGroupService;
import com.dbumama.market.service.api.ProductResultDto;
import com.dbumama.market.service.api.UmpException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductGroupServiceImpl extends WxmServiceBase<ProductGroup> implements ProductGroupService {

	private static final Product productDao = new Product().dao();
	private static final ProductGroup prodGroupdao = new ProductGroup().dao();
	private static final ProductGroupSet prodGroupSetdao = new ProductGroupSet().dao();
	
	@Override
	@Before(Tx.class)
	public void save(ProductGroup productGroup, String products, Long appId) throws GroupException {
		if(productGroup == null || appId == null || StrKit.isBlank(products))
			throw new GroupException("保存商品分组出错:参数不全");
		if(productGroup.getId() == null){
			productGroup.setAppId(appId);
			productGroup.setActive(true);
			productGroup.save();
			String productIds[]=products.split(",");
			for (int i = 0; i < productIds.length; i++) {
				ProductGroupSet productGroupSet=new ProductGroupSet();
				productGroupSet.setGroupId(productGroup.getId());
				productGroupSet.setProductId(new Long(productIds[i]));
				productGroupSet.setActive(true);
				try {
					productGroupSet.save();
				} catch (Exception e) {
					throw new UmpException(e.getMessage());
				}
			}
		}else{
			productGroup.update();
			try {
				Db.deleteById(ProductGroupSet.table,"group_id",productGroup.getId());
			} catch (Exception e) {
				throw new GroupException(e.getMessage());
			}
			String productIds[]=products.split(",");
			for (int i = 0; i < productIds.length; i++) {
				ProductGroupSet productGroupSet=new ProductGroupSet();
				productGroupSet.setGroupId(productGroup.getId());
				productGroupSet.setProductId(new Long(productIds[i]));
				try {
					productGroupSet.save();
				} catch (Exception e) {
					throw new GroupException(e.getMessage());
				}
			}
		}
	}

	@Override
	public List<ProductResultDto> getGroupProduct(Long groupId) throws ProductException {
		if(groupId == null)
			throw new ProductException ("参数出错");
		List<ProductResultDto> productResults=new ArrayList<ProductResultDto>();
		List<ProductGroupSet> groupSet=prodGroupSetdao.find("select * from "+ProductGroupSet.table+" where group_id=?",groupId);
		for (ProductGroupSet productGroupSet : groupSet) {
			Product product=productDao.findById(productGroupSet.getProductId());
			ProductResultDto productResultDto=new ProductResultDto();
			productResultDto.setId(product.getId());
			productResultDto.setName(product.getName());
			productResultDto.setImg(getImageDomain() + product.getImage());
			productResultDto.setPrice(product.getPrice());
			productResultDto.setStock(product.getStock());
			productResultDto.setStartDate(product.getCreated());
			productResultDto.setSales(product.getSales());
			
			productResults.add(productResultDto);
		}
		return productResults;
	}

	@Override
	public Page<ProductGroup> page(Long appId, Integer pageNo, Integer pageSize) {
		QueryHelper helper = new QueryHelper("select * ", "from " + ProductGroup.table);
		helper.addWhere("app_id", appId).addWhere("active", 1).addOrderBy("asc", "updated").build();
		return prodGroupdao.paginate(pageNo, pageSize, 
				helper.getSelect(), 
				helper.getSqlExceptSelect(), 
				helper.getParams());
	}

	@Override
	public List<ProductGroupSet> getProductGroupSetsByGroupId(Long groupId) {
		return prodGroupSetdao.find("select * from " + ProductGroupSet.table + " where group_id=?", groupId);
	}
	
}