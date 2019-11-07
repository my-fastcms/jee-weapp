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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.dbumama.market.model.PlatImages;
import com.dbumama.market.model.PlatUser;
import com.dbumama.market.service.api.PlatImagesService;
import com.dbumama.market.web.core.ueditor.define.AppInfo;
import com.dbumama.market.web.core.ueditor.define.BaseState;
import com.dbumama.market.web.core.ueditor.define.State;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import io.jboot.Jboot;

/**
 * @author wangjun
 * 2017年7月10日
 */
public class QiniuPlatFileManager extends AbstractPlatFileManager{
	private final Auth auth;
	private final String bucket;
	private UploadManager uploadManager;
	private PlatImagesService platImagesService = Jboot.service(PlatImagesService.class);
	
	public QiniuPlatFileManager(String ak, String sk, String bucket) {
		auth = Auth.create(ak, sk);
		this.bucket = bucket;
		uploadManager = new UploadManager(new Configuration());
	}
	
	private String getUpToken(){
		return auth.uploadToken(bucket);
	}

	@Override
	public State saveFile(byte[] data, String rootPath, String savePath) {
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		
		try {
			uploadManager.put(data, savePath, getUpToken());
		} catch (QiniuException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}
	
	@Override
	public State saveFile(InputStream is, String rootPath, String savePath, String fileName, long maxSize,Integer fileType) {
		PlatUser user = (PlatUser) getSubject().getPrincipal();
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
			Response response = uploadManager.put(data, savePath, getUpToken());
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			PlatImages platImages = new PlatImages();
			platImages.setPlatUserId(user.getId());
			platImages.setFileType(fileType);
			platImages.setTitle(fileName);
			platImages.setImgPath(putRet.key);
			platImages.setActive(1);
			
			platImages.setCreated(new Date());
			platImages.setUpdated(new Date());
			platImagesService.save(platImages);
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(is);
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}

	@Override
	public State saveFile(InputStream is, String rootPath, String savePath) {
		if (savePath.startsWith("/")) {
			savePath = savePath.substring(1);
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			IOUtils.copy(is, output);
			data = output.toByteArray();
			uploadManager.put(data, savePath, getUpToken());
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(is);
		}
		State state = new BaseState(true);
		state.putInfo("size", data.length);
		state.putInfo("title", getFileName(savePath));
		return state;
	}

}

