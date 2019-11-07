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
package com.dbumama.market.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.service.api.EmployeeService;
import com.dbumama.market.service.api.RoleService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.web.core.controller.BaseAuthUserController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

/**
 * @author wangjun
 * 2017年8月17日
 */
@RequestMapping(value="employee")
@RequiresPermissions(value="/employee")
public class EmployeeController extends BaseAuthUserController{

	@RPCInject
	EmployeeService employeeService;
	@RPCInject
	RoleService roleService;
	
	public void index(){
		setAttr("roles", roleService.getRolesBySeller(getSellerId()));
		render("employee_index.html");
	}
	
	public void list(){
		rendSuccessJson(employeeService.list(getSellerId(), getAuthUserId(), getParaToLong("roleId"), getParaToInt("active"), getPageNo(), getPageSize()));
	}
	
	public void edit(){
		setAttr("roles", roleService.getRolesBySeller(getSellerId()));
		setAttr("employee", employeeService.findById(getPara("id")));
		render("employee_edit.html");
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "phone", message = "请输入角色名称"),
        @Form(name = "emplName", message = "请输入角色描述"),
        @Form(name = "roleId", message = "请选择角色权限")
	})
	public void save(Long id, String phone, String emplName, Long roleId, Integer active){
		
		try {
			employeeService.save(getSellerId(), getAuthUserId(), id, phone, emplName, roleId, active);
			rendSuccessJson();			
		} catch (WxmallBaseException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}

