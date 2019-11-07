package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.ProductReview;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ProductReviewService  {
	
	Page<Record> getProductReviewsPage(Integer pageNo, Integer pageSize, Long appId, Integer active, String content);
	
	Page<Record> getProductReviews(Integer pageNo, Integer pageSize, Long productId);
	
	List<ProductReview> getReviewsByBuyer(Long buyerId, Long orderId, Long productId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductReview findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductReview
     */
    public List<ProductReview> findAll();


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
    public boolean delete(ProductReview model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ProductReview model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ProductReview model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductReview model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ProductReview> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ProductReview> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ProductReview> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}