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

import com.dbumama.market.model.FullCut;
import com.dbumama.market.service.api.*;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Date;

@RequestMapping(value = "fullcut")
@RequiresPermissions(value="/fullcut")
public class FullCutController extends BaseAppAdminController {

    @RPCInject
    private FullCutService fullCutService;

    public void index(){
        render("full_cut_index.html");
    }

    public void set(){
        setAttr("fullCutDto", fullCutService.getFullCutInfo(getParaToLong("pid")));
        render("full_cut_set.html");
    }

    public void list(){
        FullcutPageParamDto fullcutPageParamDto = new FullcutPageParamDto(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("status"), getParaToInt("active"));
        try{
            Page<FullCut> pages=fullCutService.list(fullcutPageParamDto);
            rendSuccessJson(pages);
        } catch (UmpException e) {
            rendFailedJson(e.getMessage());
        }
    }

    @Before(POST.class)
    public void save(Long id){
        final String fullCutName = getPara("name");
        final Date startDate = getParaToDate("start_date");
        final Date endDate = getParaToDate("end_date");
        final String setItem = getPara("setItem");
        final String productIds = getPara("product_ids");
        FullCutParamDto paramDto = new FullCutParamDto(id, getAuthUserId(), fullCutName, startDate, endDate, setItem, productIds);
        try {
            FullCut fullCut = fullCutService.save(paramDto);
            rendSuccessJson(fullCut);
        } catch (UmpException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void listProducts(){
        ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
        try {
            Page<ProductResultDto> pages = fullCutService.getProducts4FullCutPage(productParamDto);
            rendSuccessJson(pages);
        } catch (ProductException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void delSet(){
        final Long id=getParaToLong("ids");

        if(id == null){
            rendFailedJson("id is null");
            return;
        }
        try {
            FullCut set=fullCutService.findById(id);
            set.setActive(false);
            fullCutService.update(set);
            rendSuccessJson();
        } catch (Exception e) {
            rendFailedJson(e.getMessage());
        }

    }
}