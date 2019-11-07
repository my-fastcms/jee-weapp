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

import com.dbumama.market.model.MultiGroup;
import com.dbumama.market.service.api.*;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

@RequestMapping(value = "groupon", viewPath = "pintuan")
@RequiresPermissions(value="/groupon")
public class MultiGroupController extends BaseAppAdminController {

    @RPCInject
    private MultiGroupService multiGroupService;

    public void index(){
        render("groupon_index.html");
    }

    public void set(){
        setAttr("groupResult", multiGroupService.getGroupInfo(getParaToLong("id")));
        render("groupon_set.html");
    }

    public void list(){
        GrouponParamDto grouponParamDto = new GrouponParamDto(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("status"), getParaToInt("active"));
        try{
            Page<MultiGroup> pages=multiGroupService.list(grouponParamDto);
            rendSuccessJson(pages);
        } catch (UmpException e) {
            rendFailedJson(e.getMessage());
        }
    }

    @Before(POST.class)
    @EmptyValidate({
            @Form(name = "name", message = "请输入活动名称"),
            @Form(name = "start_date", message = "请输入活动开始时间"),
            @Form(name = "end_date", message = "请输入活动结束时间"),
            @Form(name = "offer_num", message = "请输入参团人数"),
            @Form(name = "valid_time", message = "请输入成团有限时间"),
    })
    public void save(Long id, String name, String start_date, String end_date, Integer offer_num, Integer valid_time, Boolean enable_moni_suc, Integer quota,
                     Integer	multi_type, Integer group_condition, Boolean need_follows, String follows_title, String follows_image, String follows_intro,
                     String activity_image, String activity_explain,  String share_image, String share_title, String share_intro,
                     String detail_title, String detail_intro){
        MultiGroup multiGroup = new MultiGroup();

        try {
            multiGroup.setAppId(getAuthUser().getId()).setActive(true).setUpdated(new Date()).setId(id)
                    .setName(name).setStartDate(DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.parse(start_date))
                    .setEndDate(DateTimeUtil.FORMAT_YYYY_MM_DDHHMMSS.parse(end_date)).setOfferNum(offer_num)
                    .setValidTime(valid_time).setEnableMoniSuc(enable_moni_suc).setQuota(quota).setMultiType(multi_type)
                    .setGroupCondition(group_condition).setNeedFollows(need_follows).setFollowsTitle(follows_title).setFollowsImage(follows_image)
                    .setFollowsIntro(follows_intro).setActivityImage(activity_image).setActivityExplain(activity_explain).setShareImage(share_image)
                    .setShareTitle(share_title).setShareIntro(share_intro).setDetailTitle(detail_title).setDetailIntro(detail_intro);
        } catch (ParseException e) {
            e.printStackTrace();
            rendFailedJson(e.getMessage());
            return;
        }

        try {
            multiGroupService.save(multiGroup, getPara("setItems"));
            rendSuccessJson();
        } catch (Exception e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void del(){
        String ids = getPara("ids");
        for(String id : ids.split("-")){
            MultiGroup multiGroup = multiGroupService.findById(Long.valueOf(id));
            if(multiGroup !=null){
                multiGroup.setActive(false);
                multiGroupService.update(multiGroup);
            }
        }
        rendSuccessJson("操作成功！");
    }

    public void listProducts(){
        ProductParamDto productParamDto = new ProductParamDto(getAuthUserId(), getPageNo());
        try {
            Page<ProductResultDto> pages = multiGroupService.getProducts4GrouponPage(productParamDto);
            rendSuccessJson(pages);
        } catch (ProductException e) {
            rendFailedJson(e.getMessage());
        }
    }

    public void put(){
        Long id = getParaToLong("id");
        HashMap<String, String> put = multiGroupService.getPutIn(id,getAuthUser());
        setAttr("map", put);
        render("gourpon_put.html");
    }

    public void download(){
        String url = getPara("url");
        String path = JFinal.me().getServletContext().getRealPath(url).replace("\\", "/");
        try {
            File file = new File(path);
            renderFile(file);
        } catch (Exception e) {
            renderFail(e.getMessage());
        }
    }
}