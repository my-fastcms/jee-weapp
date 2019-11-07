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

import com.dbumama.market.model.Cashback;
import com.dbumama.market.model.CashbackProduct;
import com.dbumama.market.service.api.*;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping(value = "cashback")
@RequiresPermissions(value="/cashback")
public class CashbackController extends BaseAppAdminController {

    @RPCInject
    private CashbackService cashbackService;
    @RPCInject
    private ProductService productService;
    public void index(){
        render("/cashback/cashback_index.html");
    }

    public void set(){
        if(getParaToLong("pid") != null){
            setAttr("cashback", cashbackService.findById(getParaToLong("pid")));
            List<CashbackProduct> cashProducts = cashbackService.getCashbackProducts(getParaToLong("pid"));
            setAttr("cashProducts", cashProducts);
        }
        render("/cashback/cashback_set.html");
    }

    public void listProducts(){
        ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
        try {
            Page<ProductResultDto> pages = cashbackService.getProducts4CashbackPage(productParamDto);
            rendSuccessJson(pages);
        } catch (ProductException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void listSelectProduct(){
        String productIds=getPara("productIds");
        try {
            List<ProductResultDto> prouctDto=productService.getProducts(productIds);
            rendSuccessJson(prouctDto);
        } catch (ProductException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void list(){
        CashbackParamDto promotionParam = new CashbackParamDto(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("status"), getParaToInt("active"));
        try{
            Page<CashbackResultDto> pages=cashbackService.list(promotionParam);
            rendSuccessJson(pages);
        } catch (UmpException e) {
            rendFailedJson(e.getMessage());
        }
    }

    @Before(POST.class)
    @EmptyValidate({
            @Form(name = "name", message = "请输入返现活动名称"),
            @Form(name = "start_time", message = "请输入开始时间"),
            @Form(name = "end_time", message = "请输入结束时间"),
            @Form(name = "cashback_limit", message = "返现限制不能为空"),
            @Form(name = "cashback_start", message = "返现区间不能为空"),
            @Form(name = "cashback_method", message = "请选择返现方式")
    })
    public void save(Long id, String name, Integer cashback_method, Integer cashback_limit, BigDecimal cashback_start, BigDecimal cashback_end, String start_time, String end_time){
        try {
            if(cashback_method == 0){
                if(cashback_end == null){
                    rendFailedJson("返现区间不能为空");
                    return;
                }
            }

            final String productIds = getPara("product_ids");
            Cashback cashback= cashbackService.findById(id);
            if(cashback == null){
                cashback = new Cashback();
                cashback.setAppId(getAuthUserId()).setActive(true);
            }

            cashback.setName(name).setStartTime(DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.parse(start_time))
                    .setEndTime(DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.parse(end_time))
                    .setCashbackMethod(cashback_method).setCashbackStart(cashback_start)
                    .setCashbackEnd(cashback_end).setCashbackLimit(cashback_limit);

            cashbackService.save(cashback, productIds, getAuthUserId());
            rendSuccessJson();
        } catch (Exception e) {
            rendFailedJson(e.getMessage());
        }

    }

    public void del(){
        String ids = getPara("ids");
        for(String id : ids.split("-")){
            Cashback cashBack = cashbackService.findById(Long.valueOf(id));
            cashBack.setActive(false);
            cashbackService.update(cashBack);
        }
        rendSuccessJson("操作成功！");
    }

}