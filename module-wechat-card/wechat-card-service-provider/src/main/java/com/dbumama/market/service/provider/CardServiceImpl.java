package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.api.CompCardApi;
import com.dbumama.weixin.api.CompMessageApi;
import com.dbumama.weixin.api.ParaMap;
import com.jfinal.aop.Inject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.math.BigDecimal;
import java.util.*;

@Bean
@RPCBean
public class CardServiceImpl extends WxmServiceBase<Card> implements CardService {
	@Inject
	private AuthUserService authUserService;
	private static final AuthUser authUserDao = new AuthUser().dao();
	private static final BuyerCard buyerCardDao = new BuyerCard().dao();
	private static final BuyerRecharge rechargeDao = new BuyerRecharge().dao();
	private static final BuyerUser buyerUserdao = new BuyerUser().dao();
	private static final Card cardDao = new Card().dao();
	private static final MemberRank mbRankdao = new MemberRank().dao();
	private static final UserCode usercodedao = new UserCode().dao();
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<CardResultDto> list(CardListParamDto cardParamDto) throws CardException {
		if(cardParamDto == null || cardParamDto.getAuthUserId() == null)
			throw new CardException("调用卡券列表接口缺少必要参数");
		
		AuthUser authUser = authUserService.findById(cardParamDto.getAuthUserId());
		String accessToken = authUserService.getAccessToken(authUser);
		Page<Card> cards = cardDao.paginate(cardParamDto.getPageNo(), cardParamDto.getPageSize(), 
				"select * ", " from " + Card.table + " where auth_user_id=? and card_type=? order by updated desc", cardParamDto.getAuthUserId(), "MEMBER_CARD");
		
		if(cards != null && cards.getList().size()>0){
			List<CardResultDto> cardDtos = new ArrayList<CardResultDto>();
			for(Card card : cards.getList()){
				cardDtos.add(convert2CardDto(card));
			}
			return new Page<CardResultDto> (cardDtos, cards.getPageNumber(), cards.getPageSize(), cards.getTotalPage(), cards.getTotalRow());
		}
		
		//从卡券列表接口获取卡券id, 一次全部拉取所有该公众号的卡券
		Map<String, String> params = ParaMap.create("offset", "0").put("count", ""+cardParamDto.getPageSize()).getData();
		ApiResult cardsResult = CompCardApi.batchGetCards(accessToken, authUser.getAppId(), JsonKit.toJson(params));
		if(cardsResult == null || !cardsResult.isSucceed() || cardsResult.isAccessTokenInvalid()) throw new CardException("调用微信批量获取卡券列表接口异常," + cardsResult != null ? cardsResult.getErrorMsg() : "");

		List<String> cardIds = new ArrayList<String>();
		cardIds.addAll(cardsResult.getList("card_id_list"));
		int totalPage = cardsResult.getInt("total_num") % cardParamDto.getPageSize() == 0 ? cardsResult.getInt("total_num")/cardParamDto.getPageSize() : cardsResult.getInt("total_num")/cardParamDto.getPageSize() + 1;
		for(int i=1;i<totalPage;i++){
			params = ParaMap.create("offset", i+"").put("count", ""+cardParamDto.getPageSize()).getData();
			cardsResult = CompCardApi.batchGetCards(accessToken, authUser.getAppId(), JsonKit.toJson(params));
			if(cardsResult != null && cardsResult.isSucceed()){
				cardIds.addAll(cardsResult.getList("card_id_list"));
			}
		}
		
		//根据id获取卡券详情
		if(cardIds != null && cardIds.size()>0){
			List<Card> cardDbs = new ArrayList<Card>();
			for(String cardId : cardIds){
				ApiResult cardResult = CompCardApi.getCard(accessToken, authUser.getAppId(), JsonKit.toJson(ParaMap.create("card_id", cardId).getData()));
				if(cardResult !=null && cardResult.isSucceed()){
					//成功的才处理
					JSONObject json = JSONObject.parseObject(cardResult.getJson());
					json = json.getJSONObject("card");
					String cardType = json.getString("card_type");
					if(StrKit.notBlank(cardType)){
						JSONObject jsonCard = json.getJSONObject(cardType.toLowerCase());
						Card card = new Card();
						card.setAuthUserId(cardParamDto.getAuthUserId());
						card.setCardId(cardId);
						card.setCardType(cardType);
						card.setBaseInfo(jsonCard.getString("base_info"));
						card.setSupplyBonus(jsonCard.getString("supply_bonus"));
						card.setSupplyBalance(jsonCard.getString("supply_balance"));
						card.setPrerogative(jsonCard.getString("prerogative"));
						card.setDiscount(jsonCard.getInteger("discount"));
						card.setBalanceRules(jsonCard.getString("balance_rule"));
						card.setBonusRules(jsonCard.getString("bonus_rule"));
						card.setBackgroundPicUrl(jsonCard.getString("background_pic_url"));
						card.setAdvancedInfo(jsonCard.getString("advanced_info"));
						card.setActivateUrl(jsonCard.getString("activate_url"));
						card.setAutoActivate(jsonCard.getString("auto_activate"));
						card.setWxActivate(jsonCard.getString("wx_activate"));
						card.setActive(true);
						card.setCreated(new Date());
						card.setUpdated(new Date());
						cardDbs.add(card);
					}
				}
			}
			if(cardDbs.size()>0) Db.batchSave(cardDbs, cardDbs.size());
		}
		
		//从接口处理过后，再查一次数据库
		if(cardIds.size()>0){
			cards = cardDao.paginate(cardParamDto.getPageNo(), cardParamDto.getPageSize(), 
					"select * ", " from " + Card.table + " where auth_user_id=? and card_type=? ", cardParamDto.getAuthUserId(), "MEMBER_CARD");			
		}
		List<CardResultDto> cardDtos = new ArrayList<CardResultDto>();
		for(Card card : cards.getList()){
			cardDtos.add(convert2CardDto(card));
		}
		return new Page<CardResultDto> (cardDtos, cards.getPageNumber(), cards.getPageSize(), cards.getTotalPage(), cards.getTotalRow());
	}
	
