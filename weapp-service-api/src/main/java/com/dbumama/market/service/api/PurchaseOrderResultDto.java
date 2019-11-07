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
package com.dbumama.market.service.api;

import java.util.Date;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * @author wangjun
 * 2017年11月3日
 */
@SuppressWarnings("serial")
public class PurchaseOrderResultDto extends AbstractResultDto{

	private String tplName;			//模板名称
	private String purchaseName;	//订购项目
	private String orderFee;		//订单金额
	private String payType;			//支付类型
	private Date created;			//订单创建时间
	private Integer status;			//订单状态 0未支付，1为已支付
	
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the tplName
	 */
	public String getTplName() {
		return tplName;
	}
	/**
	 * @param tplName the tplName to set
	 */
	public void setTplName(String tplName) {
		this.tplName = tplName;
	}
	/**
	 * @return the purchaseName
	 */
	public String getPurchaseName() {
		return purchaseName;
	}
	/**
	 * @param purchaseName the purchaseName to set
	 */
	public void setPurchaseName(String purchaseName) {
		this.purchaseName = purchaseName;
	}
	/**
	 * @return the orderFee
	 */
	public String getOrderFee() {
		return orderFee;
	}
	/**
	 * @param orderFee the orderFee to set
	 */
	public void setOrderFee(String orderFee) {
		this.orderFee = orderFee;
	}
	/**
	 * @return the payType
	 */
	public String getPayType() {
		return payType;
	}
	/**
	 * @param payType the payType to set
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
}
