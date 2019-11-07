/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.service.api;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.Specification;
import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.api.SpecificationDto;
import com.dbumama.market.service.api.SpecificationValueDto;

/**
 * @author wangjun
 * 2018年5月14日
 */
public final class SpecificationUtil {
	
	public static List<SpecificationDto> getSpecDtoList(List<Specification> specList){
		List<SpecificationDto> specDtoList = new ArrayList<SpecificationDto>();
		
		for(Specification spec : specList){
			specDtoList.add(getSpecDto(spec));
		}
		
		return specDtoList;
	}
	
	public static SpecificationDto getSpecDto(Specification spec){
		SpecificationDto specDto = new SpecificationDto();
		specDto.setId(spec.getId());
		specDto.setName(spec.getName());
		specDto.setMemo(spec.getMemo());
		specDto.setOrders(spec.getOrders());
		specDto.setAppId(spec.getAppId());
		specDto.setCreated(spec.getCreated());
		specDto.setUpdated(spec.getUpdated());
		specDto.setActive(spec.getActive());
		return specDto;
	}
	
	public static List<SpecificationValueDto> getSpecValueDtoList(List<SpecificationValue> specValues){
		List<SpecificationValueDto> specificationDtos = new ArrayList<SpecificationValueDto>();
    	for(SpecificationValue spv: specValues){
    		specificationDtos.add(getSpecValueDto(spv));
    	}
    	return specificationDtos;
	}
	
	public static SpecificationValueDto getSpecValueDto(SpecificationValue specValue){
		SpecificationValueDto specValueDto = new SpecificationValueDto();
		specValueDto.setId(specValue.getId());
		specValueDto.setName(specValue.getName());
		specValueDto.setImage(specValue.getImage());
		specValueDto.setOrders(specValue.getOrders());
		specValueDto.setCreated(specValue.getCreated());
		specValueDto.setUpdated(specValue.getUpdated());
		specValueDto.setActive(specValue.getActive());
		return specValueDto;
	}
	
}
