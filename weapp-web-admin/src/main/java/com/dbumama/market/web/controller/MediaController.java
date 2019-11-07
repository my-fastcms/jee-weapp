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

import com.dbumama.market.model.Media;
import com.dbumama.market.service.api.MediaItemNewsResDto;
import com.dbumama.market.service.api.MediaItemResDto;
import com.dbumama.market.service.api.MediaService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

/**
 * @author wangjun
 * @date 2018年7月19日
 * 微信素材
 */
@RequestMapping(value="media")
public class MediaController extends BaseAppAdminController{

	@RPCInject
	private MediaService mediaService;
	
	public void index(){
		render("media_list.html");
	}

	public void list(){
		renderSuccess(mediaService.list(getAuthUserId(), getPageNo(), getPageSize()));
	}
	
	public void add(){
		Media media = mediaService.findById(getParaToLong("id"));
		setAttr("media", media);
		render("custom_media_add.html");
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "title", message = "请输入素材标题"),
        @Form(name = "picUrl", message = "请输入素材图片"),
        @Form(name = "viewUrl", message = "请输入素材链接地址"),
	})
	public void save(Long id, String title, String desc, String picUrl, String viewUrl){
		Media media = mediaService.findById(id);
		
		if(media == null){
			media = new Media();
			media.setAppId(getAuthUserId()).setActive(true);
		}
		
		media.setTitle(title).setDesc(desc).setPicUrl(picUrl).setViewUrl(viewUrl);
		
		mediaService.saveOrUpdate(media);
		
		renderSuccess();
	}
	
	public void wechat(){
		render("media_wechat_list.html");
	}
	public void wechatItemUrl(){
		render("media_wechat_list_itemurl.html");
	}
	
	public void list4wechat(){
		Page<MediaItemNewsResDto> pages = mediaService.listMediasNews(getAuthUserId(), getPageNo(), getPageSize());
		renderSuccess(pages);
	}
	
	public void listImage(){
		Page<MediaItemResDto> pages = mediaService.listMediasImage(getAuthUserId(), getPageNo(), getPageSize());
		renderSuccess(pages);
	}
	
	public void listVoice(){
		Page<MediaItemResDto> pages = mediaService.listMediasVoice(getAuthUserId(), getPageNo(), getPageSize());
		renderSuccess(pages);
	}
	
	public void listVideo(){
		Page<MediaItemResDto> pages = mediaService.listMediasVideo(getAuthUserId(), getPageNo(), getPageSize());
		renderSuccess(pages);
	}

}
