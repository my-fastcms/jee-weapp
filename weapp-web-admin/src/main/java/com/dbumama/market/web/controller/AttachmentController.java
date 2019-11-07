package com.dbumama.market.web.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dbumama.market.model.ImageGroup;
import com.dbumama.market.model.SellerImages;
import com.dbumama.market.service.api.ImageGroupResultDto;
import com.dbumama.market.service.api.ImageGroupService;
import com.dbumama.market.service.api.SellerImagesService;
import com.dbumama.market.web.core.controller.BaseAdminController;
import com.dbumama.market.web.core.interceptor.CSRFInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value = "attachment", viewPath = "attachment")
public class AttachmentController extends BaseAdminController{
	
	@RPCInject
	private ImageGroupService imageGroupService;
	@RPCInject
	private SellerImagesService sellerImagesService;
	
	public void index(){
        setAttr("imageGroups", imageGroupService.getGroup(getSellerId()));
		render("/attachment/image_index.html");
	}
	
	public void vedio(){
		render("/attachment/vedio_index.html");
	}
	
	public void getImageGroupJson(){
        List<ImageGroupResultDto> resultDto=imageGroupService.getGroup(getSellerId());
        rendSuccessJson(resultDto); 
	}
	
	public void addGroup(){
		setAttr("imageGroup", imageGroupService.findById(getParaToLong("id")));
		render("/attachment/add_group.html");
	}
	
	public void editGroup(){
	    setAttr("imageGroups", imageGroupService.getGroup(getSellerId()));
	    setAttr("groupId", getParaToLong("groupId"));
	    setAttr("imageId", getPara("imageId"));
		render("/attachment/edit_group.html");
	}
	
	public void saveEditGroup(){
		final String image=getPara("imageId");
		final Long groupId=getParaToLong("groupId");
		try {
			if(!StrKit.isBlank(image)&&groupId!=null){
				String imageIds[]=(image.substring(0,image.length()-1)).split(",");
				for (int i = 0; i < imageIds.length; i++) {
					SellerImages sellerImages = sellerImagesService.findById(Long.valueOf(imageIds[i]));
					sellerImages.setImgGroupId(groupId);
					sellerImagesService.update(sellerImages);	
				}
			}
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "groupName", message = "请输入分组名称")
	})
	public void save(final Long id, final String groupName){
		ImageGroup imageGroup = imageGroupService.findById(id);
		
		if(imageGroup == null){
			imageGroup = new ImageGroup();
			imageGroup.setSellerId(getSellerId());
		}
		
		imageGroup.setGroupName(groupName);
		
		try {
			imageGroupService.saveOrUpdate(imageGroup);
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
		
	}
	
	@Clear(CSRFInterceptor.class)
	public void getImageList(){
		rendSuccessJson(sellerImagesService.paginate(getSellerId(), getParaToLong("id"), getPageNo(), getPageSize()));
	}
	
	
	@Before(POST.class)
	public void del(){
		SellerImages sellerImages=sellerImagesService.findById(getParaToLong("ids"));
		try {
			if(sellerImages != null){
				sellerImages.setActive(0);
				sellerImagesService.update(sellerImages);
			}	
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	/** 批量删除图片*/
	@Before(POST.class)
	public void delBatch(String ids){
		JSONArray parseArray = JSON.parseArray(ids);
		try{
		for (int i = 0; i < parseArray.size(); i++) {
			Long id = parseArray.getLong(i);
			SellerImages sellerImages=sellerImagesService.findById(id);
			if(sellerImages != null){
				sellerImages.setActive(0);
				sellerImagesService.update(sellerImages);
			}
		}
		rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	

}
