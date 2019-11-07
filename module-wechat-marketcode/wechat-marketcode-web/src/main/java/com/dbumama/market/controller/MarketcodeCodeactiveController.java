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
package com.dbumama.market.controller;

import java.util.Date;
import java.util.List;

import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.MarketcodeApply;
import com.dbumama.market.model.MarketcodeCodeactive;
import com.dbumama.market.model.MarketcodeJifen;
import com.dbumama.market.model.WeappTemplate;
import com.dbumama.market.service.MarketcodeService;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.AuthUserTemplateService;
import com.dbumama.market.service.api.MarketcodeApplyService;
import com.dbumama.market.service.api.MarketcodeCodeactiveRcdService;
import com.dbumama.market.service.api.MarketcodeCodeactiveService;
import com.dbumama.market.service.api.MarketcodeException;
import com.dbumama.market.service.api.MarketcodeJifenService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.ClassUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value = "marketcode/marketcodecodeactive", viewPath="marketcode")
//@RequiresPermissions(value={"/marketcode/marketcodecodeactive"}, logical = Logical.OR)
public class MarketcodeCodeactiveController extends BaseAppAdminController {

    @RPCInject
    private MarketcodeCodeactiveService marketcodeCodeactiveService;
    @RPCInject
    private MarketcodeApplyService marketcodeApplyService;
    @RPCInject
    private MarketcodeCodeactiveRcdService marketcodeCodeactiveRcdService;
    @RPCInject
    private AuthUserService authUserService;
    @RPCInject
    private AuthUserTemplateService authUserTemplateService;
    @RPCInject
    private MarketcodeJifenService marketcodeJifenService;
    
    public void index() {
        render("marketcodecodeactive_index.html");
    }
    
    public void list(){
    	Columns columns = Columns.create();
    	columns.add(Column.create("app_id", getAuthUserId()));
    	renderSuccess(marketcodeCodeactiveService.paginateByColumns(getPageNo(), getPageSize(), columns));
    }

    public void edit(Long id) {
    	MarketcodeApply applyEntry = marketcodeApplyService.findById(id);
    	setAttr("marketcodeApply", applyEntry);
    	if(applyEntry != null){
    		MarketcodeCodeactive entry = marketcodeCodeactiveService.findByAppApplicationId(applyEntry.getAppId(), applyEntry.getApplicationId());    		
            
    		if(entry != null){
    			AuthUser weapp = authUserService.findByAppId(entry.getWxaAppid());
    			WeappTemplate authUserTemplate = authUserTemplateService.findWeappTemplate(weapp.getId());
    			setAttr("weappTemplate", authUserTemplate);
    		}
    		
    		setAttr("marketcodeCodeactive", entry);
    	}
    	
    	List<AuthUser> weapps = authUserService.getSellerAuthUserWeapp(getSellerId());
    	setAttr("weapps", weapps);
        render("marketcodecodeactive_edit.html");
    }
   
    @Before(POST.class)
    @EmptyValidate({ 
    	@Form(name = "applicationId", message = "请输入申请单号"),
    	@Form(name = "activityName", message = "请输入活动名称"),
    	@Form(name = "productBrand", message = "请输入商品品牌"),
    	@Form(name = "productTitle", message = "请输入商品标题"),
    	@Form(name = "productCode", message = "请输入商品条码"),
    	@Form(name = "wxaAppid", message = "请输入小程序的appid"),
    	@Form(name = "wxaPath", message = "请输入小程序的path"),
    	@Form(name = "wxaType", message = "请输入小程序版本")
	})
    public void save(Long id, String applicationId, String activityName, String productBrand, String productTitle, String productCode, String wxaAppid, String wxaPath, Integer wxaType) {
    	try {
    		marketcodeCodeactiveService.codeactive(id, getAuthUserId(), applicationId, activityName, productBrand, productTitle, productCode, wxaAppid, wxaPath, wxaType);
    		renderSuccess();
		} catch (MarketcodeException e) {
			renderFail(e.getMessage());
		}
    }
    
