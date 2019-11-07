package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Area;
import com.dbumama.market.service.api.AreaResultDto;

import io.jboot.db.model.Columns;

import java.util.List;
import java.util.TreeMap;

public interface AreaService  {

	public List<Area> findRoots();     //顶级地区
	public List<Area> getChildren(Long areaId);   //下级地区
	
	public String getAreaName(String[] id);//通过数组Id得到地区名称
	
	public List<AreaResultDto> getAreaResultDto(String[] id);
	
	public TreeMap<String, String> list(Long parentId);
	
	public Object areaMore();
	
	/**
	 * 获取地区的名字
	 * @param id
	 */
	public String findNameById(Integer id);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Area findById(Object id);


    /**
     * find all model
     *
     * @return all <Area
     */
    public List<Area> findAll();


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
    public boolean delete(Area model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Area model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Area model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Area model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Area> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Area> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Area> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}