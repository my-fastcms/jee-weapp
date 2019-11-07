/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.service;

import java.util.Date;
import java.util.Random;

import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.MarketcodeCodeactive;
import com.dbumama.market.model.MarketcodeJifen;
import com.dbumama.market.model.MarketcodeScanrcd;
import com.dbumama.market.service.api.BuyerUserService;
import com.dbumama.market.service.api.MarketcodeCodeactiveService;
import com.dbumama.market.service.api.MarketcodeException;
import com.dbumama.market.service.api.MarketcodeJifenService;
import com.dbumama.market.service.api.MarketcodeScanrcdService;
import com.jfinal.kit.Kv;

import io.jboot.components.rpc.annotation.RPCInject;

/**
 * 一物一码积分处理逻辑
 * @author wangjun
 * 2019年8月13日
 */
public class MarketcodeJifenServiceProcess extends AbstractMarketcodeService implements MarketcodeService{
	
	@RPCInject
	private MarketcodeJifenService marketcodeJifenService;
	@RPCInject
	private MarketcodeCodeactiveService marketcodeCodeactiveService;
	@RPCInject
	private MarketcodeScanrcdService marketcodeScanrcdService;
	@RPCInject
	private BuyerUserService buyerUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeService#doProcess(java.lang.String, java.lang.String, com.dbumama.market.model.WeappTemplate)
	 */
	@Override
	public void doProcess(String wxaAppId, Integer applicationId, String isvApplicationId, String openid, String code)
			throws MarketcodeException {
		
		MarketcodeCodeactive marketcodeCodeactive = marketcodeCodeactiveService.findByWxaAppApplicationId(wxaAppId, applicationId);
		if(marketcodeCodeactive == null) throw new MarketcodeException("该二维码不存在激活信息");
		
		//读取积分配置
		MarketcodeJifen marketcodeJifen = marketcodeJifenService.findByCodeactive(marketcodeCodeactive.getId());
		if(marketcodeJifen == null) throw new MarketcodeException("该积分营销二维码不存在积分配置");
		
		MarketcodeScanrcd marketcodeScanrcd = marketcodeScanrcdService.findByIsvAppAndCode(isvApplicationId, applicationId, code);
		if(marketcodeScanrcd != null) throw new MarketcodeException("该码已经被扫过，请换一个码"); 
		
		//根据积分配置，给用户增加积分
		marketcodeScanrcd = new MarketcodeScanrcd();
		marketcodeScanrcd.setIsvApplicationId(isvApplicationId)
						.setApplicationId(String.valueOf(applicationId))
						.setWxaAppid(wxaAppId)
						.setCode(code)
						.setOpenid(openid)
						.setActive(true)
						.setCreated(new Date())
						.setUpdated(new Date());
		marketcodeScanrcdService.save(marketcodeScanrcd);
		
		int score = 0;
		
		try {
			if(marketcodeJifen.getJifenType() == 1){
				//固定积分
				score = marketcodeJifen.getJifenNum();
			}else{
				//随机积分
				score = new Random().nextInt(marketcodeJifen.getMaxJifenNum() - marketcodeJifen.getMinJifenNum() + 1) + marketcodeJifen.getMinJifenNum();
			}
		} catch (Exception e) {
			score = 0;
		}
		
		score = score <=0 ? 0 : score;
		
		if(score >0 ){
			try {
				BuyerUser buyer = buyerUserService.findByOpenId(openid);
				if(buyer != null){
					final int oldScore = buyer.getScore() == null ? 0 : buyer.getScore();
					buyer.setScore(oldScore+ score);
					buyerUserService.update(buyer);
				}
			} catch (Exception e) {
				throw new MarketcodeException(e.getMessage());
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeService#render(java.lang.Long)
	 */
	@Override
	public String render(MarketcodeCodeactive marketcodeCodeactive) throws MarketcodeException {
		Kv data = Kv.by("marketcodeCodeactive", marketcodeCodeactive);
		MarketcodeJifen marketcodeJifen = marketcodeJifenService.findByCodeactive(marketcodeCodeactive.getId());
		data.set("marketcodeJifen", marketcodeJifen);
		return engine.getTemplate("/com/dbumama/market/service/ui_jifen_template.jf").renderToString(data);
	}
	
}
