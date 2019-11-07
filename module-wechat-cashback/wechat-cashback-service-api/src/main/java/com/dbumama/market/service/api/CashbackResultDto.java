package com.dbumama.market.service.api;

import java.util.Date;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class CashbackResultDto extends AbstractParamDto{
    private Long id;
    private String cashbackMethod;
    private String name;
    private Date startDate;
    private Date endDate;
    private String cashbackStart;
    private Integer cashbackLimit;
    private Integer status;
	
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCashbackMethod() {
		return cashbackMethod;
	}
	public void setCashbackMethod(String cashbackMethod) {
		this.cashbackMethod = cashbackMethod;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getCashbackStart() {
		return cashbackStart;
	}
	public void setCashbackStart(String cashbackStart) {
		this.cashbackStart = cashbackStart;
	}
	public Integer getCashbackLimit() {
		return cashbackLimit;
	}
	public void setCashbackLimit(Integer cashbackLimit) {
		this.cashbackLimit = cashbackLimit;
	}
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
	
}
