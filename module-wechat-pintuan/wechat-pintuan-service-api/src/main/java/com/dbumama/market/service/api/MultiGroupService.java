package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.MultiGroup;
import com.dbumama.market.model.Order;
import com.dbumama.market.model.Product;
import com.dbumama.market.service.api.GroupingResultDto;
import com.dbumama.market.service.api.GrouponParamDto;
import com.dbumama.market.service.api.GrouponResultDto;
import com.dbumama.market.service.api.ProdGroupResultDto;
import com.dbumama.market.service.api.ProductException;
import com.dbumama.market.service.api.ProductParamDto;
import com.dbumama.market.service.api.ProductResultDto;
import com.dbumama.market.service.api.UmpException;

import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface MultiGroupService  {
	
	public void sendFailMassages(Order order);
	
	public void sendJoinMassages(Order order);
	
	public void sendSuccessMassages(Order order);
	
	/**
	* @Title: reply
	* @Description: 关注后自动回复
	 */
	public void reply(AuthUser authUser, String openid, MultiGroup multiGroup);
	
	/**
	* @Title: findByTicket
	* @Description: 通过ticket查找拼团活动
	 */
	public MultiGroup findByTicket(String ticket, Long appId);
	
	/**
	* @Title: getPutIn
	* @Description: 获取投放信息
	 */
	public HashMap<String, String> getPutIn(Long id, AuthUser authUser);
	
	/**
	 * 查询用户参加拼团的商品
	 * @return
	 */
	public List<Record> getOrderProduct(Long multigroupId, Long buyerId, int pageNo, int pageSize);

	public void save(MultiGroup multiGroup, String multiGroupSetItems) throws UmpException;
	
	public List<Record> getMultiGroupMini(Long authUserId,
                                          Long id, int pageNo, int pageSize);
	
	/**
	 * 获取拼团活动详细信息
	 * @param grouponId
	 * @return
	 * @throws UmpException
	 */
	public GrouponResultDto getGroupInfo(Long grouponId) throws UmpException;
	
	/**
	 * 获取拼团“要选择”的商品信息
	 * @param productParamDto
	 * @return
	 * @throws ProductException
	 */
	public Page<ProductResultDto> getProducts4GrouponPage(ProductParamDto productParamDto) throws ProductException;
	
    /**
     * 获取拼团列表
     * @param grouponParamDto
     * @return
     * @throws UmpException
     */
	public Page<MultiGroup> list(GrouponParamDto grouponParamDto) throws UmpException; 
	
	/**
	 * 返回拼团“已选择”的商品信息：规格
	 * @param multiGroup
	 * @param productId
	 * @return
	 * @throws UmpException
	 */
	public ProductResultDto getProductResultDto(MultiGroup multiGroup, Long productId)throws UmpException;
	
	/**
	 * 微信端获取拼团信息
	 * @param product
	 * @return
	 * @throws ProductException
	 */
	public ProdGroupResultDto getProductGroup(Product product) throws UmpException;
	
	/**
	 * 获取拼团价
	 * @param specvalue
	 * @return
	 * @throws ProductException
	 */
	public BigDecimal getCollagePrice(Product product, String specvalue) throws ProductException;
	
	/**
	 * 获取商品有效的拼团活动
	 * @param product
	 * @return
	 * @throws UmpException
	 */
	public MultiGroup getProductMultiGroup(Product product) throws UmpException;
	
	/**
	 * 获取拼团成员，拼团有效时限倒计时等信息
	 * @return
	 * @throws UmpException
	 */
	public GroupingResultDto getGroupAndJoinUserInfos(Long groupId, Long buyerId) throws UmpException;
	
	/**
	 * 获取当前商品正在进行中的拼团列表
	 * @param product
	 * @return
	 * @throws UmpException
	 */
	public List<GroupingResultDto> getGroupsByProduct(Product product) throws UmpException;
	
	/**
	 * 获取全部未结束的拼团活动
	 * 定时任务用
	 * @return
	 */
	List<MultiGroup> getUnFinishMultiGroup();

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MultiGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <MultiGroup
     */
    public List<MultiGroup> findAll();


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
    public boolean delete(MultiGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MultiGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MultiGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MultiGroup model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MultiGroup> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MultiGroup> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MultiGroup> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

}