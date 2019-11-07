package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Cart;
import com.dbumama.market.service.api.CartItemResultDto;
import com.dbumama.market.service.api.ProdFullCutResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface CartService  {

	public void add(Long buyerId, Long productId, int quantity, String speci) throws WxmallBaseException;
	
	public List<CartItemResultDto> getCartsByBuyer(Long buyerId) throws WxmallBaseException;
	
	public Long getCartItemCountByBuyer(Long buyerId) throws WxmallBaseException;
	
	/**
	 * 获取购物车中的商品的满减数据，并按从小到大的顺序进行排序
	 * @param buyerId
	 * @return
	 * @throws WxmallBaseException
	 */
	public List<ProdFullCutResultDto> getCartFullCat(List<CartItemResultDto> cartItemParamDto);

	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Cart findById(Object id);


    /**
     * find all model
     *
     * @return all <Cart
     */
    public List<Cart> findAll();


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
    public boolean delete(Cart model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Cart model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Cart model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Cart model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Cart> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Cart> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Cart> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}