	@Override
	public String save2Weixin(AuthUser authUser, String cardData, String supplyBuy)
			throws CardException {
		if(authUser == null || StrKit.isBlank(cardData))
			throw new CardException("保存会员卡缺少必要参数");
		String accessToken = authUserService.getAccessToken(authUser);
		JSONObject postData = JSONObject.parseObject(cardData);
		logger.debug("json:" + postData.toString());
		ApiResult result = CompCardApi.createCard(accessToken, authUser.getAppId(), postData.toString());
		if(result.isAccessTokenInvalid()){
			throw new CardException("请重新绑定公众号");
		}
		if(!result.isSucceed()){
			throw new CardException("创建会员卡失败:" + result.getErrorMsg());
		}
		
		//如果需要激活，调用接口修改最终的会员卡激活地址
		final String cardId= result.get("card_id");
		ApiResult cardResult = CompCardApi.getCard(accessToken, authUser.getAppId(), JsonKit.toJson(ParaMap.create("card_id", cardId).getData()));
		if(cardResult != null && cardResult.isSucceed()){
			try {
				Card mcard = save(authUser.getId(), cardId, cardResult.getJson(), supplyBuy);
				JSONObject cardJson = JSONObject.parseObject(cardResult.getJson());
				JSONObject memberCard = JSONObject.parseObject(cardJson.getString("card"));
				memberCard = JSONObject.parseObject(memberCard.getString("member_card"));
				
				String activeUrl = "";
				if("false".equals(memberCard.getString("auto_activate"))){
					//生成激活地址
					activeUrl = "http://" + authUser.getAppId() + ".dbumama.com/user/active_card/?cardId=" + mcard.getId();
				}
				JSONObject updateJson = new JSONObject();
				updateJson.put("card_id", mcard.getCardId());
				JSONObject member_card = new JSONObject();
				member_card.put("activate_url", activeUrl);
				
				//生成充值地址
				if("true".equals(memberCard.getString("supply_balance"))){
					JSONObject custom_cell1 = new JSONObject();
					custom_cell1.put("name", "去充值");
					custom_cell1.put("tips", "会员卡充值");
					custom_cell1.put("url", "http://" + authUser.getAppId() + ".dbumama.com/user/recharge/?cardId=" + mcard.getId());
					member_card.put("custom_cell1", custom_cell1);
				}
				updateJson.put("member_card", member_card);
				ApiResult updateResult = CompCardApi.updateCard(accessToken, authUser.getAppId(), updateJson.toString());
				if(updateResult == null || !updateResult.isSucceed()) 
					throw new CardException("调用更新会员卡接口失败");
				//更新后再获取一次卡券，同步到数据库
				cardResult = CompCardApi.getCard(accessToken, authUser.getAppId(), JsonKit.toJson(ParaMap.create("card_id", cardId).getData()));
				save(authUser.getId(), cardId, cardResult.getJson(), supplyBuy);
			} catch (CardException e) {
				logger.error(e.getMessage());
				throw new CardException(e.getMessage());
			}
		}
		return cardId;
	}
	
