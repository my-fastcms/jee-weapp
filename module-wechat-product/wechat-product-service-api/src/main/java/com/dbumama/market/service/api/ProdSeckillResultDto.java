package com.dbumama.market.service.api;

import java.util.Date;

import com.dbumama.market.service.common.AbstractResultDto;

/**
* @author xiezhineng
* @version 创建时间：2019年5月14日 
* @Description 秒杀商品详情页DTO
*/
@SuppressWarnings("serial")
public class ProdSeckillResultDto extends AbstractResultDto{

	private Long seckillProductId;
    private String seckillMoney; 	//秒杀价格
    private Integer seckillStock;			//秒杀库存
    private Date startDate;
	private Date endDate;
	
	
	public Long getSeckillProductId() {
		return seckillProductId;
	}
	public void setSeckillProductId(Long seckillProductId) {
		this.seckillProductId = seckillProductId;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getSeckillMoney() {
		return seckillMoney;
	}
	public void setSeckillMoney(String seckillMoney) {
		this.seckillMoney = seckillMoney;
	}
	public Integer getSeckillStock() {
		return seckillStock;
	}
	public void setSeckillStock(Integer seckillStock) {
		this.seckillStock = seckillStock;
	}

}
