package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.PlatImages;
import com.dbumama.market.service.api.ImagepathResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface PlatImagesService  {
	
	public Page<ImagepathResultDto> paginate(Long platUserId, int pageNo, int pageSize);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public PlatImages findById(Object id);


    /**
     * find all model
     *
     * @return all <PlatImages
     */
    public List<PlatImages> findAll();


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
    public boolean delete(PlatImages model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(PlatImages model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(PlatImages model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(PlatImages model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<PlatImages> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<PlatImages> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<PlatImages> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}