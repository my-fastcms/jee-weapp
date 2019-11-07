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
package com.dbumama.market.service.provider;

import com.dbumama.market.model.AuthUserTemplate;
import com.dbumama.market.model.WeappTemplate;
import com.dbumama.market.service.api.AuthUserTemplateService;
import com.dbumama.market.service.api.WeappTemplateService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

/**
 * @author wangjun
 * 2019年8月8日
 */
@Bean
@RPCBean
public class AuthUserTemplateServiceImpl extends WxmServiceBase<AuthUserTemplate> implements AuthUserTemplateService{

	@Inject
	private WeappTemplateService weappTemplateService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserTemplateService#getAuthUserTemplate(java.lang.Long)
	 */
	@Override
	public AuthUserTemplate getAuthUserTemplate(Long authUserId) {
		return DAO.findFirst("select * from " + AuthUserTemplate.table + " where app_id=? ", authUserId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthUserTemplateService#findWeappTemplate()
	 */
	@Override
	public WeappTemplate findWeappTemplate(Long authUserId) {
		AuthUserTemplate authUserTemplate = getAuthUserTemplate(authUserId);
		if(authUserTemplate == null) return null;
		WeappTemplate weappTemplate = weappTemplateService.findById(authUserTemplate.getTemplateId());
		return weappTemplate;
	}

}
