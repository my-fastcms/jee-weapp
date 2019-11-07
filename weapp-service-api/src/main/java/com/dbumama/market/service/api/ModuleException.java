package com.dbumama.market.service.api;

@SuppressWarnings("serial")
public class ModuleException extends RuntimeException{
	public ModuleException (String msg){
		super(msg);
	}

	public ModuleException (String code, String msg){
		super(code, new Throwable(msg));
	}
}
