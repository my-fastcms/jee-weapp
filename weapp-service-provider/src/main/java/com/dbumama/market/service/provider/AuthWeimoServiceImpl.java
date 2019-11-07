package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.AuthWeimo;
import com.dbumama.market.model.Employee;
import com.dbumama.market.service.api.AuthWeimoService;
import com.dbumama.market.service.api.EmployeeService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class AuthWeimoServiceImpl extends WxmServiceBase<AuthWeimo> implements AuthWeimoService {

	@Inject
	private EmployeeService employeeService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthWeimoService#findByPid(java.lang.String)
	 */
	@Override
	public AuthWeimo findByPid(String pid) {
		return DAO.findFirst("select * from " + AuthWeimo.table + " where pid=? ", pid);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthWeimoService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<AuthWeimo> list(Long sellerId, Long appId, Integer pageNo, Integer pageSize) {
		Employee employee = employeeService.findBySellerAndAuthUser(sellerId, appId);
		if(employee == null) return null;
		
		//员工
		List<Employee> employees = employeeService.findBySeller(employee.getSellerId(), appId);
		final List<Long> sellerIds = new ArrayList<Long>();
		for(Employee _employee : employees){
			sellerIds.add(_employee.getUserId());
		}
		
		QueryHelper helper = new QueryHelper("select * ", " from " + AuthWeimo.table);
		helper.addWhereIn("seller_id", sellerIds)
		.addOrderBy("desc", "updated").build();
		
		return DAO.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

}