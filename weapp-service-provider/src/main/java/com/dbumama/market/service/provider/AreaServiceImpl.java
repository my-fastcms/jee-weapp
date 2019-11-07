package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Area;
import com.dbumama.market.service.api.AreaResultDto;
import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Db;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class AreaServiceImpl extends WxmServiceBase<Area> implements AreaService {
	@Override
	public List<Area> findRoots() {
		return DAO.find("SELECT * FROM t_area area WHERE area.parent_id IS NULL ORDER BY id ASC ");
	}

	@Override
	public List<Area> getChildren(Long areaId) {
		return DAO.find("SELECT * FROM `t_area` WHERE parent_id = ? ORDER BY orders ASC", areaId);
	}

	@Override
	public String getAreaName(String[] id) {
		
		String areaName="";
        if(id.length>0){
        	for (int i = 0; i < id.length; i++) {
        		Area area = findById(id[i]);
    			areaName=areaName+area.getName()+",";
    		}
        	areaName=areaName.substring(0,areaName.length()-1);
		}
		
		return areaName;
	}

	@Override
	public List<AreaResultDto> getAreaResultDto(String[] id) {
		List<AreaResultDto> resultDtos=new ArrayList<AreaResultDto>();
        if(id.length>0){
        	for (int i = 0; i < id.length; i++) {
        		Area area = findById(id[i]);
    			AreaResultDto dto=new AreaResultDto();
    			dto.setId(new Long(id[i]));
    			dto.setName(area.getName());
    			resultDtos.add(dto);
    		}
		}
		return resultDtos;
	}

	
	@Override
	public TreeMap<String, String> list(Long parentId) {
		List<Area> areas = new ArrayList<Area>();
		Area parent = findById(parentId);
		if (parent != null) {
			areas = getChildren(parent.getId());
		} else {
			areas = findRoots();
		}
		TreeMap<String, String> options = new TreeMap<String, String>();
		for (Area area : areas) {
			options.put(area.getId()+"", area.getName());
		}
		return options;
	}

	@Override
	public Object areaMore() {
		List<Area> listRoot = findRoots();
		JSONArray jsonArray = new JSONArray();
		for (Area area : listRoot) {
			//一级对象
			JSONObject json = new JSONObject();
			JSONArray jsonArrayChild = new JSONArray();
			json.put("Id", area.getId());
			json.put("Name", area.getName());
			
			//二级对象
			List<Area> listChild = getChildren(area.getId());
			JSONArray jsonArrayLast = new JSONArray();
			for (Area areaChild : listChild) {
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("Id", areaChild.getId());
				jsonChild.put("Name", areaChild.getName());
				jsonArrayChild.add(jsonChild);
				
				//三级对象
				List<Area> lastChilds = getChildren(areaChild.getId());
				if(lastChilds != null && lastChilds.size()>0){
					JSONArray jsonArrayLastChild = new JSONArray();
					for (Area lastChild : lastChilds) {
						JSONObject jsonLastChild = new JSONObject();
						jsonLastChild.put("Id", lastChild.getId());
						jsonLastChild.put("Name", lastChild.getName());
						jsonArrayLastChild.add(jsonLastChild);
					}
					//三级对象存入二级中
					jsonChild.put("ChildrenList", jsonArrayLastChild);
					jsonArrayLast.add(jsonChild);
				}
			}
			//二级对象存入一级中
			json.put("ChildrenList", jsonArrayChild);
			jsonArray.add(json);
		}
		return jsonArray;
	}

	@Override
	public String findNameById(Integer id) {
		return Db.queryStr("SELECT name FROM "+ Area.table +" WHERE id = ? ", id);
	}
	
}