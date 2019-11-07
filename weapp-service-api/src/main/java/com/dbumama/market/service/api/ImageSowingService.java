package com.dbumama.market.service.api;

import java.util.List;

import com.dbumama.market.model.ImageSowing;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.db.model.Columns;

public interface ImageSowingService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ImageSowing findById(Object id);


    /**
     * find all model
     *
     * @return all <ImageSowing
     */
    public List<ImageSowing> findAll();


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
    public boolean delete(ImageSowing model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ImageSowing model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ImageSowing model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ImageSowing model);

	/**
	 * @param columns
	 * @return
	 */
	public List<ImageSowing> findByColumns(Columns columns);

    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ImageSowing> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ImageSowing> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ImageSowing> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
    
    /**根据AppID查找对应的轮播图*/
    public Page<ImageSowingResultDto> list(Long appId, Integer pageNo, Integer pageSize);
    
    /** 删除对应的轮播图*/
    public void delByIdAndAppid(Long id, Long appId)throws Exception;

    /** 添加和修改*/
	public void mysaveOrUpdate(Long id, Long appId, String sowingImg, String sowingUrl)throws Exception;


}