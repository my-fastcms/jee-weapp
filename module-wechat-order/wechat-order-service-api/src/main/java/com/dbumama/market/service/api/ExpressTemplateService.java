package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.ExpressComp;
import com.dbumama.market.model.ExpressImg;
import com.dbumama.market.model.ExpressTemplate;

import io.jboot.db.model.Columns;

import java.util.List;
import java.util.Map;

public interface ExpressTemplateService  {

	/**
	 * 获取用户支持的快递公司列表，排除用户已添加设置过的快递模板
	 * @return
	 * @throws OrderException
	 */
	List<ExpressComp> getUserExpComps (Long appId) throws OrderException;
	
	List<ExpressTemplate> getUserExpTemplate(Long appId);
	
	/**
	 * 根据快递公司，获取快递模块的背景图
	 * @param expKey
	 * @return
	 * @throws OrderException
	 */
	List<ExpressImg> getExpTemplateBackImage(String expKey) throws OrderException;
	
	/**
	 * 获取用户指定的快递模板
	 * @param key
	 * @return
	 * @throws OrderException
	 */
	ExpressTemplate getUserExpTemplateByKey(String key, Long appId) throws OrderException; 
	
	/**
	 * 保存用户添加的快递模板
	 * @param expTemplateDto
	 * @return
	 * @throws OrderException
	 */
	ExpressTemplate saveTemplate(ExpTplSaveParamDto expTemplateDto) throws OrderException;
	
	/**
	 * 删除用户的模板
	 * @param key
	 * @throws OrderException
	 */
	void delTemplate(String key, Long appId) throws OrderException;
	
	/**
	 * 初始化快递模板指标数据
	 * @return
	 */
	Map<String, List<PalletElementResultDto>> initPalletElement(Long appId);
	
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ExpressTemplate findById(Object id);


    /**
     * find all model
     *
     * @return all <ExpressTemplate
     */
    public List<ExpressTemplate> findAll();


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
    public boolean delete(ExpressTemplate model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ExpressTemplate model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(ExpressTemplate model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ExpressTemplate model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<ExpressTemplate> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<ExpressTemplate> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<ExpressTemplate> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}