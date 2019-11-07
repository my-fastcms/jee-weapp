package com.dbumama.market.service.sqlhelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;

/**
 * jfinal sql动态查询通用类
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class QueryHelper implements Serializable{
	private String select;
	private String sqlExceptSelect;
	private List<Object> params = new ArrayList<Object>();
	private StringBuilder whereBuilder = new StringBuilder(" where 1=1 ");
	private StringBuilder orderBy = new StringBuilder(" order by ");
	
	private List<Where> wheres;
	
	public QueryHelper(){}
	
	public QueryHelper(String select, String sqlExceptSelect){
		this.select = select;
		this.sqlExceptSelect = sqlExceptSelect;
	}
	
	public QueryHelper addWhere(String field, Object value, int type){
		if(StrKit.isBlank(field) || value == null || "".equals(value)) return this;
        if(wheres == null)
			wheres = new ArrayList<Where>();
		Where where = new Where (field, value, type);
		wheres.add(where);
        return this;
    }
	
	public QueryHelper addWhere(String field, String field2, Object value, Object value2, int type){
		if(StrKit.isBlank(field) || StrKit.isBlank(field2) || value == null || "".equals(value)) return this;
        if(wheres == null)
			wheres = new ArrayList<Where>();
		Where where = new Where (field, field2, value, value2, type);
		wheres.add(where);
        return this;
    }
	
	public String getSelect() {
		return select;
	}

	public String getSqlExceptSelect() {
		return sqlExceptSelect + getWhere();
	}
	
	/**
	 * 等于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhere(String field, Object value){
		return addWhere(field, value, Type.EQUAL);
	}
	
	/**
	 * field or field2 模糊查询
	 * (field like value or field2 like value2)
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereLikeOr(String field, String field2, Object value, Object value2){
		return addWhere(field, field2, value, value2, Type.LIKE_OR);
	}
	
	/**
	 * 不等于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereNOT_EQUAL(String field, Object value){
		return addWhere(field, value, Type.NOT_EQUAL);
	}
	
	/**
	 * 大于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereTHEN_G(String field, Object value){
		return addWhere(field, value, Type.THEN_G);
	}
	
	/**
	 * 大于等于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereTHEN_GE(String field, Object value){
		return addWhere(field, value, Type.THEN_GE);
	}
	
	/**
	 * OR 大于等于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereTHEN_GE_OR(String field, Object value){
		return addWhere(field, value, Type.THEN_GE_OR);
	}
	
	/**
	 * 小于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereTHEN_L(String field, Object value){
		return addWhere(field, value, Type.THEN_L);
	}
	
	/**
	 * 小于等于
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereTHEN_LE(String field, Object value){
		return addWhere(field, value, Type.THEN_LE);
	}
	
	/**
	 * between
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryHelper addWhereBETWEEN(String field, Object value){
		return addWhere(field, value, Type.BETWEEN);
	}
	
	public QueryHelper addWhereIn(String field, Object value){
		return addWhere(field, value, Type.IN);
	}
	
	public QueryHelper addWhereNotIn(String field, Object value){
		return addWhere(field, value, Type.NOT_IN);
	}
	
	public QueryHelper addWhereLike(String field, Object value){
		return addWhere(field, value, Type.LIKE);
	}
	
	public QueryHelper addWhereLikeLeft(String field, Object value){
		return addWhere(field, value, Type.LIKE_LEFT);
	}
	
	public QueryHelper addWhereLikeRight(String field, Object value){
		return addWhere(field, value, Type.LIKE_RIGHT);
	}
	
	/**
	 * find_in_set查询 
	 * 包含value所有
	 * @param value
	 * @param field
	 * @return
	 */
	public QueryHelper addWhereFindInSet(String field, Object value){
		return addWhere(field, value, Type.FINDINSET);
	}
	
	/**
	 * find_in_set查询 
	 * 包含value任一
	 * @param value
	 * @param field
	 * @return
	 */
	public QueryHelper addWhereFindInSetOr(String field, Object value){
		return addWhere(field, value, Type.FINDINSET_OR);
	}
	
	/**
	 * find_in_set查询 
	 * 排除value所有
	 * @param value
	 * @param field
	 * @return
	 */
	public QueryHelper addWhereNotFindInSet(String field, Object value){
		return addWhere(field, value, Type.NOT_FINDINSET);
	}
	
	/**
	 * find_in_set查询 
	 * 排除value任一
	 * @param value
	 * @param field
	 * @return
	 */
	public QueryHelper addWhereNotFindInSetOr(String field, Object value){
		return addWhere(field, value, Type.NOT_FINDINSET_OR);
	}
	
	public QueryHelper addWhereNotNull(String field){
		if(StrKit.isBlank(field)) return this;
		if(wheres == null)
			wheres = new ArrayList<Where>();
		Where where = new Where(field, Type.IS_NOT_NULL);
		wheres.add(where);
		return this;
	}
	
	public QueryHelper addIsNull(String field){
		if(StrKit.isBlank(field)) return this;
		if(wheres == null)
			wheres = new ArrayList<Where>();
		Where where = new Where(field, Type.IS_NULL);
		wheres.add(where);
		return this;
	}
	
	/**
	 * 条件 GROUP_BY field 统计每组的记录条数.
	 * @param field
	 * @return
	 */
	public QueryHelper addGroupBy(String field){
		if(StrKit.isBlank(field)) return this;
		if(wheres == null)
			wheres = new ArrayList<Where>();
		Where where = new Where(field, Type.GROUP_BY);
		wheres.add(where);
		return this;
	}
	
	public QueryHelper addOrderBy(String type, String ... fields){
		if(StrKit.isBlank(type) || fields == null) return this;
		for(String f : fields){
			orderBy.append(f).append(",");
		}
		orderBy = orderBy.deleteCharAt(orderBy.length()-1).append(" ").append(type);
		return this;
	}
	
	public QueryHelper build(){
		if(wheres == null) return this;
		for(Where where : wheres){
			buildSQL(where);
		}
		return this;
	}
	
	public String getWhere(){
		return (getOrderBy().contains("desc") || getOrderBy().contains("asc")) ? whereBuilder.append(getOrderBy()).toString() : whereBuilder.toString();
	}
	
	public Object [] getParams(){
		return params.toArray();
	}
	
	public String getOrderBy(){
		return orderBy.toString();
	}
	
    void buildSQL(Where where) {
        Object value = where.getValue();
        switch (where.getType()) {
        case Type.LIKE:
        	whereBuilder.append(" and " + where.getField() + " like ? ");
			params.add("%"+value+"%");
            break;
        case Type.LIKE_LEFT:
        	whereBuilder.append(" and " + where.getField() + " like ? ");
			params.add("%"+value);
            break;
        case Type.LIKE_RIGHT:
        	whereBuilder.append(" and " + where.getField() + " like ? ");
			params.add(value+"%");
            break;
        case Type.EQUAL:
        	whereBuilder.append(" and " + where.getField() + " = ? ");
			params.add(value);
        	break;
        case Type.LIKE_OR:
        	Object value2 = where.getValue2();
        	whereBuilder.append(" and ( " + where.getField() + " like ? or " + where.getField2() + " like ? )");
			params.add("%"+value+"%");
			params.add("%"+value2+"%");
        	break;
        case Type.THEN_G:
        	whereBuilder.append(" and " + where.getField() + " > ? ");
			params.add(value);
            break;
        case Type.THEN_GE:
        	whereBuilder.append(" and " + where.getField() + " >= ? ");
			params.add(value);
            break;
        case Type.THEN_L:
        	whereBuilder.append(" and " + where.getField() + " < ? ");
			params.add(value);
            break;
        case Type.THEN_LE:
        	whereBuilder.append(" and " + where.getField() + " <= ? ");
			params.add(value);
            break;
        case Type.THEN_GE_OR:
        	whereBuilder.append(" or " + where.getField() + " >= ? ");
			params.add(value);
            break;
        case Type.NOT_EQUAL:
        	whereBuilder.append(" and " + where.getField() + " != ? ");
			params.add(value);
            break;
        case Type.IS_NULL:
        	whereBuilder.append(" and " + where.getField() + " is null ");
            break;
        case Type.IS_NOT_NULL:
        	whereBuilder.append(" and " + where.getField() + " is not null ");
        	break;
        case Type.BETWEEN :
        	if(value instanceof List){
        		@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) value;
        		if(list!= null && list.size()==2){
        			whereBuilder.append(" and (" + where.getField() + " between ? and ? )");
        			for(Object obj : list){
        				params.add(obj);
        			}
        		}
        	}
        	break;
        case Type.IN:
        	if(value instanceof List){
        		@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) value;
        		if(list != null && list.size()>0){
        			StringBuffer instr = new StringBuffer();
                	whereBuilder.append(" and " + where.getField() + " in (");
                	for (Object obj : list) {
                	    instr.append("?").append(",");
                	    params.add(obj);
                	}
                	instr.deleteCharAt(instr.length() -1);
                	whereBuilder.append(instr + ") ");        			
        		}
        	}
            break;
        case Type.NOT_IN:
        	if(value instanceof List){
        		@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) value;
        		if(list != null && list.size()>0){
        			StringBuffer instr = new StringBuffer();
                	whereBuilder.append(" and " + where.getField() + " not in (");
                	for (Object obj : list) {
                	    instr.append("?").append(",");
                	    params.add(obj);
                	}
                	instr.deleteCharAt(instr.length() -1);
                	whereBuilder.append(instr + ") ");        			
        		}
        	}
        	break;
        case Type.FINDINSET_OR:
        	if(value instanceof List){
	        	@SuppressWarnings("unchecked")
	        	List<Object> list = (List<Object>) value;
	        	if(list != null && list.size()>0){
		        	StringBuffer instr = new StringBuffer();
		        	whereBuilder.append(" and (");
					for(Object obj : list){
						String tag = (String) obj;
	                	instr.append(" find_in_set(").append("?").append(","+where.getField()+") or");
	                	params.add(tag);
					}
					instr.delete(instr.length()-3,instr.length());
					whereBuilder.append(instr + ") ");
	        	}
        	}
        	break;
        case Type.FINDINSET:
        	if(value instanceof List){
	        	@SuppressWarnings("unchecked")
	        	List<Object> list = (List<Object>) value;
	        	if(list != null && list.size()>0){
		        	StringBuffer instr = new StringBuffer();
		        	whereBuilder.append(" and (");
					for(Object obj : list){
						String tag = (String) obj;
	                	instr.append(" find_in_set(").append("?").append(","+where.getField()+") and");
	                	params.add(tag);
					}
					instr.delete(instr.length()-4,instr.length());
					whereBuilder.append(instr + ") ");
	        	}
        	}
        	break;
        case Type.NOT_FINDINSET_OR:
        	if(value instanceof List){
	        	@SuppressWarnings("unchecked")
	        	List<Object> list = (List<Object>) value;
	        	if(list != null && list.size()>0){
		        	StringBuffer instr = new StringBuffer();
		        	whereBuilder.append(" and ");
					for(Object obj : list){
						String tag = (String) obj;
	                	instr.append(" not find_in_set(").append("?").append(","+where.getField()+") and");
	                	params.add(tag);
					}
					instr.delete(instr.length()-4,instr.length());
					whereBuilder.append(instr);
	        	}
        	}
        	break;
        case Type.NOT_FINDINSET:
        	if(value instanceof List){
	        	@SuppressWarnings("unchecked")
	        	List<Object> list = (List<Object>) value;
	        	if(list != null && list.size()>0){
		        	StringBuffer instr = new StringBuffer();
		        	whereBuilder.append(" and (");
					for(Object obj : list){
						String tag = (String) obj;
	                	instr.append(" not find_in_set(").append("?").append(","+where.getField()+") or");
	                	params.add(tag);
					}
					instr.delete(instr.length()-3,instr.length());
					whereBuilder.append(instr + ") ");
	        	}
        	}
        	break;
        case Type.GROUP_BY:
        	whereBuilder.append(" GROUP BY " + where.getField() + " ");
        	break;
        }
    }
	
}
