package com.dbumama.market.web.controller;

import com.dbumama.market.model.Card;
import com.dbumama.market.model.MemberRank;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseMobileController;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "user")
public class UserController extends BaseMobileController{

	@RPCInject
	private CardService cardService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private MemberRankService memberRankService;
	@RPCInject
	BuyerProdService buyerProdService;
	
	public void index(){
		if(getBuyerUser().getMemberRankId() != null){
			MemberRank rank = memberRankService.findById(getBuyerUser().getMemberRankId());
			setAttr("rank", rank);
		}
		
		//获取收藏商品数目
		Long count = buyerProdService.getProdCountByBuyer(getBuyerId());
		if(count != null){
			setAttr("count",count);
		}
		render("/user/index.html");
	}
	
	/**
	 * 会员卡充值
	 */
	public void recharge(){
		Long cardId = getParaToLong("cardId");
		setAttr("cardId", cardId);
		Card card = cardService.findById(cardId);
		setAttr("card", card);
		if(card.getSupplyBuy() != null && "true".equals(card.getSupplyBuy())){
			List<MemberRank> ranks = memberRankService.getAppMemberRanks(getAuthUserId());
			setAttr("ranks", ranks);
			render("/user/rech_card_rank.html");
		}else{
			render("/user/rech_card.html");			
		}
	}
	
	/**
	 * 去到会员卡激活界面
	 */
	public void active_card(){
		Long cardId = getParaToLong("cardId");
		setAttr("cardId", cardId);
		render("/user/active_card.html");
	}
	
	//激活会员卡
	public void activecard(){
		final String phone = getPara("phone");
		final String phoneCode = getPara("phoneCode");
		final String code = getPara("code");
		final String codeInSession = getSession().getAttribute("captcha") == null ? "" : getSession().getAttribute("captcha").toString();
		CardActiveParamDto cardActiveParam = new CardActiveParamDto(getBuyerId(), getAuthUserId(), getParaToLong("cardId"), phone, phoneCode, code, codeInSession);
		cardActiveParam.setAppId(authUserService.getAuthUserByAppId(getAppId()).getAppId());
		try {
			cardService.activeCard(cardActiveParam);
			rendSuccessJson();
		} catch (CardException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}
