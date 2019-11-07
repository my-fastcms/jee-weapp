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
package com.dbumama.market.web.core.ueditor.manager;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectResult;
import com.dbumama.market.model.SellerImages;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.SellerImagesService;
import com.dbumama.market.service.config.AliyunOssConfig;
import com.dbumama.market.web.core.ueditor.define.AppInfo;
import com.dbumama.market.web.core.ueditor.define.BaseState;
import com.dbumama.market.web.core.ueditor.define.State;
import io.jboot.Jboot;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author wangjun
 * 2019年4月15日
 */
public class AliyunossFileManager extends AbstractFileManager{

	private SellerImagesService sellerImagesService = Jboot.service(SellerImagesService.class);
	
	private AliyunOssConfig aliyunOssConfig = Jboot.config(AliyunOssConfig.class);
	
	final String endpoint = aliyunOssConfig.getAliyunOssEndpoint();
    final String accessId = aliyunOssConfig.getAliyunOssAk();
    final String accessKey = aliyunOssConfig.getAliyunOssSk();
    final String bucketName = aliyunOssConfig.getAliyunOssBucket();
    
    OSSClient ossClient;
	
	public AliyunossFileManager() {
	}

	@Override
	public State saveFile(byte[] data, String rootPath, String savePath) {
		ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(accessId, accessKey), null);
		
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		
		try {
			ossClient.putObject(bucketName, savePath, new ByteArrayInputStream(data));
		} catch (Exception e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}finally {
			ossClient.shutdown();
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}
	
	@Override
	public State saveFile(InputStream is, String rootPath, String savePath, String fileName, long maxSize,Integer fileType) {
		
		ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(accessId, accessKey), null);
		
		SellerUser user = (SellerUser) getSubject().getPrincipal();
		if(user == null){
			return new BaseState(false, AppInfo.NOT_EXIST);			
		}
		
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			IOUtils.copy(is, output);
			data = output.toByteArray();
			if (data.length > maxSize) {
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			PutObjectResult response = ossClient.putObject(bucketName, savePath, new ByteArrayInputStream(data));
			
			if(response != null){
				SellerImages sellerImages = new SellerImages();
				sellerImages.setSellerId(user.getId());
				sellerImages.setFileType(fileType);
				sellerImages.setTitle(fileName);
				sellerImages.setImgPath(savePath);
				sellerImages.setActive(1);
				
				sellerImages.setCreated(new Date());
				sellerImages.setUpdated(new Date());
				sellerImagesService.save(sellerImages);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(is);
			ossClient.shutdown();
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}

	@Override
	public State saveFile(InputStream is, String rootPath, String savePath) {
		ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(accessId, accessKey), null);
		
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			IOUtils.copy(is, output);
			data = output.toByteArray();
			ossClient.putObject(bucketName, savePath, is);
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(is);
			ossClient.shutdown();
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}
	
}
