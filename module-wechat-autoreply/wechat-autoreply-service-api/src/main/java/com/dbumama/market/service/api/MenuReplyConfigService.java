package com.dbumama.market.service.api;

import com.dbumama.market.model.MenuReplyConfig;
import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.AuthUser;

import io.jboot.db.model.Columns;

import java.util.List;

public interface MenuReplyConfigService  {
	
	void save(Long shopId, String menuKey, String menuConfig, String replyRuleConfig) throws WxmallBaseException;
	
	List<MenuReplyConfig> findMenuReplyConfig(Long shopId, String menuKey);
	
	List<MenuReplyConfigResDto> findAllMenuReplyConfig(Long shopId, String menuKey);

	void save(Long authUserId, String menuConfigMap, String accessToken, String authAppId, String menus) throws WxmallBaseException;
	
	void reply(AuthUser authUser, String menuKey, String openid);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MenuReplyConfig findById(Object id);


    /**
     * find all model
     *
     * @return all <MenuReplyConfig
     */
    public List<MenuReplyConfig> findAll();


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
    public boolean delete(MenuReplyConfig model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(MenuReplyConfig model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(MenuReplyConfig model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MenuReplyConfig model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<MenuReplyConfig> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<MenuReplyConfig> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<MenuReplyConfig> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}