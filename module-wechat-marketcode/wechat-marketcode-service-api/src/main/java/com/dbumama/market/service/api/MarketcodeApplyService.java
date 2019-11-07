package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.MarketcodeApply;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface MarketcodeApplyService  {
	
	public MarketcodeApply findByIsvAppApplicationId(String isvApplicationId, Integer applicationId);
	public MarketcodeApply findByAppApplicationId(Long appId, String applicationId);
	public void apply(Long appId, Long codeNum) throws MarketcodeException;
	public ApiResult applyStatus(Long appId, String applicationId, String isvApplicationId);
	public List<String> downloadCode(Long id) throws MarketcodeException;
	public ApiResult ticket2code(Long appId, String openid, String codeTicket) throws MarketcodeException;
	void process(String isvApplicationId, String code, Integer applicationId, String wxaAppId, String openid) throws MarketcodeException;
	
    public MarketcodeApply findById(Object id);
    public List<MarketcodeApply> findAll();
    public boolean deleteById(Object id);
    public boolean delete(MarketcodeApply model);
    public Object save(MarketcodeApply model);
    public Object saveOrUpdate(MarketcodeApply model);
    public boolean update(MarketcodeApply model);
	public List<MarketcodeApply> findByColumns(Columns columns);
    public Page<MarketcodeApply> paginate(int page, int pageSize);
    public Page<MarketcodeApply> paginateByColumns(int page, int pageSize, Columns columns);
    public Page<MarketcodeApply> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
}