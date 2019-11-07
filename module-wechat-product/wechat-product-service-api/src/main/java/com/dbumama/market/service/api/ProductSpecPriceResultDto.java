package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * 商品不同规格对用的不同价格跟库存以及物流重量
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class ProductSpecPriceResultDto extends AbstractResultDto {

	private String price;	
	private String stock;
	private String weight;
	private String promPrice;	//优惠价
	private String collagePrice; //拼团价
	private String agentPrice; //分销价
	
	public String getAgentPrice() {
		return agentPrice;
	}
	public void setAgentPrice(String agentPrice) {
		this.agentPrice = agentPrice;
	}
	public String getPromPrice() {
		return promPrice;
	}
	public void setPromPrice(String promPrice) {
		this.promPrice = promPrice;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getCollagePrice() {
		return collagePrice;
	}
	public void setCollagePrice(String collagePrice) {
		this.collagePrice = collagePrice;
	}
}
