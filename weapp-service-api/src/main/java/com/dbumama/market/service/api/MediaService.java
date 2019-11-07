package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Media;
import com.dbumama.market.service.api.MediaItemNewsResDto;
import com.dbumama.market.service.api.MediaItemResDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface MediaService  {
	
	/**
	 * 分页列出自定义素材表
	 * @param shopId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<Media> list(Long appId, Integer pageNo, Integer pageSize);
	
	/**
	 * 获取微信公众号图文素材
	 * @param appId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<MediaItemNewsResDto> listMediasNews(Long appId, Integer pageNo, Integer pageSize);
	
	/**
	 * 获取微信素材列表
	 * 其他类型（图片、语音、视频）
	 * @param appId
	 * @param type
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<MediaItemResDto> listMediasImage(Long appId, Integer pageNo, Integer pageSize);
	
	public Page<MediaItemResDto> listMediasVoice(Long appId, Integer pageNo, Integer pageSize);
	
	public Page<MediaItemResDto> listMediasVideo(Long appId, Integer pageNo, Integer pageSize);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Media findById(Object id);


    /**
     * find all model
     *
     * @return all <Media
     */
    public List<Media> findAll();


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
    public boolean delete(Media model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Media model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Media model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Media model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Media> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Media> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Media> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}