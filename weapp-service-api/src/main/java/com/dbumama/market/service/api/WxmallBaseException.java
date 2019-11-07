package com.dbumama.market.service.api;

/**
 * wjun_java@163.com
 * 2016年7月4日
 */
@SuppressWarnings("serial")
public class WxmallBaseException extends RuntimeException{

	public WxmallBaseException(String message, Throwable cause) {
        super(message, cause);
    }
	
	private Integer errorCode;
	
	/**
	 * @return the errorCode
	 */
	public Integer getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 */
	public WxmallBaseException(String message) {
		super(message);
	}
	
	public WxmallBaseException(Integer errorCode, String message){
		super(message);
		this.errorCode = errorCode;
	}
	
}
