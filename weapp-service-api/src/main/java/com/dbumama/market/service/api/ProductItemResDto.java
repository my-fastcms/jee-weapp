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

import java.io.Serializable;

/**
 * @author wangjun
 * 2018年7月5日
 */
@SuppressWarnings("serial")
public class ProductItemResDto implements Serializable{

	private Long itemId;
	private String simpleTitle;
	private String title;
	private String image;
	private Integer quantity;
	private String createdTime;
	private String url;
	private String shareTitle;
	private String price;
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	/**
	 * @return the shareTitle
	 */
	public String getShareTitle() {
		return shareTitle;
	}
	/**
	 * @param shareTitle the shareTitle to set
	 */
	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}
	/**
	 * @return the simpleTitle
	 */
	public String getSimpleTitle() {
		return simpleTitle;
	}
	/**
	 * @param simpleTitle the simpleTitle to set
	 */
	public void setSimpleTitle(String simpleTitle) {
		this.simpleTitle = simpleTitle;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the itemId
	 */
	public Long getItemId() {
		return itemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the createdTime
	 */
	public String getCreatedTime() {
		return createdTime;
	}
	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	
}
