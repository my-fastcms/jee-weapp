package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.MarketcodeScanrcd;
import io.jboot.db.model.Columns;

import java.util.List;

public interface MarketcodeScanrcdService  {

	//code here
	
	MarketcodeScanrcd findByIsvAppAndCode(String isvApplicationId, Integer applicationId, String code);

	//gen code begin =============================================================================================
    public MarketcodeScanrcd findById(Object id);
    public List<MarketcodeScanrcd> findAll();
    public boolean deleteById(Object id);
    public boolean delete(MarketcodeScanrcd model);
    public Object save(MarketcodeScanrcd model);
    public Object saveOrUpdate(MarketcodeScanrcd model);
    public boolean update(MarketcodeScanrcd model);
	public List<MarketcodeScanrcd> findByColumns(Columns columns);
    public Page<MarketcodeScanrcd> paginate(int page, int pageSize);
    public Page<MarketcodeScanrcd> paginateByColumns(int page, int pageSize, Columns columns);
    public Page<MarketcodeScanrcd> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
    //gen code end ================================================================================================
}