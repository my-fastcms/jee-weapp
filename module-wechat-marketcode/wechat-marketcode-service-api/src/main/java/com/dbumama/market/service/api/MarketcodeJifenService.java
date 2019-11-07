package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.MarketcodeJifen;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MarketcodeJifenService  {

	//code here
	
	MarketcodeJifen findByCodeactive(Long codeactiveId);

	//gen code begin =============================================================================================
    public MarketcodeJifen findById(Object id);
    public List<MarketcodeJifen> findAll();
    public boolean deleteById(Object id);
    public boolean delete(MarketcodeJifen model);
    public Object save(MarketcodeJifen model);
    public Object saveOrUpdate(MarketcodeJifen model);
    public boolean update(MarketcodeJifen model);
	public List<MarketcodeJifen> findByColumns(Columns columns);
    public Page<MarketcodeJifen> paginate(int page, int pageSize);
    public Page<MarketcodeJifen> paginateByColumns(int page, int pageSize, Columns columns);
    public Page<MarketcodeJifen> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
    //gen code end ================================================================================================
}