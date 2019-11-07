package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.ImageGroup;
import com.dbumama.market.model.SellerImages;
import com.dbumama.market.service.api.ImageGroupResultDto;
import com.dbumama.market.service.api.ImageGroupService;
import com.dbumama.market.service.api.SellerImagesService;
import com.dbumama.market.web.core.controller.BasePlatController;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "attachment", viewPath = "attachment")
public class AttachmentController extends BasePlatController {
	
	@RPCInject
	private ImageGroupService imageGroupService;
	@RPCInject
	private SellerImagesService sellerImagesService;
	
	public void index(){
        List<ImageGroupResultDto> resultDto=imageGroupService.getGroup(null);
        setAttr("imageGroups", resultDto);
		render("/attachment/image_group_index.html");
	}
	
	public void vedio(){
		render("/attachment/vedio_index.html");
	}
	
	public void getImageGroupJson(){
        List<ImageGroupResultDto> resultDto=imageGroupService.getGroup(null);
        rendSuccessJson(resultDto); 
	}
	
	public void addGroup(){
		ImageGroup imageGroup=imageGroupService.findById(getParaToLong("id"));	
		setAttr("imageGroup", imageGroup);
		render("/attachment/add_group.html");
	}
	
	public void editGroup(){
		Long groupId=getParaToLong("groupId");
		List<ImageGroupResultDto> resultDto=imageGroupService.getGroup(null);
	    setAttr("imageGroups", resultDto);
	    setAttr("groupId", groupId);
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
					sellerImages.update();	
				}
			}
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void getImageList(){
		rendSuccessJson(sellerImagesService.paginate(null, getParaToLong("id"), getPageNo(), 14));
	}
	
	public void del(){
		SellerImages sellerImages=sellerImagesService.findById(getParaToLong("ids"));
		try {
			if(sellerImages != null){
				sellerImages.setActive(0);
				sellerImages.update();	
			}
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	
}
