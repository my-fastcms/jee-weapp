package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.service.api.*;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.api.CompCardApi;
import com.dbumama.weixin.api.ParaMap;
import com.jfinal.aop.Inject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Bean
@RPCBean
public class WechatCardServiceImpl implements WechatCardService {

	@Inject
	private AuthUserService authUserService;
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<CardResultDto> list(CardListParamDto cardParamDto, String cardType) {
		if(cardParamDto == null || cardParamDto.getAuthUserId() == null)
			throw new CardException("调用卡券列表接口缺少必要参数");
		
		AuthUser authUser = authUserService.findById(cardParamDto.getAuthUserId());
		String accessToken = authUserService.getAccessToken(authUser);
		
		//从卡券列表接口获取卡券id, 一次全部拉取所有该公众号的卡券
		Map<String, String> params = ParaMap.create("offset", "0").put("count", ""+cardParamDto.getPageSize()).getData();
		ApiResult cardsResult = CompCardApi.batchGetCards(accessToken, authUser.getAppId(), JsonKit.toJson(params));
		if(cardsResult == null || !cardsResult.isSucceed() || cardsResult.isAccessTokenInvalid()) throw new CardException("调用微信批量获取卡券列表接口异常," + cardsResult != null ? cardsResult.getErrorMsg() : "");
		
		List<String> cardIds = new ArrayList<String>();
		cardIds.addAll(cardsResult.getList("card_id_list"));
		
		List<CardResultDto> cardDbs = new ArrayList<CardResultDto>();
		//根据id获取卡券详情
		if(cardIds != null && cardIds.size()>0){
			for(String cardId : cardIds){
				ApiResult cardResult = CompCardApi.getCard(accessToken, authUser.getAppId(), JsonKit.toJson(ParaMap.create("card_id", cardId).getData()));
				if(cardResult !=null && cardResult.isSucceed()){
					//成功的才处理
					JSONObject json = JSONObject.parseObject(cardResult.getJson());
					json = json.getJSONObject("card");
					String card_type = json.getString("card_type");
					if(StrKit.notBlank(card_type)){
						JSONObject jsonCard = json.getJSONObject(card_type.toLowerCase());
						CardResultDto card = new CardResultDto();
						//卡券详情
						String baseInfo = jsonCard.getString("base_info");
						JSONObject baseInfoMap = JSONObject.parseObject(baseInfo);
						//库存
						String sku = baseInfoMap.getString("sku");
						JSONObject skuJsonMap = JSONObject.parseObject(sku);
						Integer quantity = skuJsonMap.getInteger("quantity");
						//日期处理
						String dateInfo = baseInfoMap.getString("date_info");
						JSONObject dateInfoMap = JSONObject.parseObject(dateInfo);;
						String dateType = dateInfoMap.getString("type");
						if("DATE_TYPE_FIX_TIME_RANGE".equals(dateType)){
							//固定日期区间
							String begin = DateTimeUtil.FORMAT_YYYY_MM_DD.format(new Date(dateInfoMap.getLong("begin_timestamp")*1000));
							String end = DateTimeUtil.FORMAT_YYYY_MM_DD.format(new Date(dateInfoMap.getLong("end_timestamp")*1000));
							card.setDateInfo(begin + " 到 " + end);
						}else if("DATE_TYPE_FIX_TERM".equals(dateType)){//固定时长，自领取后按天算
							card.setDateInfo("自领取后" + dateInfoMap.getInteger("fixed_term")+"天内有效");
						}else if("DATE_TYPE_PERMANENT".equals(dateType)){//永久有效
							card.setDateInfo("永久有效");
						}else{
							card.setDateInfo("未知时效");
						}
						card.setCardId(cardId);
						card.setCardType(card_type);
						card.setCardName(baseInfoMap.getString("title"));
						card.setQuantity(quantity);
						if(StrKit.notBlank(cardType)){
							if(card_type.equals(cardType)){
								cardDbs.add(card);
							}
						}else{
							cardDbs.add(card);
						}
					}
				}
			}
		}
		int totalRow = cardDbs.size();
		int totalPage = cardDbs.size() % cardParamDto.getPageSize() == 0 ? cardDbs.size()/cardParamDto.getPageSize() : cardDbs.size()/cardParamDto.getPageSize() + 1;
		int totalPageNo = cardParamDto.getPageNo() -1 ;
		return new Page<CardResultDto> (cardDbs, totalPageNo, cardParamDto.getPageSize(), totalPage, totalRow);
	}

}
