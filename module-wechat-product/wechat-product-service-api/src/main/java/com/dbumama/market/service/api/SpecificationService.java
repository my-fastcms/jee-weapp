package com.dbumama.market.service.api;

import com.jfinal.plugin.activerecord.Page;
import com.dbumama.market.model.Specification;
import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.api.SpecificationParamDto;
import com.dbumama.market.service.api.SpecificationResultDto;
import com.dbumama.market.service.api.SpecificationsResultDto;

import io.jboot.db.model.Columns;

import java.util.List;

public interface SpecificationService  {
	
	Page<SpecificationsResultDto> list (Long appId, Integer pageNo, Integer pageSize, Integer active);
	
	public List<SpecificationResultDto> findAll(SpecificationParamDto specificationParamDto);

	public SpecificationResultDto getSpeciAndVaules(Long specId);
	
	public List<SpecificationValue> getSpeciValues(Long specId);

	public Specification save(Specification specification, String items, Long appId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Specification findById(Object id);


    /**
     * find all model
     *
     * @return all <Specification
     */
    public List<Specification> findAll();


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
    public boolean delete(Specification model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(Specification model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if save or update success
     */
    public Object saveOrUpdate(Specification model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Specification model);


    /**
     * page query
     *
     * @param page
     * @param pageSize
     * @return page data
     */
    public Page<Specification> paginate(int page, int pageSize);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @return page data
     */
    public Page<Specification> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * page query by columns
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return page data
     */
    public Page<Specification> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


}