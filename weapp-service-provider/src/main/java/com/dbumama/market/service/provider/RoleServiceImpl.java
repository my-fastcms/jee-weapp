package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dbumama.market.model.Employee;
import com.dbumama.market.model.Menu;
import com.dbumama.market.model.Role;
import com.dbumama.market.model.RolePermission;
import com.dbumama.market.service.api.EmployeeService;
import com.dbumama.market.service.api.MenuService;
import com.dbumama.market.service.api.RolePermissionService;
import com.dbumama.market.service.api.RoleService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class RoleServiceImpl extends WxmServiceBase<Role> implements RoleService {

	private static final String MENU_CACHENAME = "menu_cache";
	private static final String MENU_CACHENAME_KEY = "menu_key_";
	
	private static final String MENU_CHILDRED_CACHENAME = "menu_children_cache";
	private static final String MENU_CHILDREN_CACHENAME_KEY = "menu_children_key_";
	
	private RolePermission rolePermDao = new RolePermission().dao();
	
	@Inject
	private EmployeeService employeeService;
	@Inject
	private MenuService menuService;
	@Inject
	private RolePermissionService rolePermissionService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RoleService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<Role> list(Long sellerId, Integer pageNo, Integer pageSize, Integer active) {
		return DAO.paginate(pageNo, pageSize, "select * ", 
				" from " + Role.table + " where (seller_id is null or seller_id=?) and active=? ", sellerId, active);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RoleService#save(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@CacheEvict(name = MENU_CACHENAME, key = "*")
	@Before(Tx.class)
	public void save(Long sellerId, Long appId, Long id, String roleName, String roleDesc, String menuids, Integer active) throws WxmallBaseException {
		if(sellerId == null) throw new WxmallBaseException("seller id is null");
		if(StrKit.isBlank(roleName)) throw new WxmallBaseException("请输入角色名称");
		if(StrKit.isBlank(roleDesc)) throw new WxmallBaseException("请输入角色描述");
		if(StrKit.isBlank(menuids)) throw new WxmallBaseException("请选择角色权限");
		
		Employee _employee = employeeService.findBySellerAndAuthUser(sellerId, appId);
		if(_employee == null || _employee.getIsOwner() == null || _employee.getIsOwner() == false){
			throw new WxmallBaseException("公众号拥有者才有权限增加角色");
		}
		
		Jboot.getCache().removeAll(MENU_CHILDRED_CACHENAME);
		
		Role role = findById(id);
		if(role == null){
			role = new Role();
			role.setSellerId(sellerId).setIsSystem(false);
		}
		
		if(sellerId.intValue() != role.getSellerId().intValue()) throw new WxmallBaseException("你没有权限操作");
		
		if(role.getIsSystem()) throw new WxmallBaseException("系统官方角色不可编辑");
		
		role.setActive(active == null || active==1 ? true : false).setName(roleName).setDescription(roleDesc);
		
		try {
			saveOrUpdate(role);
		} catch (Exception e) {
			throw new WxmallBaseException("save role error");
		}
		
		
		String menuidsArr [] = menuids.split(",");
		
		if(menuidsArr == null || menuidsArr.length <=0) throw new WxmallBaseException("请选择角色权限");
		
		List<RolePermission> rolePerms = rolePermDao.find("select * from " + RolePermission.table + " where role_id=? ", role.getId());
		if(rolePerms != null && rolePerms.size()>0){//编辑的时候，已有权限的情况
			//check 权限是否发生变化
			List<Long> olderPerms = new ArrayList<Long>();//原来的权限
			for(RolePermission rolePerm : rolePerms){
				olderPerms.add(rolePerm.getPermissionId());
			}
			String olderSbuff = sort(olderPerms);
			
			//新的权限值
			List<Long> newsPerms = new ArrayList<Long>();
			for(String menuid : menuidsArr){
				newsPerms.add(Long.valueOf(menuid));
			}
			String newsSbuff = sort(newsPerms);
			
			if(!newsSbuff.toString().equals(olderSbuff.toString())){
				//说明权限发生变化 删除旧的权限，插入新的权限
				Db.delete("delete from " + RolePermission.table + " where role_id=? ", role.getId());
				List<RolePermission> rolePermissions = new ArrayList<RolePermission>();
				for(String menuid : menuidsArr){
					RolePermission roleperm = new RolePermission();
					roleperm.setRoleId(role.getId());
					roleperm.setPermissionId(Long.valueOf(menuid));
					roleperm.setActive(true);
					roleperm.setCreated(new Date());
					roleperm.setUpdated(new Date());
					rolePermissions.add(roleperm);
				}
				
				try {
					Db.batchSave(rolePermissions, rolePermissions.size());			
				} catch (ActiveRecordException e) {
					throw new WxmallBaseException(e.getMessage());
				}
			}
			
		}else{
			//新增的情况
			List<RolePermission> rolePermissions = new ArrayList<RolePermission>();
			for(String menuid : menuidsArr){
				RolePermission roleperm = new RolePermission();
				roleperm.setRoleId(role.getId());
				roleperm.setPermissionId(Long.valueOf(menuid));
				roleperm.setActive(true);
				roleperm.setCreated(new Date());
				roleperm.setUpdated(new Date());
				rolePermissions.add(roleperm);
			}
			
			try {
				Db.batchSave(rolePermissions, rolePermissions.size());			
			} catch (ActiveRecordException e) {
				throw new WxmallBaseException(e.getMessage());
			}
		}
	}

	//权限值排序
	private String sort(List<Long> perms){
		Collections.sort(perms, new Comparator<Long>(){
			@Override
			public int compare(Long o1, Long o2) {
				return o1.intValue() - o2.intValue();
			}
		});
		
		StringBuffer sbuff = new StringBuffer();
		for(Long op : perms){
			sbuff.append(op.intValue());
		}
		return sbuff.toString();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RoleService#getRoleBySeller(java.lang.Long)
	 */
	@Override
	public List<Role> getRolesBySeller(Long sellerId) {
		return DAO.find("select * from " + Role.table + " where seller_id is null or seller_id=? and active=1 ", sellerId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RoleService#getRoleBySeller(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Role getRoleBySeller(Long sellerId, Long appId) {
		Employee employee = employeeService.findBySellerAndAuthUser(sellerId, appId);
		if(employee == null) return null;
		
		Role role = findById(employee.getRoleId());
		
		return role != null && role.getActive() ? role : null ;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RoleService#getPermissionByRole(java.lang.Long)
	 */
	@Override
	@Cacheable(name = MENU_CACHENAME, key = MENU_CACHENAME_KEY+"#(roleId)#(appId)")
	public List<Menu> getPermissionByRole(Long roleId, Long appId) {
		List<RolePermission> rps = rolePermissionService.getPermissionByRole(roleId);
		Set<Long> menusSet = new HashSet<Long>();
		
		for(RolePermission rp : rps){
			Menu menu = menuService.findById(rp.getPermissionId());
			
			if(menu.getAppId() == null || menu.getAppId().intValue() != appId.intValue()){
				continue;
			}
			
			if(menu.getParentId() == null && menu.getActive()){//一级菜单
				menusSet.add(menu.getId());
				continue;
			}
			
			if(menu.getParentId() != null){
				Menu parent = menuService.findById(menu.getParentId());
				if(parent.getParentId() == null && parent.getActive()){
					menusSet.add(parent.getId());
				}
			}
		}
		
		List<Menu> menus = new ArrayList<Menu>();
		for(Long menuId : menusSet){
			Menu _menu = menuService.findById(menuId);
			if(_menu.getAppId() == appId)
				menus.add(menuService.findById(menuId));
		}
		
		Collections.sort(menus, new Comparator<Menu>(){
			@Override
			public int compare(Menu o1, Menu o2) {
				return o1.getId().intValue() - o2.getId().intValue();
			}
		});
		
		return menus;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.RoleService#getPermissionChildrenByRole(java.lang.Long)
	 */
	@Override
	@Cacheable(name = MENU_CHILDRED_CACHENAME, key = MENU_CHILDREN_CACHENAME_KEY+"#(roleId)#(menuId)")
	public List<Menu> getPermissionChildrenByRole(Long roleId, Long menuId) {
		List<RolePermission> rps = rolePermissionService.getPermissionByRole(roleId);
		
		Set<Long> menuIds = new HashSet<Long>();
		for(RolePermission rp : rps){
			Menu menu = menuService.findById(rp.getPermissionId());
			if(menu.getParentId() != null && menu.getParentId().intValue() == menuId.intValue() && menu.getActive()){
				menuIds.add(menu.getId());
				continue;
			}
			
			if(menu.getParentId() != null){
				Menu parent = menuService.findById(menu.getParentId());
				if(parent.getParentId() != null && parent.getParentId().intValue() == menuId.intValue() && parent.getActive()){
					menuIds.add(parent.getId());
				}
			}
		}
		
		List<Menu> menus = new ArrayList<Menu>();
		for(Long _menuId : menuIds){
			menus.add(menuService.findById(_menuId));
		}
		
		Collections.sort(menus, new Comparator<Menu>(){
			@Override
			public int compare(Menu o1, Menu o2) {
				return o1.getId().intValue() - o2.getId().intValue();
			}
		});
		
		return menus;
	}

	
}