	@Override
	public String update2Weixin(AuthUser authUser, String updateData, String supplyBuy)
			throws CardException {
		if(authUser == null || StrKit.isBlank(updateData))
			throw new CardException("保存会员卡缺少必要参数");
		
		String accessToken = authUserService.getAccessToken(authUser);
		JSONObject postData = JSONObject.parseObject(updateData);
		logger.debug("json:" + postData.toString());
		final String cardId = postData.getString("card_id");
		ApiResult result = CompCardApi.updateCard(accessToken, authUser.getAppId(), postData.toString());
		if(result.isAccessTokenInvalid()){
			throw new CardException("请重新绑定公众号");
		}
		if(!result.isSucceed()){
			logger.error(result.getErrorMsg());
			throw new CardException("更新会员卡失败:" + result.getErrorMsg());
		}
		
		ApiResult cardResult = CompCardApi.getCard(accessToken, authUser.getAppId(), JsonKit.toJson(ParaMap.create("card_id", cardId).getData()));
		if(cardResult == null || !cardResult.isSucceed()){
			throw new CardException("修改会员卡，调用获取会员卡接口失败");
		}
		try {
			Card mcard = save(authUser.getId(), cardId, cardResult.getJson(), supplyBuy);
			JSONObject cardJson = JSONObject.parseObject(cardResult.getJson());
			JSONObject memberCard = JSONObject.parseObject(cardJson.getString("card"));
			memberCard = JSONObject.parseObject(memberCard.getString("member_card"));
			String activeUrl = "";
			if("false".equals(memberCard.getString("auto_activate"))){
				//生成激活地址
				activeUrl = "http://" + authUser.getAppId() + ".dbumama.com/user/active_card/?cardId=" + mcard.getId();
			}
			JSONObject updateJson = new JSONObject();
			updateJson.put("card_id", mcard.getCardId());
			JSONObject member_card = new JSONObject();
			member_card.put("activate_url", activeUrl);
			
			//生成充值地址
			if("true".equals(memberCard.getString("supply_balance"))){
				JSONObject custom_cell1 = new JSONObject();
				custom_cell1.put("name", "去充值");
				custom_cell1.put("tips", "会员卡充值");
				custom_cell1.put("url", "http://" + authUser.getAppId() + ".dbumama.com/user/recharge/?cardId=" + mcard.getId());
				member_card.put("custom_cell1", custom_cell1);
			}
			
			updateJson.put("member_card", member_card);
			ApiResult updateResult = CompCardApi.updateCard(accessToken, authUser.getAppId(), updateJson.toString());
			if(updateResult == null || !updateResult.isSucceed()) 
				throw new CardException("调用更新会员卡接口失败");
			//更新后再获取一次卡券，同步到数据库
			cardResult = CompCardApi.getCard(accessToken, authUser.getAppId(), JsonKit.toJson(ParaMap.create("card_id", cardId).getData()));
			save(authUser.getId(), cardId, cardResult.getJson(), supplyBuy);
		} catch (CardException e) {
			logger.error(e.getMessage());
			throw new CardException(e.getMessage());
		}
		return cardId;
	}

	
	@Override
	public Card save(Long authUserId, String cardId, String cardResultJson, String supplyBuy) throws CardException {
		if(StrKit.isBlank(cardResultJson)) throw new CardException("保存卡券缺少必要参数");
		JSONObject json = JSONObject.parseObject(cardResultJson);
		json = json.getJSONObject("card");
		String cardType = json.getString("card_type");
		if(StrKit.isBlank(cardType)) throw new CardException("未知卡券类型");
		
		JSONObject jsonCard = json.getJSONObject(cardType.toLowerCase());
		Card card = cardDao.findFirst("select * from " + Card.table + " where card_id=? ", cardId);
		if(card == null) card = new Card();
		card.setAuthUserId(authUserId);
		card.setCardId(cardId);
		card.setCardType(cardType);
		card.setBaseInfo(jsonCard.getString("base_info"));
		card.setSupplyBonus(jsonCard.getString("supply_bonus"));
		card.setSupplyBalance(jsonCard.getString("supply_balance"));
		card.setPrerogative(jsonCard.getString("prerogative"));
		card.setDiscount(jsonCard.getInteger("discount"));
		card.setBalanceRules(jsonCard.getString("balance_rule"));
		card.setBonusRules(jsonCard.getString("bonus_rule"));
		card.setBackgroundPicUrl(jsonCard.getString("background_pic_url"));
		card.setAdvancedInfo(jsonCard.getString("advanced_info"));
		card.setAutoActivate(jsonCard.getString("auto_activate"));
		card.setWxActivate(jsonCard.getString("wx_activate"));
		card.setActivateUrl(jsonCard.getString("activate_url"));
		card.setActive(true);
		card.setCreated(new Date());
		card.setUpdated(new Date());
		card.setSupplyBuy(supplyBuy);
		if(card.getId() == null){
			card.save();
		}else{
			card.update();
		}
		return card;
	}
	
