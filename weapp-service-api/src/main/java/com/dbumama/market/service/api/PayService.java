/**
 * 文件名:PayService.java
 * 版本信息:1.0
 * 日期:2015-11-2
 * 广州点步信息科技版权所有
 */
package com.dbumama.market.service.api;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;

import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * @author: wjun_java@163.com
 * @date:2015-11-2
 */
public interface PayService {
	
	/**
	 * 用户参与抽奖活动支付充值后回调到此方法
	 * @param user
	 * @throws PayException
	 */
	public void resultLotteryCallback(BuyerUser user, TreeMap<String, Object> params) throws PayException;
	
	/**
	 * 用户在商城支付订单后，进行回调处理
	 * @param user
	 * @param params
	 * @throws PayException
	 */
	public void resultOrderCallback(BuyerUser user, TreeMap<String, Object> params) throws PayException;
	
	/**
	 * 会员充值会员卡后回调处理
	 * @param user
	 * @param params
	 * @throws PayException
	 */
	public void resultMemberCardCallback(BuyerUser user, TreeMap<String, Object> params) throws PayException;
	
	/**
	 * 调用微信统一下单接口
	 * @param ip
	 * @return
	 * @throws PayException
	 */
	public TreeMap<String, Object> prepareToPay(Long orderId, String ip) throws PayException;
	
	/**
	 * 代公众号发起支付请求
	 * @param authUser
	 * @param ip
	 * @return
	 * @throws PayException
	 */
	public TreeMap<String, Object> prepareToPay(Long orderId, AuthUser authUser, String ip) throws PayException;
	
	/**
	 * 小程序发起支付请求
	 * @param ip
	 * @return
	 * @throws PayException
	 */
	public TreeMap<String, Object> wxAppPrepareToPay(Long orderId, String ip) throws PayException;
	
	/**
	 * 调用微信PC扫码支付统一下单接口
	 * @param tradeNo
	 * @param payFee
	 * @param desc
	 * @param ip
	 * @return
	 * @throws PayException
	 */
	public String prepareToPay4pc(String tradeNo, BigDecimal payFee, String desc, String ip) throws PayException ;
	
	/**
	 * 通过会员卡支付订单
	 * @param orderId
	 * @throws PayException
	 */
	public void payByCard(Long orderId, Long userId) throws PayException;

	public boolean checkSign(TreeMap<String, Object> params, String tenpaySign);
	
}
