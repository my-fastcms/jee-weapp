package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.MemberRank;
import com.dbumama.market.service.api.CustomerException;

import io.jboot.db.model.Columns;

import java.util.List;

public interface MemberRankService  {

	/**
	 * 会员等级列表
	 * @param appId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws CustomerException
	 */
	public List<MemberRank> list(Long appId) throws CustomerException;
	
	List<MemberRank> getAppMemberRanks(Long appId);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MemberRank findById(Object id);


    /**
     * find all model
     *
     * @return all <MemberRank
     */
    public List<MemberRank> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(MemberRank model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MemberRank model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MemberRank model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MemberRank model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MemberRank> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MemberRank> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MemberRank> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}