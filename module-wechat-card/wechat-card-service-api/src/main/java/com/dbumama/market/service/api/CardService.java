package com.dbumama.market.service.api;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerCard;
import com.dbumama.market.model.Card;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;

import java.util.List;
import java.util.TreeMap;

public interface CardService  {
	
	/**
	 * 获取卖家的卡券列表 auth_user_id
	 * @return
	 * @throws CardException
	 */
	public Page<CardResultDto> list(CardListParamDto cardParamDto) throws CardException;
	
	/**
	 * 保存卡券
	 * @param cardResultJson
	 * @throws CardException
	 */
	public Card save(Long authUserId, String cardId, String cardResultJson, String supplyBuy) throws CardException;
	
	/**
	 * 保存卡券到微信
	 * @param authUser
	 * @param cardData
	 * @throws CardException
	 */
	public String save2Weixin(AuthUser authUser, String cardData, String supplyBuy) throws CardException;
	
	/**
	 * 调用接口修改卡券
	 * @param authUser
	 * @param updateData
	 * @return
	 * @throws CardException
	 */
	public String update2Weixin(AuthUser authUser, String updateData, String supplyBuy) throws CardException;
	
	/**
	 * 激活会员卡
	 * @param cardActiveParam
	 * @return
	 * @throws CardException
	 */
	public String activeCard(CardActiveParamDto cardActiveParam) throws CardException;
	
	/**
	 * 会员卡充值
	 * @param buyerId
	 * @param cardId
	 * @param recharge
	 * @return
	 * @throws CardException
	 */
	public TreeMap<String, Object> rechargeCard(Long buyerId, Long cardId, String recharge, String clientIp) throws CardException;
	
	/**
	 * 会员卡按会员等级充值
	 * 对应的充值金额对应不同的会员等级
	 * @param buyerId
	 * @param cardId
	 * @param rankId
	 * @param clientIp
	 * @return
	 * @throws CardException
	 */
	public TreeMap<String, Object> rechargeCardRank(Long buyerId, Long cardId, Long rankId, String clientIp) throws CardException;
	
	/**
	 * 投放会员卡
	 * @throws CardException
	 */
	public String putIn(AuthUser authUser, String Data, Integer type) throws CardException;
	
	/**
	 * 获取用户会员卡
	 * @param buyerId
	 * @return
	 * @throws CardException
	 */
	public Card getCardByUser(Long buyerId);
	
	/**
	 * 
	 * @param openId
	 * @param cardId
	 * @param userCode
	 * @return
	 */
	public BuyerCard getUserBuyerCard(String openId, String cardId, String userCode);
	
	/**
	 * 微信端领取会员卡后，微信通知调用改方法记录用户领取的会员卡
	 * @param openId
	 * @param cardId
	 * @param userCode
	 */
	public void getWechatCard(String openId, String cardId, String userCode);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Card findById(Object id);


    /**
     * find all model
     *
     * @return all <Card
     */
    public List<Card> findAll();


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
    public boolean delete(Card model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Card model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Card model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Card model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Card> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Card> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Card> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}