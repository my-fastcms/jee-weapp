package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.model.MarketcodeCodeactive;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface MarketcodeCodeactiveService  {
	
	public MarketcodeCodeactive findByWxaAppApplicationId(String wxaAppId, Integer applicationId);
	public MarketcodeCodeactive findByWxaAppId(String wxaAppId);
	public MarketcodeCodeactive findByAppApplicationId(Long appId, String applicationId);
	public void codeactive(Long id, Long appId, String applicationId, 
			String activityName, String productBrand, String productTitle, String productCode,
			String wxaAppid, String wxaPath, Integer wxaType) throws MarketcodeException;
	
    public MarketcodeCodeactive findById(Object id);
    public List<MarketcodeCodeactive> findAll();
    public boolean deleteById(Object id);
    public boolean delete(MarketcodeCodeactive model);
    public Object save(MarketcodeCodeactive model);
    public Object saveOrUpdate(MarketcodeCodeactive model);
    public boolean update(MarketcodeCodeactive model);
	public List<MarketcodeCodeactive> findByColumns(Columns columns);
    public Page<MarketcodeCodeactive> paginate(int page, int pageSize);
    public Page<MarketcodeCodeactive> paginateByColumns(int page, int pageSize, Columns columns);
    public Page<MarketcodeCodeactive> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
}