	@Override
	public String activeCard(CardActiveParamDto cardActiveParam) throws CardException {
		if(cardActiveParam == null || cardActiveParam.getBuyerId() == null || cardActiveParam.getAppId() == null
				|| StrKit.isBlank(cardActiveParam.getCode()) || StrKit.isBlank(cardActiveParam.getCodeInSession())
				|| StrKit.isBlank(cardActiveParam.getPhoneCode()) || StrKit.isBlank(cardActiveParam.getPhone())
				|| cardActiveParam.getCardId() == null) 
			throw new CardException("激活会员卡缺少必要参数");
		//检查图片验证码
		if(StrKit.notBlank(cardActiveParam.getCode()) 
				&& StrKit.notBlank(cardActiveParam.getCodeInSession())
				&& !cardActiveParam.getCode().toLowerCase().equals(cardActiveParam.getCodeInSession())) 
			throw new CardException("验证码错误");
		
		//check 手机验证码
		UserCode userCode = usercodedao.findFirst("select * from " + UserCode.table + " where vcode_phone=? and vcode_code=? ", 
				cardActiveParam.getPhone(), cardActiveParam.getPhoneCode());
		if(userCode == null) throw new CardException("短信验证码错误");
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis())
			throw new CardException("短信验证码已过期");

		Card mcard = cardDao.findById(cardActiveParam.getCardId());
		if(mcard == null) throw new CardException("卡券不存在");

		BuyerCard buyerCard = buyerCardDao.findFirst("select * from " + BuyerCard.table + " where buyer_id=? and card_id=? ", 
				cardActiveParam.getBuyerId(), mcard.getCardId());
		if(buyerCard == null) throw new CardException("还没有领取过该会员卡，激活失败");
		if(buyerCard.getStatus() == 1) throw new CardException("该会员卡已激活，无需重新激活");
		
		Integer initBonus = 0;
		//优惠策略
		if("true".equals(mcard.getSupplyBonus())){
			//积分优惠,激活送积分
			JSONObject bonusRulesJson = JSONObject.parseObject(mcard.getBonusRules());
			if(bonusRulesJson !=null && bonusRulesJson.getInteger("init_increase_bonus") != null){
				initBonus = bonusRulesJson.getInteger("init_increase_bonus");
			}
		}
		
		Integer initBalance = 0;
		if("true".equals(mcard.getSupplyBalance())){
			//支持储值
			JSONObject bonusRulesJson = JSONObject.parseObject(mcard.getBalanceRules());
			if(bonusRulesJson !=null && bonusRulesJson.getInteger("init_increase_balance") != null){
				initBalance = bonusRulesJson.getInteger("init_increase_balance");
			}
		}
		
		//调用微信接口激活会员卡
		Map<String, String> memberShipMap = ParaMap.create()
				.put("init_bonus", initBonus.toString())
				.put("init_bonus_record", "首次激活会员卡")
				.put("init_balance", initBalance.toString())
				.put("membership_number", buyerCard.getUserCardCode())
				.put("code", buyerCard.getUserCardCode())
				.put("card_id", buyerCard.getCardId())
				.getData(); 
		
		final String accessToken = authUserService.getAccessToken(authUserService.findById(cardActiveParam.getAuthUserId()));
		
