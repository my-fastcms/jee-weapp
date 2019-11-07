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
 * 红包场景值
 * @author wangjun
 * 2018年11月14日
 */
public enum RedpackSceneIdType {

	PRODUCT_0(""),
	PRODUCT_1("PRODUCT_1"), //商品促销
	PRODUCT_2("PRODUCT_2"), //抽奖
	PRODUCT_3("PRODUCT_3"),	//虚拟物品兑奖
	PRODUCT_4("PRODUCT_4"), //企业内部福利
	PRODUCT_5("PRODUCT_5"),	//渠道分润
	PRODUCT_6("PRODUCT_6"), //保险回馈
	PRODUCT_7("PRODUCT_7"), //彩票派奖
	PRODUCT_8("PRODUCT_8"); //税务刮奖

    private final String value;

    RedpackSceneIdType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
	
}
