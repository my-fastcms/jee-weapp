package com.dbumama.market.service.provider;

import java.util.Date;
import java.util.List;

import com.dbumama.market.model.Employee;
import com.dbumama.market.model.Role;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.EmployeeService;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class EmployeeServiceImpl extends WxmServiceBase<Employee> implements EmployeeService {

	@Inject
	private SellerUserService sellerUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.EmployeeService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<Record> list(Long sellerId, Long appId, Long roleId, Integer active, Integer pageNo, Integer pageSize) {
		String select = "select e.*, r.name ";
		String sqlExceptSelect = " from " + Employee.table + " e "
					+ " left join " + Role.table + " r on e.role_id=r.id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("e.seller_id", sellerId)
		.addWhere("e.app_id", appId).addWhere("e.role_id", roleId).addWhere("e.active", active).addOrderBy("desc", "e.created", "e.updated");
		
		helper.build();
		
		return Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.EmployeeService#save(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public void save(Long sellerId, Long appId, Long id, String phone, String name, Long roleId, Integer active) throws WxmallBaseException {
		if(sellerId == null) throw new WxmallBaseException("sellerId is null");
		if(appId == null) throw new WxmallBaseException("appId is null");
		if(StrKit.isBlank(name)) throw new WxmallBaseException("员工姓名不能为空");
		if(StrKit.isBlank(phone)) throw new WxmallBaseException("登录账号不能为空");
		if(roleId == null) throw new WxmallBaseException("请选择员工角色");
		
		Employee _employee = findBySellerAndAuthUser(sellerId, appId);
		if(_employee == null || _employee.getIsOwner() == null || _employee.getIsOwner() == false){
			throw new WxmallBaseException("公众号拥有者才有权限增加员工");
		}
		
		//添加谁为员工
		SellerUser seller = sellerUserService.findByPhone(phone);
		if(seller == null || seller.getActive() == null || seller.getActive() == 0){
			throw new WxmallBaseException("员工账号不存在，请核实");
		}
		
		Employee employee = findById(id);
		
		if(employee == null){
			
			Employee emplCheck = DAO.findFirst("select * from " + Employee.table + " where user_id=? and app_id=? ", seller.getId(), appId);
			if(emplCheck != null && emplCheck.getActive() != null && emplCheck.getActive()==true){
				throw new WxmallBaseException("员工信息已存在，不可重复添加");
			}
			
			if(emplCheck !=null && emplCheck.getActive() !=null && emplCheck.getActive() == false){
				employee = emplCheck;
			}else{
				employee = new Employee();				
				employee.setSellerId(sellerId).setUserId(seller.getId()).setAppId(appId).setPhone(phone).setCreated(new Date()).setIsOwner(false);
			}
			
		}
		
		if(sellerId.intValue() != employee.getSellerId().intValue()) throw new WxmallBaseException("您不是员工创建者，没有操作权限");
		
		if(employee.getIsOwner()){
			throw new WxmallBaseException("当前员工是公众号/小程序拥有者，不可更改拥有者权限");
		}
		
		employee.setEmplName(name).setRoleId(roleId).setActive(active==null || active==1 ? true : false);
		
		saveOrUpdate(employee);
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.EmployeeService#findBySellerAndAuthUser(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Employee findBySellerAndAuthUser(Long sellerId, Long appId) {
		return DAO.findFirst("select * from " + Employee.table + " where user_id=? and app_id=? ", sellerId, appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.EmployeeService#findBySeller(java.lang.Long)
	 */
	@Override
	public List<Employee> findBySeller(Long sellerId, Long appId) {
		return DAO.find("select * from " + Employee.table + " where seller_id=? and app_id=? and active =1 ", sellerId, appId);
	}

}