    public void activercd(Long id){
    	setAttr("id", id);
    	render("marketcodecodeactive_rcd.html");
    }
    
    public void activercdList(Long id){
    	renderSuccess(marketcodeCodeactiveRcdService.findByCodeactiveId(id));
    }
    
    @Before(POST.class)
    public void checkWeapp(Long id){
    	//选择的小程序
    	AuthUser authUser = authUserService.findById(id);
    	if(authUser == null){
    		renderFail("authUser is null");
    		return;
    	}
    	
    	WeappTemplate authUserTemplate = authUserTemplateService.findWeappTemplate(authUser.getId());
    	renderSuccess(authUserTemplate);
    }
    
    public void config(Long id){
    	MarketcodeCodeactive marketcodeCodeactive = marketcodeCodeactiveService.findById(id);
    	if(marketcodeCodeactive == null){
    		renderHtml("<div>marketcodeCodeactive is null</div>");
    		return;
    	}
    	
    	final AuthUser weapp = authUserService.findByAppId(marketcodeCodeactive.getWxaAppid());
    	if(weapp == null){
    		renderHtml("<div class=\"alert alert-danger\">weapp is null</div>");
    		return;
    	}
    	
    	WeappTemplate authUserTemplate = authUserTemplateService.findWeappTemplate(weapp.getId());
    	
    	if(authUserTemplate == null){
    		renderHtml("<div class=\"alert alert-danger\">请先设置小程序模板</div>");
    		return;
    	}
    	
    	if(StrKit.isBlank(authUserTemplate.getProcessClass())){
    		renderHtml("<div class=\"alert alert-danger\">未注册小程序模板处理类</div>");
    		return;
    	}
    	
    	try {
			Object temp = ClassUtil.newInstance(Class.forName(authUserTemplate.getProcessClass()));
			MarketcodeService marketcodeService = (MarketcodeService) temp;
			renderHtml(marketcodeService.render(marketcodeCodeactive));
		} catch (Exception e) {
			renderHtml("<div class=\"alert alert-danger\">系统500错误</div>");
		}
    	
    }
    
    @Before(POST.class)
    @EmptyValidate({ 
    	@Form(name = "codeactiveId", message = "激活信息不存在"),
    })	
    public void savejifen(Long id, Long codeactiveId, Integer jifenType, Integer jifenNum, Integer maxJifenNum, Integer minJifenNum){
    	
    	if(jifenType == null) {
    		renderFail("请选择积分类型");
    		return;
    	}
    	
    	if(jifenType == 2){
    		//随机
    		if(maxJifenNum == null || minJifenNum == null || maxJifenNum <= minJifenNum){
    			renderFail("随机积分，请设置最大值跟最小值，并且，最小值不能大于最大值");
    			return;
    		}
    	}else {
    		//固定
    		if(jifenNum == null){
    			renderFail("请填写积分");
    			return;
    		}
    	}
    	
    	MarketcodeJifen marketcodeJifen = marketcodeJifenService.findById(id);
    	if(marketcodeJifen == null){
    		marketcodeJifen = new MarketcodeJifen();
    		marketcodeJifen.setCodeactiveId(codeactiveId);
    		marketcodeJifen.setCreated(new Date());
    		marketcodeJifen.setActive(true);
    	}
    	
    	marketcodeJifen.setJifenType(jifenType).setJifenNum(jifenNum).setMaxJifenNum(maxJifenNum).setMinJifenNum(minJifenNum).setUpdated(new Date());
    	
    	try {
    		marketcodeJifenService.saveOrUpdate(marketcodeJifen);
    		renderSuccess();
		} catch (Exception e) {
			renderFail(e.getMessage());
		}
    	
    }

	@Before(POST.class)
    public void del(Long id) {
        renderSuccess();
    }
	
}