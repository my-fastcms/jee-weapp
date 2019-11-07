package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.WeappTemplate;
import com.dbumama.market.service.api.WeappTemplateService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeappTemplateServiceImpl extends WxmServiceBase<WeappTemplate> implements WeappTemplateService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeappTemplateService#findByModuleId(java.lang.Long)
	 */
	@Override
	public WeappTemplate findByTemplateId(Long templateId) {
		return DAO.findFirst("select * from " + WeappTemplate.table + " where template_id=? ", templateId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeappTemplateService#findList()
	 */
	@Override
	public List<WeappTemplate> findList() {
		return DAO.find("select * from " + WeappTemplate.table + " where active = 1");
	}
	
}