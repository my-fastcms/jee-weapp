package com.dbumama.market.service.api;

import com.dbumama.market.model.OrderGroup;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;

public interface OrderGroupService  {

	/**
	 * 管理端获取拼团订单
	 *
	 * @param orderParamDto
	 * @return
	 * @throws OrderException
	 */
	public Page<OrderTuanResultDto> list4Tuan(OrderListParamDto orderParamDto) throws OrderException;

	/**
	 * 创建参团订单
	 *
	 * @throws OrderException
	 */
	public Long joinGroup(OrderJoinParamDto orderJoinParam) throws OrderException;

	/**
	 * 微信端创建拼团订单
	 *
	 * @param orderParamDto
	 * @return
	 * @throws OrderException
	 */
	public Long gcreate(OrderCreateParamDto orderParamDto) throws OrderException;

	/**
	 * 针对拼团订单做结算
	 *
	 * @param buyerId
	 * @param receiverId
	 * @param items
	 * @return
	 * @throws OrderException
	 */
	public OrderResultDto gbalance(Long buyerId, Long receiverId, String items) throws OrderException;

	/**
	 * 查询出所有正在拼团中的订单
	 * @return
	 */
	List<OrderGroup> findGroupingGroups();
	
	/**
	 * 定时任务用
	 * * 拼团订单在规定时间内都全部支付后，
	 * 如果支付回调没有处理好更新拼团状态的情况下，
	 * 此任务做补充处理
	 * 已确保拼团状态为“组团成功的状态”
	 * 
	 * 如果拼团规定时间内还是没有凑足人数的时候
	 * 把拼团状态更改为“组团超时状态”
	 * 
	 * “组团超时状态” 为 “组团失败状态”的一个过渡状态
	 * “组团超时”分为2种
	 * 1种是只有团长支付，没有其他人员加入，这种情况不管团购活动是否开启模拟成团，都视为组团失败，且退款给团长
	 * 2种是团长支付后，有其他成员加入，但是人员不够，此时，如果团购活动开启模拟成团的话，视为组团成功，使用匿名补够未达到人数，否则退款给所有支付过的用户
	 * @param orderGroup
	 */
	void updateGroupStatus(OrderGroup orderGroup);

	/**
	 * 获取所有拼团超时的订单
	 * @return
	 */
	List<OrderGroup> getTimeOutGroups();
	
	/**
	 * 定时任务用
	 * 处理拼团超时的订单
	 * @param group
	 */
	void dealTimeOutOrderGroup(OrderGroup group);

	/**
	 * @Title: getMultiGroupCount
	 * @Description: 获取某用户对该拼团商品的购买数量
	 */
	public Integer getMultiGroupCount(Long buyerId, Long productId);

	/**
	 * 小程序撤销退款订单
	 *
	 * @param orderId 订单id
	 * @param buyerId 用户id
	 * @throws OrderException
	 */
	public void cancelRefunds(Long orderId, Long buyerId) throws OrderException;

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public OrderGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <OrderGroup
     */
    public List<OrderGroup> findAll();


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
    public boolean delete(OrderGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(OrderGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(OrderGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(OrderGroup model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<OrderGroup> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<OrderGroup> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<OrderGroup> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}