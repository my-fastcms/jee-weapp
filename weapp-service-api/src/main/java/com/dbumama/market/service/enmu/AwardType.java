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
package com.dbumama.market.service.enmu;

/**
 * 奖品类型枚举
 * @author wangjun
 * 2018年10月24日
 */
public enum AwardType {

	/** 未知奖品类型 **/
	UNKNOW_TYPE,
	
	/** 微信卡券 **/
	WECHAT_CARD,
	
	/** 有赞卡券 **/
	YZ_CARD,
	
	/** 自营商品 **/
	OWNER_PROD,
	
	/** 有赞商品 **/
	YZ_PROD,
	
	/** 微信红包 **/
	WECHAT_REDPACK,
	
	/** 自定义回复 **/
	CUSTOM_REPLY,
	
	/** 签到积分 **/
	QIANDAO_JIFEN,

	/** 抽奖使用，未中奖 **/
	LOTTERY_UN_LUCK;

}
