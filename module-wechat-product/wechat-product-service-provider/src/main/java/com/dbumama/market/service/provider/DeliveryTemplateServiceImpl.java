package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.DeliverySet;
import com.dbumama.market.model.DeliveryTemplate;
import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.service.api.DeliverySetResultDto;
import com.dbumama.market.service.api.DeliveryTemplateEditResultDto;
import com.dbumama.market.service.api.DeliveryTemplateException;
import com.dbumama.market.service.api.DeliveryTemplateResultDto;
import com.dbumama.market.service.api.DeliveryTemplateService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class DeliveryTemplateServiceImpl extends WxmServiceBase<DeliveryTemplate> implements DeliveryTemplateService {

	private static final DeliverySet deliverySetDao = new DeliverySet().dao();
	private static final DeliveryTemplate deliveryTpldao = new DeliveryTemplate().dao();
	
	@Inject
	private AreaService areaService;
	
	@Override
	public DeliveryTemplate save(DeliveryTemplate dt, String items)
			throws DeliveryTemplateException {
		if(StrKit.isBlank(items))
			throw new DeliveryTemplateException("运费模板参数出错");
		
		try {
			if(dt.getId() == null) dt.save(); else dt.update();
		} catch (Exception e) {
			throw new DeliveryTemplateException("系统异常，保存出错");
		}
		
		JSONArray jsonArray = JSONArray.parseArray(items);
		
		if(jsonArray == null || jsonArray.size()<=0) throw new DeliveryTemplateException("items is null");
		
		for(int i=0;i<jsonArray.size();i++){
			JSONObject json = jsonArray.getJSONObject(i);
			String data=json.getString("data");
			JSONArray jsonDelivery = JSONArray.parseArray(data);
			for(int j=0;j<jsonDelivery.size();j++){
				JSONObject jsonSet = jsonDelivery.getJSONObject(j);
				DeliverySet ds = null;
				if(jsonSet.getLong("id") == null) {
					ds = new DeliverySet();
					ds.setActive(1);
					ds.setTemplateId(dt.getId());
				}else{
					ds = deliverySetDao.findById(jsonSet.getLong("id"));
				}
				
				if(ds == null) continue;
				
				ds.setValuationType(jsonSet.getLong("deliveryType"));
				ds.setAreaId(jsonSet.getString("areaId"));
				ds.setStartStandards(jsonSet.getInteger("startStandards"));
				ds.setStartFees(jsonSet.getBigDecimal("startFees"));
				ds.setAddStandards(jsonSet.getInteger("addStandards"));
				ds.setAddFees(jsonSet.getBigDecimal("addFees"));
				
				try {
					if(ds.getId() == null){
						ds.save();	
					}else{
						ds.update();
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new DeliveryTemplateException("系统异常，保存出错");
				}
			}
		}
		return dt;
	}

	@Override
	public List<DeliverySet> getDeliverySetByTpl(Long templateId) {
		return deliverySetDao.find("SELECT * FROM "+DeliverySet.table+" WHERE template_id = ? ORDER BY updated desc", templateId);
	}

	@Override
	public Page<DeliveryTemplateResultDto> list(Long appId, Integer pageNo, Integer pageSize, Integer active) {
		QueryHelper helper = new QueryHelper("select * ", " from " + DeliveryTemplate.table + " a ");
		helper.addWhere("a.app_id", appId)
		.addWhere("a.active", active)
		.addOrderBy("desc", "a.updated").build();
		
		Page<DeliveryTemplate> deliveryTemplatePage = deliveryTpldao.paginate(pageNo, pageSize, 
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		List<DeliveryTemplateResultDto> dtResults= new ArrayList<DeliveryTemplateResultDto>();
		for (DeliveryTemplate dt : deliveryTemplatePage.getList()) {
			DeliveryTemplateResultDto result=new DeliveryTemplateResultDto();
			result.setId(dt.getId());
			result.setName(dt.getName());
			if(dt.getActive()!=null && dt.getActive().intValue()==0){
				result.setName(dt.getName()+"<font color='red'>(停用)</font>");
			}
			result.setValuationType(dt.getValuationType());
			result.setStartTime((dt.getUpdated()==null ? dt.getCreated() : dt.getUpdated()).toString());
			List<DeliverySet> deliverySetList=new ArrayList<DeliverySet>();
			List<DeliverySet> dsList=getDeliverySetByTpl(dt.getId());
			for (DeliverySet deliverySet : dsList) {
				if(!StrKit.isBlank(deliverySet.getAreaId())){
					deliverySet.setAreaId(areaService.getAreaName(deliverySet.getAreaId().split(",")));
				}
				deliverySetList.add(deliverySet);
			}
			result.setDeliverySetList(deliverySetList);
			dtResults.add(result);
		}
		return new Page<DeliveryTemplateResultDto>(dtResults, deliveryTemplatePage.getPageNumber(), deliveryTemplatePage.getPageSize(), deliveryTemplatePage.getTotalPage(), deliveryTemplatePage.getTotalRow());
	}

	@Override
	public List<DeliveryTemplate> getDelivTemplateByApp(Long appId) {
		return deliveryTpldao.find("select id,name,valuation_type from " + DeliveryTemplate.table + " where app_id = ? and active=1 ", appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.DeliveryTemplateService#findByTemplateId(java.lang.Long)
	 */
	@Override
	public DeliveryTemplateEditResultDto findByTemplateId(Long templateId) {
		
		DeliveryTemplate dt = findById(templateId);
		
		if(dt == null) return null;
		
		DeliveryTemplateEditResultDto dtResultDto = new DeliveryTemplateEditResultDto();
		
		dtResultDto.setId(dt.getId());
		dtResultDto.setActive(dt.getActive());
		dtResultDto.setName(dt.getName());
		dtResultDto.setValuationType(dt.getValuationType());
		List<DeliverySet> dsList = getDeliverySetByTpl(dt.getId());
		List<DeliverySetResultDto> numList = new ArrayList<DeliverySetResultDto>();
		List<DeliverySetResultDto> weightList = new ArrayList<DeliverySetResultDto>();
		for (DeliverySet deliverySet : dsList) {
			DeliverySetResultDto setResultDto = new DeliverySetResultDto();
			setResultDto.setId(deliverySet.getId());
			setResultDto.setAreaId(deliverySet.getAreaId());
			setResultDto.setStartStandards(deliverySet.getStartStandards());
			setResultDto.setStartFees(deliverySet.getStartFees());
			setResultDto.setAddStandards(deliverySet.getAddStandards());
			setResultDto.setAddFees(deliverySet.getAddFees());
			if (!StrKit.isBlank(deliverySet.getAreaId())) {
				String[] areaId = deliverySet.getAreaId().split(",");
				setResultDto.setAreaResultDto(areaService.getAreaResultDto(areaId));
			}
			if (deliverySet.getValuationType() == 1) {
				numList.add(setResultDto);
				dtResultDto.setNumList(numList);
			}
			if (deliverySet.getValuationType() == 2) {
				weightList.add(setResultDto);
				dtResultDto.setWeightList(weightList);
			}
		}
		
		return dtResultDto;
	}

	
}