package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.RolePermission;
import com.dbumama.market.service.api.RolePermissionService;
import com.dbumama.market.service.base.WxmServiceBase;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class RolePermissionServiceImpl extends WxmServiceBase<RolePermission> implements RolePermissionService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RolePermissionService#getPermissionByRole(java.lang.Long)
	 */
	@Override
	public List<RolePermission> getPermissionByRole(Long roleId) {
		return DAO.find("select * from " + RolePermission.table + " where role_id=? and active=1 ", roleId);
	}

}