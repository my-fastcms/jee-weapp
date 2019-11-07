package com.dbumama.market.service.sqlhelper;

import java.io.Serializable;

/**
 * 
 * @author wangjun
 *
 */
interface Type extends Serializable{

	/** int类型 */
    public static final int INT = 0;
    /** string类型 */
    public static final int STRING = 1;
    
    /** 等于 */
    public static final int EQUAL = 0;//等于
    /** 不等 */ 
    public static final int NOT_EQUAL = 1;//不等
    /** 两边都LIKE */
    public static final int LIKE = 10;//两边都LIKE
    /** 左LIKE */
    public static final int LIKE_LEFT = 11;//左LIKE
    /** 右LIKE */
    public static final int LIKE_RIGHT = 12;//右LIKE
    /** OR 两边都LIKE */
    public static final int LIKE_OR = 13;//OR 两边都LIKE
    /** 大于 */
    public static final int THEN_G = 21;//大于
    /** 大于等于 */
    public static final int THEN_GE = 22;//大于等于
    /** 小于 */
    public static final int THEN_L = 23;//小于
    /** 小于等于 */
    public static final int THEN_LE = 24;//小于等于
    /** OR 大于等于 */
    public static final int THEN_GE_OR = 25;//OR 大于等于
    /** is null */
    public static final int IS_NULL = 31;
    /** is not null */
    public static final int IS_NOT_NULL = 32;
    /** in */
    public static final int IN = 33;
    /** not in **/
    public static final int NOT_IN = 34;
    /** between **/
    public static final int BETWEEN = 35;
    
    /** find_in_set */
    public static final int FINDINSET = 41;
    /** find_in_set or */
    public static final int FINDINSET_OR = 42;
    /** not find_in_set */
    public static final int NOT_FINDINSET = 43;
    /** not find_in_set or  */
    public static final int NOT_FINDINSET_OR = 44;
    
    /** 顺序 */
    public static final String ASC = "asc";
    /** 倒序 */
    public static final String DESC = "desc";
    
    /** 左关联 */
    public static final int LEFT = 1;
    /** 右关联 */
    public static final int RIGHT = 2;
    
    /** 查询条目 */
    public static final int COUNT = 0;
    /** 查询列表 */
    public static final int LIST = 1;
    
    /** 分组查询 */
    public static final int GROUP_BY = 51;
	
}
