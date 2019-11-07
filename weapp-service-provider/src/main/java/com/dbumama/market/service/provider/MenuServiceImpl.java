package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Employee;
import com.dbumama.market.model.Menu;
import com.dbumama.market.model.Role;
import com.dbumama.market.model.RolePermission;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.AuthUserType;
import com.dbumama.market.service.enmu.MenuType;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Bean
@RPCBean
public class MenuServiceImpl extends WxmServiceBase<Menu> implements MenuService {
//	private static final String MENU_CACHENAME = "menu_cache";
//	private static final String MENU_CACHENAME_KEY = "menu_key_";
//	private static final String MENU_CHILDREN_CACHENAME_KEY = "menu_children_key_";
	
	private static final String MENU_ITEM_CACHENAME = "menu_item_cache";
	private static final String MENU_ITEM_CACHENAME_KEY = "menu_item_cache_key_";
	
	@Inject
	private RolePermissionService rolePermissionService;
	@Inject
	private EmployeeService employeeService;
	@Inject
	private RoleService roleService;
	
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.menu.MenuService#getMenus(java.lang.Long)
	 */
	@Override
	public List<Menu> getMenus(Long sellerId, Long authUserId, Long appId) {
		Employee employee = employeeService.findBySellerAndAuthUser(sellerId, authUserId);
		if(employee == null) return null;
		
		Role role = roleService.findById(employee.getRoleId());
		if(role == null || !role.getActive()) return null;
		
		return roleService.getPermissionByRole(role.getId(), appId);
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MenuService#getChildren(java.lang.Long)
	 */
	@Override
	public List<Menu> getChildren(Long sellerId, Long authUserId, Long menuId) {
		if(authUserId == null || menuId == null) return null;
		
		Employee employee = employeeService.findBySellerAndAuthUser(sellerId, authUserId);
		if(employee == null) return null;
		
		Role role = roleService.findById(employee.getRoleId());
		if(role == null || !role.getActive()) return null;
		
		return roleService.getPermissionChildrenByRole(role.getId(), menuId);
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MenuService#getMenus()
	 */
	@Override
	public List<JsTreeResultDto> getJsTreeMenus(Long roleId, Boolean readonly) {
		List<RolePermission> rolePerms = rolePermissionService.getPermissionByRole(roleId);
		
		List<Menu> menus = DAO.find("select * from " + Menu.table + " where active=1 and parent_id is null");
		List<JsTreeResultDto> jsTreeDtos = new ArrayList<JsTreeResultDto>();
		for(Menu menu : menus){
			JsTreeResultDto jsTreeDto = getJsTreeDto(menu, rolePerms, readonly);
			jsTreeDtos.add(jsTreeDto);
		}
		
//		HashMap<String, Object> result = new HashMap<String ,Object>();
//		result.put("text", "权限树");
//		JSONObject state = new JSONObject();
//		state.put("opened", true);
//		result.put("state", state);
//		result.put("children", jsTreeDtos);
		return jsTreeDtos;
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MenuService#getJsTreeChildren(java.lang.Long)
	 */
	@Override
	public List<JsTreeResultDto> getJsTreeChildren(Long menuId, Long roleId, Boolean readonly) {
		List<RolePermission> rolePerms = rolePermissionService.getPermissionByRole(roleId);
		List<JsTreeResultDto> children = new ArrayList<JsTreeResultDto>();
		List<Menu> menus = DAO.find("select * from " + Menu.table + " where parent_id=?", menuId);
		for(Menu menu : menus){
			JsTreeResultDto departDto = getJsTreeDto(menu, rolePerms, readonly);
			children.add(departDto);
		}
		return children;
	}

	private JsTreeResultDto getJsTreeDto(Menu menu, List<RolePermission> rolePerms, Boolean readonly){
		JsTreeResultDto jsTreeDto = new JsTreeResultDto();
		jsTreeDto.setId(menu.getId());
		jsTreeDto.setParent(menu.getParentId());
		jsTreeDto.setText(menu.getMenuName());
		if(Db.queryLong("select count(id) from " + Menu.table + " where parent_id=?", menu.getId()) <= 0){
			JSONObject state = new JSONObject();
			state.put("opened", true);
			state.put("selected", hasPerm(menu, rolePerms));
			state.put("disabled", readonly.booleanValue());
			jsTreeDto.setState(state);
			jsTreeDto.setChildren(false);//是否有下级
			jsTreeDto.setIcon("none");
		}else{
			JSONObject state = new JSONObject();
			state.put("selected", hasPerm(menu, rolePerms));
			state.put("opened", true);
			state.put("disabled", readonly.booleanValue());
			jsTreeDto.setState(state);
			jsTreeDto.setChildren(true);//是否有下级
		}
		return jsTreeDto;
	}
	
	private boolean hasPerm(Menu menu, List<RolePermission> rolePerms){
		for(RolePermission rp : rolePerms){
			if(rp.getPermissionId() == menu.getId())
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MenuService#getMenusPerms(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<Menu> getMenusPerms(Long sellerId, Long appId) {
		Employee employee = employeeService.findBySellerAndAuthUser(sellerId, appId);
		if(employee == null) return null;
		
		List<Menu> menus = new ArrayList<Menu>();
		
		Role role = roleService.findById(employee.getRoleId());
		if(role != null && role.getActive() == true){
			List<RolePermission> rps = rolePermissionService.getPermissionByRole(role.getId());
			
			for(RolePermission rp : rps){
				Menu menu = findByIdCache(rp.getPermissionId());
				if(menu != null){
					menus.add(menu);
				}
			}
			
		}
		return menus;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MenuService#getMenuByApp(java.lang.Long)
	 */
	@Override
	public Menu getMenuByApp(Long appId) {
		return DAO.findFirst("select * from "+ Menu.table + " where app_id=? and parent_id is null", appId);
	}

	private List<MenuItem> getMenus(int authUserType) {
		List<Menu> allMenus = DAO.find("select * from " + Menu.table + " where active=1");
		List<MenuItem> allMenuItems = new ArrayList<>();
		List<MenuItem> allRootItems = new ArrayList<>();//菜单

		allMenus.forEach(menu->{
			MenuItem menuItem = new MenuItem();
			menuItem.setId(menu.getId());
			menuItem.setParentId(menu.getParentId());
			menuItem.setAppId(menu.getAppId());
			menuItem.setIcon(menu.getMenuIcon());
			menuItem.setText(menu.getMenuName());
			menuItem.setUrl(menu.getMenuUrl());
			menuItem.setClassId(menu.getClassId());
			menuItem.setMenuType(menu.getMenuType());
			menuItem.setOrder(menu.getMenuOrder());
			if(menu.getMenuGroup() !=null){
				menuItem.setMenuGroup(menu.getMenuGroup());
			}

			allMenuItems.add(menuItem);

			if (menu.getParentId() == null) {// 一级菜单
				if(authUserType == AuthUserType.dingyue.ordinal()){
					if(menu.getMenuType() == MenuType.ALL.ordinal()
							|| menu.getMenuType() == MenuType.DY.ordinal()
							|| menu.getMenuType() == MenuType.FW_DY.ordinal()
							|| menu.getMenuType() == MenuType.DY_XCX.ordinal()
					){//订阅号
						allRootItems.add(menuItem);
					}
				}else if(authUserType == AuthUserType.fuwu.ordinal()){
					if(menu.getMenuType() == MenuType.ALL.ordinal()
							||menu.getMenuType() == MenuType.FW.ordinal()
							|| menu.getMenuType() == MenuType.FW_XCX.ordinal()
							|| menu.getMenuType() == MenuType.FW_DY.ordinal()
					){//服务号
						allRootItems.add(menuItem);
					}
				}else if(authUserType == AuthUserType.xiaochengxu.ordinal()){
					if(menu.getMenuType() == MenuType.ALL.ordinal()
							|| menu.getMenuType() == MenuType.XCX.ordinal()
							|| menu.getMenuType() == MenuType.FW_XCX.ordinal()
							|| menu.getMenuType() == MenuType.DY_XCX.ordinal()
					){//小程序
						allRootItems.add(menuItem);
					}
				}
			}
		});

		allRootItems.forEach(item ->{
			List<MenuItem> childrenItems = getChildren(item.getId(), allMenuItems, authUserType);
			item.setChildren(childrenItems);
		});

		return allRootItems.stream().sorted(Comparator.comparingInt(MenuItem::getOrder)).collect(Collectors.toList());
	}

	@Override
	public List<MenuItem> getXcxMenus() {
		return getMenus(AuthUserType.xiaochengxu.ordinal());
	}

	@Override
	public List<MenuItem> getFwMenus() {
		return getMenus(AuthUserType.fuwu.ordinal());
	}

	@Override
	public List<MenuItem> getDyMenus() {
		return getMenus(AuthUserType.dingyue.ordinal());
	}

	private List<MenuItem> getChildren(Long menuId, List<MenuItem> allItems, int serviceType) {
		List<MenuItem> childrenItems = new ArrayList<MenuItem>();

		allItems.forEach(menu -> {
			if (menu.getParentId() != null && menu.getParentId().intValue() == menuId.intValue()) {
				if(menu.getMenuType() == 0){//0为3中类型都支持的菜单
					childrenItems.add(menu);
				}else{
					if(serviceType == AuthUserType.xiaochengxu.ordinal()
							&& (menu.getMenuType() == MenuType.XCX.ordinal()
							|| menu.getMenuType() == MenuType.DY_XCX.ordinal()
							|| menu.getMenuType() == MenuType.FW_XCX.ordinal())){
						//小程序
						childrenItems.add(menu);
					}else if(serviceType == AuthUserType.dingyue.ordinal()
							&& (menu.getMenuType() == MenuType.DY.ordinal()
							|| menu.getMenuType() == MenuType.DY_XCX.ordinal()
							|| menu.getMenuType() == MenuType.FW_DY.ordinal())){
						//订阅号
						childrenItems.add(menu);
					}else if(serviceType == AuthUserType.fuwu.ordinal()
							&& (menu.getMenuType() == MenuType.FW.ordinal()
							|| menu.getMenuType() == MenuType.FW_XCX.ordinal()
							|| menu.getMenuType() == MenuType.FW_DY.ordinal())){
						//服务号
						childrenItems.add(menu);
					}
				}
			}
		});

		childrenItems.forEach(child ->{
			child.setChildren(getChildren(child.getId(), allItems, serviceType));
		});

		if (childrenItems.size() <= 0)
			return new ArrayList<>();

		return childrenItems.stream().sorted(Comparator.comparingInt(MenuItem::getOrder)).collect(Collectors.toList());
	}

	@Override
	public Menu findByIdCache(Object id) {
		return DAO.findFirstByCache(MENU_ITEM_CACHENAME, MENU_ITEM_CACHENAME_KEY + id.toString(), "select * from " + Menu.table + " where id=? ", id);
	}

}