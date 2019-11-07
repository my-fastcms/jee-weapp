package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ProductNotifyConfig;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductNotifyConfigService  {
	
	ProductNotifyConfig findByConfigType(Long shopId, String notifyType);
	
	ProductNotifyConfig save(ProductNotifyConfig config, String notifyers) throws Exception;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductNotifyConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductNotifyConfig
     */
    public List<ProductNotifyConfig> findAll();


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
    public boolean delete(ProductNotifyConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductNotifyConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductNotifyConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductNotifyConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductNotifyConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductNotifyConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductNotifyConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}