		ApiResult activeResult = CompCardApi.memberCard(accessToken, cardActiveParam.getAppId(), JsonKit.toJson(memberShipMap));
		if(activeResult.isAccessTokenInvalid() || !activeResult.isSucceed())
			throw new CardException("激活失败，调用微信激活接口出错" + activeResult.getErrorMsg());
		
		if(buyerCard.getStatus() !=1){
			buyerCard.setStatus(1);
			buyerCard.update();
		}
		return mcard.getCardId();
	}
	
	@Override
	public TreeMap<String, Object> rechargeCard(Long buyerId, Long cardId, String recharge, String clientIp) throws CardException {
		if(buyerId == null || cardId == null) throw new CardException("调用充值接口缺少必要参数");
		BuyerUser buyer = buyerUserdao.findById(buyerId);
		if(buyer == null) throw new CardException(buyerId+ ",用户不存在");
		if(StrKit.isBlank(recharge)){
			throw new CardException("充值金额不能为空");
		}
		BigDecimal totalMoney = new BigDecimal(recharge);
		if(totalMoney.compareTo(new BigDecimal(0)) == -1){
			throw new CardException("充值金额不可为零");
		}
		
		AuthUser authUser = authUserService.findById(buyer.getAppId());
		if(authUser == null) throw new CardException("授权公众号不存在");
		
		Card mcard = cardDao.findById(cardId);
		if(mcard == null) throw new CardException("会员卡不存在");
		
		//获取当前充值会员卡信息，需要激活
		BuyerCard buyerCard = buyerCardDao.findFirst("select * from " + BuyerCard.table + " where buyer_id=? and card_id=? and status=1 ", buyerId, mcard.getCardId());
		if(buyerCard == null) throw new CardException("充值失败，会员卡激没有激活");
		//获取该会员卡旧的积分以及余额信息
		Map<String, String> paramsCardInfoMap = ParaMap.create().put("card_id", buyerCard.getCardId()).put("code", buyerCard.getUserCardCode()).getData();
		ApiResult cardInfoResult = CompCardApi.memberCardInfo(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsCardInfoMap));
		if(!cardInfoResult.isSucceed()) throw new PayException(cardInfoResult.getErrorMsg());
		String user_card_status = cardInfoResult.getStr("user_card_status");
		if(!"NORMAL".equals(user_card_status)) throw new CardException("充值失败，会员卡状态不可用,当前状态值：" + user_card_status);
		
		//插入一条预充值记录, 如果当前用户对应金额有微支付记录的话，不重复插入数据库
		BuyerRecharge br = rechargeDao.findFirst(" select * from " + BuyerRecharge.table + 
				" where buyer_id=? and card_id=? and recharge=? and active=0", buyerId, mcard.getCardId(), recharge);
		if(br == null){
			br = new BuyerRecharge();
			br.setBuyerId(buyerId);
			br.setCardId(mcard.getCardId());
			br.setOutTradeId("c-" + getTradeNo().substring(2));
			br.setRecharge(totalMoney);
			br.setActive(false);
			br.setCreated(new Date());
			br.setUpdated(new Date());
			br.save();
		}
		//生成本次交易号tradeNo，同步tradeNo 保证唯一
