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

import com.dbumama.market.model.Promotion;
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

@RequestMapping(value = "promotion")
@RequiresPermissions(value="/promotion")
public class PromotionController extends BaseAppAdminController {

    @RPCInject
    private PromotionService promotionService;

    public void index(){
        render("/promotion/promotion_index.html");
    }

    public void set(){
        setAttr("promotion", promotionService.getPromotionInfo(getParaToLong("pid")));
        render("/promotion/promotion_set.html");
    }

    public void listProducts(){
        ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
        try {
            Page<ProductResultDto> pages = promotionService.getProductsNoPromotionPage(productParamDto);
            rendSuccessJson(pages);
        } catch (ProductException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void listDiscountProducts(){
        ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
        productParamDto.setPromotionId(getParaToLong("promotionId"));
        try {
            Page<ProductResultDto> pages = promotionService.getProductsPromotionPage(productParamDto);
            rendSuccessJson(pages);
        } catch (ProductException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void list(){
        PromotionParamDto promotionParam = new PromotionParamDto(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("status"), getParaToInt("active"));
        try {
            Page<PromotionResultDto> pages = promotionService.list(promotionParam);
            rendSuccessJson(pages);
        } catch (UmpException e) {
            rendFailedJson(e.getMessage());
        }
    }

    @Before(POST.class)
    @EmptyValidate({
            @Form(name = "promotion_name", message = "请输入折扣活动名称"),
            @Form(name = "start_date", message = "请输入开始时间"),
            @Form(name = "end_date", message = "请输入结束时间"),
            @Form(name = "promotion_tag", message = "请输入折扣活动标签")
    })
    public void save(Long id, String promotion_name, String start_date, String end_date, String promotion_tag){
        try {
            Promotion promotion = promotionService.findById(id);

            if(promotion == null){
                promotion = new Promotion();
                promotion.setAppId(getAuthUserId());
                promotion.setActive(true);
            }

            promotion.setStartDate(DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.parse(start_date)).setEndDate(DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.parse(end_date)).setPromotionName(promotion_name).setPromotionTag(promotion_tag);

            String promotionSetItems = getPara("zhekouItems");
            promotionService.save(promotion, getAuthUserId(), promotionSetItems);
            rendSuccessJson();
        } catch (Exception e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void del(){
        String ids = getPara("ids");
        for(String id : ids.split("-")){
            Promotion promotion = promotionService.findById(Long.valueOf(id));
            promotion.setActive(false);
            promotionService.update(promotion);
        }
        rendSuccessJson("操作成功！");
    }

}