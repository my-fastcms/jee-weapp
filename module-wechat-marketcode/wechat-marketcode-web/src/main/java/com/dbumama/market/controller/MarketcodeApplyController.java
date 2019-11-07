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

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.MarketcodeApply;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.MarketcodeApplyService;
import com.dbumama.market.service.api.MarketcodeCodeactiveService;
import com.dbumama.market.service.api.MarketcodeException;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.dbumama.market.web.core.render.TempFileRender;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@RequestMapping(value = "marketcode/marketcodeapply", viewPath="marketcode")
@RequiresPermissions(value={"/marketcode/marketcodeapply"}, logical = Logical.OR)
public class MarketcodeApplyController extends BaseAppAdminController {

    @RPCInject
    private MarketcodeApplyService marketcodeApplyService;
    @RPCInject
    private MarketcodeCodeactiveService marketcodeCodeactiveService;
    @RPCInject
    private AuthUserService authUserService;

    public void index() {
        render("marketcodeapply_index.html");
    }
    
    public void list(){
    	Columns columns = Columns.create();
    	columns.add(Column.create("app_id", getAuthUserId()));
    	renderSuccess(marketcodeApplyService.paginateByColumns(getPageNo(), getPageSize(), columns));
    }

    public void edit(Long id) {
        MarketcodeApply entry = marketcodeApplyService.findById(id);
        setAttr("marketcodeApply", entry);
        if(entry !=null){
        	ApiResult apiResult = marketcodeApplyService.applyStatus(entry.getAppId(), entry.getApplicationId(), entry.getIsvApplicationId());
        	if(apiResult.isSucceed() && StrKit.notBlank(apiResult.getStr("status"))){
        		setAttr("result", apiResult.getStr("status"));
        	}else{
        		setAttr("result", apiResult.getErrorMsg());
        	}
        }
        render("marketcodeapply_edit.html");
    }
   
    @Before(POST.class)
    @EmptyValidate({ 
    	@Form(name = "codeCount", message = "请输入申请码数量"),
	})
    public void save(Long id, Long codeCount) {
    	try {
    		marketcodeApplyService.apply(getAuthUserId(), codeCount);
    		renderSuccess();
		} catch (MarketcodeException e) {
			renderFail(e.getMessage());
		}
    }

    public void download(Long id){
    	try {
        	List<String> dataList = marketcodeApplyService.downloadCode(id);
        	
        	File file = new File(PathKit.getWebRootPath() +File.separator+ DateTimeUtil.getDateTime14String() + ".txt");
        	
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            for(int i=0;i<dataList.size();i++) {
                String line = dataList.get(i);
                bw.write(line);
                bw.newLine();
                bw.flush();
            }

            //释放资源
            bw.close();
        	
        	render(new TempFileRender(file));

//        	List<String> headers = new ArrayList<String>();
//    		headers.add("微信码");
//    		headers.add("原始码");
//    		headers.add("索引");
//    		headers.add("二维码");
//    		
//    		render(new CsvRender(headers, dataList).fileName(DateTimeUtil.getDateTime14String() + ".csv").encodeType("utf-8"));

    	} catch (MarketcodeException | IOException e) {
			setAttr("error", e.getMessage());
			renderError(403);
		} 
    }
    
    public void set(){
    	render("marketcode_set.html");
    }
    
    @Before(POST.class)
    @EmptyValidate({ 
    	@Form(name = "marketcodeKey", message = "请输入一物一码密钥"),
	})
    public void savekey(String marketcodeKey){
    	AuthUser authUser = authUserService.findById(getAuthUserId());
    	authUser.setMarketcodeKey(marketcodeKey);
    	authUserService.update(authUser);
    	SecurityUtils.getSubject().getSession().setAttribute(WeappConstants.WEB_WEAPP_IN_SESSION, authUser);
    	renderSuccess();
    }
    
	@Before(POST.class)
    public void del(Long id) {
		renderSuccess();
    }
}