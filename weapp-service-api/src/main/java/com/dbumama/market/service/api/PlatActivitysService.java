package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.PlatActivitys;
import io.jboot.db.model.Columns;

import java.util.Date;
import java.util.List;

public interface PlatActivitysService  {
	
	public void save(Long platUserId, Long activityId, String name,
			Date startDate, Date endDate, String explain,
			String imgPath);
	
	public Page<PlatActivitys> list(Long platUserId, int pageNo, int pageSize, String name);

	public PlatActivitys getOnline();
	
	public PlatActivitys findOnlieById(Long activityId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public PlatActivitys findById(Object id);


    /**
     * find all model
     *
     * @return all <PlatActivitys
     */
    public List<PlatActivitys> findAll();


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
    public boolean delete(PlatActivitys model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(PlatActivitys model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(PlatActivitys model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(PlatActivitys model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<PlatActivitys> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<PlatActivitys> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<PlatActivitys> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}