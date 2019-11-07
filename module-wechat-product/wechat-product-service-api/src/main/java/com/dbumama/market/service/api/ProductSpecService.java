package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductSpec;
import com.dbumama.market.model.ProductSpecValue;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.SpecificationResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductSpecService  {
	
	public List<ProductSpec> getSpecificationsByProduct(Long productId) throws ProductException;
	public List<ProductSpecValue> getSpecificationVaulesByProduct(Long productId) throws ProductException;
	
	List<SpecificationResultDto> getProductSpecSkus(Product product);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductSpec findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductSpec
     */
    public List<ProductSpec> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(ProductSpec model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductSpec model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductSpec model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductSpec model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductSpec> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductSpec> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductSpec> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}