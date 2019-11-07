package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductSpec;
import com.dbumama.market.model.ProductSpecValue;
import com.dbumama.market.model.Specification;
import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductSpecService;
import com.dbumama.market.service.api.SpecificationResultDto;
import com.dbumama.market.service.api.SpecificationService;
import com.dbumama.market.service.api.SpecificationUtil;
import com.dbumama.market.service.api.SpecificationValueService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductSpecServiceImpl extends WxmServiceBase<ProductSpec> implements ProductSpecService {
	
	private static final ProductSpec prodSpecidao = new ProductSpec().dao();
	private static final ProductSpecValue prodSpecValuedao = new ProductSpecValue().dao();

	@Inject
	SpecificationService specificationService;
	@Inject
	SpecificationValueService specificationValueService;
	
	public List<ProductSpec> getSpecificationsByProduct(Long productId) throws ProductException {
		return prodSpecidao.find("select * from " + ProductSpec.table + " where product_id=? ", productId);
	}

	@Override
	public List<ProductSpecValue> getSpecificationVaulesByProduct(Long productId) throws ProductException {
		return prodSpecValuedao.find("select * from " + ProductSpecValue.table + " where product_id=? ", productId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.ProductSpecService#getProductSpecSkus(com.dbumama.market.model.Product)
	 */
	@Override
	public List<SpecificationResultDto> getProductSpecSkus(Product product) {
		if(product == null || product.getIsUnifiedSpec() == null || product.getIsUnifiedSpec() == true) return null;//单规格没有规格值
		List<SpecificationResultDto> specifications = new ArrayList<SpecificationResultDto>();
    	//查询商品多规格
        List<ProductSpec> prodSpeces = prodSpecidao.find("select * from " + ProductSpec.table + " where product_id = ?", product.getId());
        for(ProductSpec ps : prodSpeces){
        	SpecificationResultDto specificationResultDto = new SpecificationResultDto();
        	Specification speci = specificationService.findById(ps.getSpecificationId());
        	specificationResultDto.setSpecification(SpecificationUtil.getSpecDto(speci));
        	List<ProductSpecValue> prodSpecValues = prodSpecValuedao.find(
        			"select * from " + ProductSpecValue.table + " where product_id=? and specification_id = ? ", product.getId(), ps.getSpecificationId());
        	List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue> ();
        	for(ProductSpecValue productSpecValue : prodSpecValues){
        		SpecificationValue specificationValue = specificationValueService.findById(productSpecValue.getSpecificationValueId());
        		specificationValues.add(specificationValue);
        	}
        	specificationResultDto.setSpecificationValues(SpecificationUtil.getSpecValueDtoList(specificationValues));
        	specifications.add(specificationResultDto);
        }
        return specifications;
	}

}