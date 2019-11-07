package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Specification;
import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.api.SpecificationException;
import com.dbumama.market.service.api.SpecificationParamDto;
import com.dbumama.market.service.api.SpecificationResultDto;
import com.dbumama.market.service.api.SpecificationService;
import com.dbumama.market.service.api.SpecificationUtil;
import com.dbumama.market.service.api.SpecificationValueDto;
import com.dbumama.market.service.api.SpecificationsResultDto;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class SpecificationServiceImpl extends WxmServiceBase<Specification> implements SpecificationService {
	static final Specification specificationDao = new Specification().dao();
	private static final SpecificationValue specValueDao = new SpecificationValue().dao();
	private static final Specification specDao = new Specification().dao();

	@Override
	public List<SpecificationResultDto> findAll(SpecificationParamDto specificationParamDto) {
		if (specificationParamDto == null || specificationParamDto.getAuthUserId() == null) return null;
		List<SpecificationResultDto> results = new ArrayList<SpecificationResultDto>();
		List<Specification> specifications = specDao.find("SELECT * FROM "+Specification.table+" where app_id=? and active=1", specificationParamDto.getAuthUserId());
		for (Specification specifcation : specifications) {
			SpecificationResultDto sdto = new SpecificationResultDto();
			sdto.setSpecification(SpecificationUtil.getSpecDto(specifcation));
			List<SpecificationValue> specificationValues = specValueDao.find("SELECT * FROM "+SpecificationValue.table+" WHERE `specification_id`=? and active=1 ", specifcation.getId());
			sdto.setSpecificationValues(SpecificationUtil.getSpecValueDtoList(specificationValues));
			results.add(sdto);
		}
		return results;
	}

	@Override
	public Specification save(Specification specification, String items, Long appId) {
		if (StrKit.isBlank(items) || appId == null || specification == null)
			throw new SpecificationException("规格值参数异常出错");
		try {
			if(specification.getId() == null)
				specification.save();
			else
				specification.update();
			
		} catch (Exception e) {
			throw new SpecificationException("系统异常，保存出错");
		}
		
		JSONArray jsonArray = JSONArray.parseArray(items);
		if (jsonArray == null || jsonArray.size() <= 0)
			throw new SpecificationException("请填写规格值");

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			if(StrKit.isBlank(json.getString("name")) || json.getInteger("orders")==null) continue;
			
			if (json.getLong("itemId") != null) {
				SpecificationValue values = specValueDao.findById(json.getLong("itemId"));
				values.setSpecificationId(specification.getId());
				values.setName(json.getString("name"));
				values.setOrders(json.getInteger("orders"));
				values.setImage(null);
				if("del".equals(json.get("del"))){
					values.setActive(0);
				}else{
					values.setActive(1);					
				}
				values.setUpdated(new Date());
				try {
					values.update();
				} catch (Exception e) {
					throw new SpecificationException("系统异常，保存出错" + e.getMessage());
				}
			} else {
				if("del".equals(json.get("del"))){
					continue;
				}
				
				SpecificationValue values = new SpecificationValue();
				values.setSpecificationId(specification.getId());
				values.setName(json.getString("name"));
				values.setOrders(json.getInteger("orders"));
				values.setImage(null);
				values.setActive(1);
				values.setUpdated(new Date());
				values.setCreated(new Date());
				try {
					values.save();
				} catch (Exception e) {
					throw new SpecificationException("系统异常，保存出错," + e.getMessage());
				}
			}
		}
		return specification;
	}

	@Override
	public SpecificationResultDto getSpeciAndVaules(Long specId) {
		SpecificationResultDto sdto = new SpecificationResultDto();
		sdto.setSpecification(SpecificationUtil.getSpecDto(specDao.findById(specId)));
		
		List<SpecificationValue> specificationValues = getSpeciValues(specId);
		List<SpecificationValueDto> specificationDtos = new ArrayList<SpecificationValueDto>();
    	for(SpecificationValue spv: specificationValues){
    		specificationDtos.add(SpecificationUtil.getSpecValueDto(spv));
    	}
		sdto.setSpecificationValues(specificationDtos);
		
		return sdto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dbumama.market.service.api.specification.SpecificationService#list(
	 * java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<SpecificationsResultDto> list(Long appId, Integer pageNo, Integer pageSize, Integer active) {

		QueryHelper helper = new QueryHelper("select *", "from " + Specification.table);
		helper.addWhere("app_id", appId).addWhere("active", active).addOrderBy("desc", "updated").build();

		Page<Specification> specificationpage = specDao.paginate(pageNo, pageSize, helper.getSelect(),
				helper.getSqlExceptSelect(), helper.getParams());
		List<SpecificationsResultDto> resultDto = new ArrayList<SpecificationsResultDto>();
		for (Specification s : specificationpage.getList()) {
			SpecificationsResultDto sResultDto = new SpecificationsResultDto();
			sResultDto.setId(s.getId());
			sResultDto.setName(s.getName());
			sResultDto.setCreated(s.getCreated());
			sResultDto.setActive(s.getActive());
			List<SpecificationValue> specificationValues = specValueDao
					.find("select * from " + SpecificationValue.table + " where specification_id=? and active=1 ", s.getId());
			sResultDto.setSpecificationValues(specificationValues);
			resultDto.add(sResultDto);
		}
		Page<SpecificationsResultDto> sresultPage = new Page<>(resultDto, pageNo, pageSize,
				specificationpage.getTotalPage(), specificationpage.getTotalRow());

		return sresultPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dbumama.market.service.api.specification.SpecificationService#
	 * getSpeciValues(java.lang.Long)
	 */
	@Override
	public List<SpecificationValue> getSpeciValues(Long specId) {
		return specValueDao.find(
				"SELECT * FROM " + SpecificationValue.table + " WHERE specification_id =? and active=1 order by orders asc",
				specId);
	}

}