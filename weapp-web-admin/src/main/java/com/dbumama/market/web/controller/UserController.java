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

import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.SellerUserService;
import com.dbumama.market.service.api.UserException;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

/**
 * @author wangjun
 * 2017年11月14日
 */
@RequestMapping(value = "user")
public class UserController extends BaseAdminController {

	@RPCInject
	private SellerUserService sellerUserService;
	
	public void index(){
		setAttr("seller", sellerUserService.findById(getSellerId()));
		render("user_index.html");
	}
	
	public void setpwd(){
		render("user_pwd.html");
	}
	
	@Before(POST.class)
	public void savepwd(){
		final String code = getPara("code");
		final String password = getPara("password");
		final String confirmPwd = getPara("confirmPwd");
		
		try {
			sellerUserService.setPwd(getSellerId(), code, password, confirmPwd);
			rendSuccessJson();
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void tradepwd(){
		render("user_tradepwd.html");
	}
	
	/**
	 * 设置交易密码
	 */
	@Before(POST.class)
	public void saveTradepwd(){
		final String code = getPara("code");
		final String password = getPara("password");
		final String confirmPwd = getPara("confirmPwd");
		
		try {
			sellerUserService.setTradePwd(getSellerId(), code, password, confirmPwd);
			rendSuccessJson();
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "header_img", message = "请上传头像"),
	})
	public void update(final String header_img, final Long id){
		try {
			SellerUser seller = sellerUserService.findById(id);
			if(seller == null) {
				rendFailedJson("账号异常，不允许修改");
				return;
			}
			
			seller.setHeaderImg(header_img);
			sellerUserService.update(seller);

			rendSuccessJson();
		} catch (Exception e) {
			e.printStackTrace();
			rendFailedJson(e.getMessage());
		}
	}
	
	public void bindPhone(){
		render("user_bindPhone.html");
	}
	
	@Before(POST.class)
	public void saveUserPhone(){
		final String phone = getPara("phone");
		final String code = getPara("code");
		final String password = getPara("password");
		final String confirmPwd = getPara("confirmPwd");
		
		try {
			sellerUserService.setUserPhone(getSellerId(), phone, code, password, confirmPwd);
			rendSuccessJson();
		} catch (UserException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}
