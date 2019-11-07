package com.dbumama.market.web.controller;

import com.dbumama.market.model.Card;
import com.dbumama.market.model.MemberRank;
import com.dbumama.market.service.api.CardException;
import com.dbumama.market.service.api.CardListParamDto;
import com.dbumama.market.service.api.CardService;
import com.dbumama.market.service.api.MemberRankService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * 会员卡
 * @author drs
 *
 */
@RequestMapping(value="card", viewPath="card")
@RequiresPermissions(value="/card")
public class CardController extends BaseAppAdminController{
	
	@RPCInject
	private CardService cardService;
	@RPCInject
	private MemberRankService memberRankService;
	
    public void index(){
    	render("/card/card_index.html");
    }
    
    public void wxcard(){
    	setAttr("cardType", getPara("cardType"));
    	render("/card/card_wechat_list.html");
    }
    
    public void list(){
    	CardListParamDto cardParam = new CardListParamDto(getAuthUserId(), getPageNo());
    	try {
    		rendSuccessJson(cardService.list(cardParam));
		} catch (CardException e) {
			rendFailedJson(e.getMessage());
		}
    }
    
    public void set(){
    	Card card = cardService.findById(getParaToLong("id"));
    	List<MemberRank> ranks = memberRankService.getAppMemberRanks(getAuthUserId());
    	setAttr("ranks", ranks);
    	setAttr("mcard", card);
    	setAttr("authUser", getAuthUser());   
    	render("/card/card_set.html");
    }
    
    //创建会员卡
   	public void save(){
   		try {
   			final String card = getPara("card");
   			final String supplyBuy = getPara("supply_buy");
   			cardService.save2Weixin(getAuthUser(), card, supplyBuy);
   			rendSuccessJson();			
		} catch (CardException e) {
			rendFailedJson(e.getMessage());
		}
   	}
   	
   	/**
   	 * 更新会员卡
   	 */
   	public void update(){
   		try {
   			final String card = getPara("card");
   			final String supplyBuy = getPara("supply_buy");
   			cardService.update2Weixin(getAuthUser(), card, supplyBuy);
   			rendSuccessJson();			
		} catch (CardException e) {
			rendFailedJson(e.getMessage());
		}
   	}
   	
   	/**
   	 * 跳到群发界面
   	 */
   	public void cput(){
   		String card_id=getPara("id");
   		setAttr("cardId", card_id);
   		render("/card/card_putin.html");
   	}
   	
   	/**
   	 * 投放
   	 */
   	public void putin(){
   		String card = getPara("card");
   		int type=getParaToInt("type");
   		try {
		String msgId=cardService.putIn(getAuthUser(), card,type);
		rendSuccessJson(msgId);
		} catch (CardException e) {
			rendFailedJson(e.getMessage());
		}
   	}
}
