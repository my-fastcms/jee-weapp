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

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.service.api.PlatImagesService;
import com.dbumama.market.service.api.FileService;
import com.dbumama.market.web.core.controller.BasePlatController;
import com.jfinal.upload.UploadFile;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2017年7月11日
 */
@RequestMapping(value="upload", viewPath = "attachment")
public class UploadController extends BasePlatController{

	@RPCInject
	private FileService fileService;
	@RPCInject
	private PlatImagesService platImagesService;
	
	/**
    * 选择图片界面
    */
    public void addImage(){
    	setAttr("model", getPara("model"));
    	render("list_image.html");
    }
    
    public void addUploadImage(){
    	setAttr("model", getPara("model"));
    	setAttr("groupId", getPara("groupId"));
    	render("upload_image.html");
    }
    
    public void addVedio(){
    	setAttr("model", getPara("model"));
    	render("list_vedio.html");
    }
    
    public void addUploadVedio(){
    	setAttr("model", getPara("model"));
    	setAttr("groupId", getPara("groupId"));
    	render("upload_vedio.html");
    }

	public void index() {
		List<UploadFile> uFile = getFiles();
		if (uFile == null) {
			rendFailedJson("没有图片");
			return;
		}
		
		List<String> errorFile = new ArrayList<String>();
		
		for (UploadFile uploadFile : uFile) {
			try {
				fileService.upload(getPlatUserId(), uploadFile.getFile());
			} catch (Exception e) {
				e.printStackTrace();
				errorFile.add(uploadFile.getFileName());
			}
		}
		
		if(errorFile.size()>0){
			rendFailedJson("有文件不是图片格式，不能上传");
		}else{
			rendSuccessJson();			
		}
	}
	
	public void vedio() {
		List<UploadFile> uFile = getFiles();
		if (uFile == null) {
			rendFailedJson("没有图片");
			return;
		}
		
		List<String> errorFile = new ArrayList<String>();
		
//		for (UploadFile uploadFile : uFile) {
//			try {
//				fileService.uploadVedio(uploadFile.getFile(), null);
//			} catch (Exception e) {
//				e.printStackTrace();
//				errorFile.add(uploadFile.getFileName());
//			}
//		}
		
		if(errorFile.size()>0){
			rendFailedJson("有文件不是图片格式，不能上传");
		}else{
			rendSuccessJson();			
		}
	}
	
	public void getImageList(){
		rendSuccessJson(platImagesService.paginate(getPlatUserId(), getPageNo(), 14));
	}

}