//		try {
//			TreeMap<String, Object> params = payService.prepareToPay(buyer.getOpenId(), br.getOutTradeId(), totalMoney, "会员卡充值", clientIp, authUser);
//			return params;
//		} catch (PayException e) {
//			throw new CardException(e.getMessage());
//		}
		return null;
	}

	@Override
	public TreeMap<String, Object> rechargeCardRank(Long buyerId, Long cardId, Long rankId, String clientIp)
			throws CardException {
		if(buyerId == null || cardId == null || rankId == null) throw new CardException("调用充值接口缺少必要参数");
		BuyerUser buyer = buyerUserdao.findById(buyerId);
		if(buyer == null) throw new CardException(buyerId+ ",用户不存在");
		MemberRank rank = mbRankdao.findById(rankId);
		if(rank == null){
			throw new CardException("会员等级不存在");
		}
		BigDecimal totalMoney = rank.getFirstCharge();
		if(totalMoney.compareTo(new BigDecimal(0)) == -1){
			throw new CardException("充值金额不可为零");
		}
		AuthUser authUser = authUserDao.findById(buyer.getAppId());
		if(authUser == null) throw new CardException("授权公众号不存在");
		
		Card mcard = cardDao.findById(cardId);
		if(mcard == null) throw new CardException("会员卡不存在");
		
		//获取当前充值会员卡信息，需要激活
		BuyerCard buyerCard = buyerCardDao.findFirst("select * from " + BuyerCard.table + " where buyer_id=? and card_id=? and status=1 ", buyerId, mcard.getCardId());
		if(buyerCard == null) throw new CardException("充值失败，会员卡激没有激活");
		//获取该会员卡旧的积分以及余额信息
		Map<String, String> paramsCardInfoMap = ParaMap.create().put("card_id", buyerCard.getCardId()).put("code", buyerCard.getUserCardCode()).getData();
		ApiResult cardInfoResult = CompCardApi.memberCardInfo(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsCardInfoMap));
		if(!cardInfoResult.isSucceed()) throw new PayException(cardInfoResult.getErrorMsg());
		String user_card_status = cardInfoResult.getStr("user_card_status");
		if(!"NORMAL".equals(user_card_status)) throw new CardException("充值失败，会员卡状态不可用,当前状态值：" + user_card_status);
		
		//插入一条预充值记录, 如果当前用户对应金额有微支付记录的话，不重复插入数据库
		BuyerRecharge br = rechargeDao.findFirst(" select * from " + BuyerRecharge.table + 
				" where buyer_id=? and card_id=? and member_rank_id=? and active=0", buyerId, mcard.getCardId(), rank.getId());
		if(br == null){
			br = new BuyerRecharge();
			br.setBuyerId(buyerId);
			br.setCardId(mcard.getCardId());
			br.setMemberRankId(rankId);
			br.setOutTradeId("c-" + getTradeNo().substring(2));
			br.setRecharge(totalMoney);
			br.setActive(false);
			br.setCreated(new Date());
			br.setUpdated(new Date());
			br.save();
		}
		//生成本次交易号tradeNo，同步tradeNo 保证唯一
