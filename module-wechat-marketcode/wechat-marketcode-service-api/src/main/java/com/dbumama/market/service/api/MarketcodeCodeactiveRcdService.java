package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.MarketcodeCodeactiveRcd;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MarketcodeCodeactiveRcdService  {
	
	MarketcodeCodeactiveRcd findByAppCodeIndex(Long codeactiveId, Long codeStart, Long codeEnd);
	
	List<MarketcodeCodeactiveRcd> findByCodeactiveId(Long codeactiveId);
	
    public MarketcodeCodeactiveRcd findById(Object id);
    public List<MarketcodeCodeactiveRcd> findAll();
    public boolean deleteById(Object id);
    public boolean delete(MarketcodeCodeactiveRcd model);
    public Object save(MarketcodeCodeactiveRcd model);
    public Object saveOrUpdate(MarketcodeCodeactiveRcd model);
    public boolean update(MarketcodeCodeactiveRcd model);
	public List<MarketcodeCodeactiveRcd> findByColumns(Columns columns);
    public Page<MarketcodeCodeactiveRcd> paginate(int page, int pageSize);
    public Page<MarketcodeCodeactiveRcd> paginateByColumns(int page, int pageSize, Columns columns);
    public Page<MarketcodeCodeactiveRcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
}