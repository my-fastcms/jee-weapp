package com.dbumama.market.service.common;

/**
 * wjun_java@163.com
 * 2016年7月8日
 */
@SuppressWarnings("serial")
public abstract class AbstractPageParamDto extends AbstractParamDto{

	private Integer pageNo;
	private Integer pageSize;
	
	protected AbstractPageParamDto(){}
	
	protected AbstractPageParamDto(Long authUserId, Integer pageNo, Integer pageSize){
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.authUserId = authUserId;
	}
	
	protected AbstractPageParamDto(Long authUserId, Integer pageNo){
		this(authUserId, pageNo, 10);
	}
	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