//		try {
//			TreeMap<String, Object> params = payService.prepareToPay(buyer.getOpenId(), br.getOutTradeId(), totalMoney, "购买会员等级", clientIp, authUser);
//			return params;
//		} catch (PayException e) {
//			throw new CardException(e.getMessage());
//		}
		return null;
	}
	
	private CardResultDto convert2CardDto(Card card){
		JSONObject jsonMap = JSONObject.parseObject(card.getBaseInfo());
		String cardName = jsonMap.getString("title");
		String dateInfo = jsonMap.getString("date_info");
		String brandName = jsonMap.getString("brand_name");
		String brandLogo = jsonMap.getString("logo_url");
		String status = jsonMap.getString("status");
		String sku = jsonMap.getString("sku");
		JSONObject skuJsonMap = JSONObject.parseObject(sku);
		Integer quantity = skuJsonMap.getInteger("quantity");
		Integer totalQuantity = skuJsonMap.getInteger("total_quantity");
		String cardId = card.getCardId();
		CardResultDto cardDto = new CardResultDto();
		cardDto.setId(card.getId());
		cardDto.setCardId(cardId);
		cardDto.setCardName(cardName);
		cardDto.setBrandName(brandName);
		cardDto.setBrandLogo(brandLogo);
		cardDto.setQuantity(quantity);
		cardDto.setTotalQuantity(totalQuantity);
		cardDto.setDateInfo(dateInfo);
		cardDto.setStatus(status);
		if("CARD_STATUS_NOT_VERIFY".equals(cardDto.getStatus())){
			cardDto.setStatusCn("待审核");
		}else if("CARD_STATUS_VERIFY_FALL".equals(cardDto.getStatus())){
			cardDto.setStatusCn("审核失败");
		}else if("CARD_STATUS_VERIFY_OK".equals(cardDto.getStatus())){
			cardDto.setStatusCn("通过审核");
		}else if("CARD_STATUS_DELETE".equals(cardDto.getStatus())){
			cardDto.setStatusCn("已删除");
		}else if("CARD_STATUS_DISPATCH".equals(cardDto.getStatus())){
			cardDto.setStatusCn("已投放");
		}else{
			cardDto.setStatusCn("未知状态");
		}
		//日期处理
		JSONObject dateInfoJsonMap = JSONObject.parseObject(cardDto.getDateInfo());
		String dateType = dateInfoJsonMap.getString("type");
		if("DATE_TYPE_FIX_TIME_RANGE".equals(dateType)){
			//固定日期区间
			String begin = DateTimeUtil.FORMAT_YYYY_MM_DD.format(new Date(dateInfoJsonMap.getLong("begin_timestamp")*1000));
			String end = DateTimeUtil.FORMAT_YYYY_MM_DD.format(new Date(dateInfoJsonMap.getLong("end_timestamp")*1000));
			cardDto.setDateInfo(begin + " 到 " + end);
		}else if("DATE_TYPE_FIX_TERM".equals(dateType)){//固定时长，自领取后按天算
			cardDto.setDateInfo("自领取后" + dateInfoJsonMap.getInteger("fixed_term")+"天内有效");
		}else if("DATE_TYPE_PERMANENT".equals(dateType)){//永久有效
			cardDto.setDateInfo("永久有效");
		}else{
			cardDto.setDateInfo("未知时效");
		}
		return cardDto;
	}

	@Override
	public String putIn(AuthUser authUser, String Data,Integer type) throws CardException {
		if(authUser == null || StrKit.isBlank(Data))
			throw new CardException("投放会员卡缺少必要参数");
		JSONObject postData = JSONObject.parseObject(Data);
		logger.debug("json:" + postData.toString());
		String accessToken = authUserService.getAccessToken(authUser);
		ApiResult result=null;
		if(type==2){
			result=CompMessageApi.send(accessToken, authUser.getAppId(), postData.toString());
		}else{
			result=CompMessageApi.sendAll(accessToken, authUser.getAppId(), postData.toString());
		}
		if(result == null || !result.isSucceed() || result.isAccessTokenInvalid()) throw new CardException("投放会员卡失败," + result != null ? result.getErrorMsg() : "");
		return result.getLong("msg_id").toString();
	}

	@Override
	public Card getCardByUser(Long buyerId) {
		if(buyerId == null) return null;
		BuyerUser user = buyerUserdao.findById(buyerId);
		if(user == null) return null;
		List<BuyerCard> bcards = buyerCardDao.find("select * from " + BuyerCard.table + " where buyer_id=? and status=1", buyerId);
		if(bcards == null || bcards.size()<=0) return null;
		
		AuthUser authUser = authUserService.findById(user.getAppId());
		
		Map<String, String> paramsCardInfoMap = ParaMap.create().put("card_id",  bcards.get(0).getCardId()).put("code",  bcards.get(0).getUserCardCode()).getData();
		ApiResult cardInfoResult = CompCardApi.memberCardInfo(authUserService.getAccessToken(authUser), authUser.getAppId(), JsonKit.toJson(paramsCardInfoMap));
		if(!cardInfoResult.isSucceed() || !"NORMAL".equals(cardInfoResult.getStr("user_card_status"))) return null;
		return cardDao.findFirst("select * from " + Card.table + " where card_id=? ", bcards.get(0).getCardId());
	}

	@Override
	public BuyerCard getUserBuyerCard(String openId, String cardId, String userCode) {
		return buyerCardDao.findFirst("select * from " + BuyerCard.table 
				+ " where open_id=? and card_id=? and user_card_code=?"
				, openId, cardId, userCode);
	}

	@Override
	public void getWechatCard(String openId, String cardId, String userCode) {
		BuyerUser buyer = buyerUserdao.findFirst("select * from t_buyer_user where open_id=? ", openId);
		if(buyer != null){
			Card card = cardDao.findFirst("select * from " + Card.table + " where card_id=? ", cardId);
			if(card != null){
				//领取会员卡后，标志该用户为商城会员
				BuyerCard buyerCard = buyerCardDao.findFirst(" select * from " + BuyerCard.table + " where open_id=? and user_card_code=? ", 
						buyer.getOpenId(), userCode);
				if(buyerCard == null){
					buyerCard = new BuyerCard();
					buyerCard.setBuyerId(buyer.getId());
					buyerCard.setCardId(cardId);
					buyerCard.setOpenId(buyer.getOpenId());
					buyerCard.setUserCardCode(userCode);
					if(card.getAutoActivate() != null && "true".equals(card.getAutoActivate())){
						//自动激活
						buyerCard.setStatus(1);//已激活
					}else{
						//需要手动激活会员卡
						buyerCard.setStatus(0);//未激活
					}
					buyerCard.setActive(true);
					buyerCard.setCreated(new Date());
					buyerCard.setUpdated(new Date());
					buyerCard.save();
				}
			}
		}		
	}

}