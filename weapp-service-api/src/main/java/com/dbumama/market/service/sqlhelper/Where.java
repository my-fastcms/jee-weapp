package com.dbumama.market.service.sqlhelper;

import java.io.Serializable;

/**
 * 
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
class Where implements Serializable{
	private String field;
	private String field2;
    private Object value;
    private Object value2;
    private int type;		//相等，或者<= 或 其他
    
    public Where(String field, String field2, Object value, Object value2, int type) {
        this.field = field;
        this.field2 = field2;
        this.value = value;
        this.value2 = value2;
        this.type = type;
    }
    
    public Where(String field, Object value, int type) {
        this.field = field;
        this.value = value;
        this.type = type;
    }
    
    public Where(String field, int type){
    	this.field = field;
    	this.type = type;
    }
    
    public String getField2() {
        return field2;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue2() {
        return value2;
    }

    public Object getValue() {
        return value;
    }
    
    public int getType() {
        return type;
    }

    //以下为包访问权限
    void setValue(Object value) {
        this.value = value;
    }
}
