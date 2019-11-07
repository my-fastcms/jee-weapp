package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ImageGroup;
import com.dbumama.market.service.api.ImageGroupResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface ImageGroupService  {
	
	public List<ImageGroupResultDto> getGroup(Long sellerId); //得到图片分组数据

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ImageGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <ImageGroup
     */
    public List<ImageGroup> findAll();


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
    public boolean delete(ImageGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ImageGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ImageGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ImageGroup model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ImageGroup> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ImageGroup> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ImageGroup> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}