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
package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.WeappConstants;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.FileService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.config.QiniuConfig;
import com.dbumama.market.service.utils.AliyunOssUtils;
import com.dbumama.market.utils.AttachmentUtils;
import com.dbumama.market.utils.FileUtils;
import com.dbumama.weixin.api.CompMediaApi;
import com.dbumama.weixin.api.CompMediaApi.MediaType;
import com.google.gson.Gson;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.rpc.annotation.RPCBean;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangjun
 * 2017年7月12日
 */
@Bean
@RPCBean
public class FileServiceImpl implements FileService{

	QiniuConfig qiniuConfig = Jboot.config(QiniuConfig.class);
	Auth auth = Auth.create(qiniuConfig.getQiniuAk(), qiniuConfig.getQiniuSk());
	
	@Inject
	private AuthUserService authUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.FileService#upload(java.io.File)
	 */
	@Override
	public String upload(File file, Long sellerId, Long appId, Long groupId) throws WxmallBaseException{
		
		if(!_isGetThumnail(file.getName())) {
			file.delete();
			throw new WxmallBaseException("只能上传图片文件");	
		}
		
		//文件处理器
		final String fileHandleType = WeappConstants.FILE_HANDLE_TYPE;
		
		String title = file.getName();
		String imgUrl = "";
		String mediaId = "";

		try {
			switch (fileHandleType) {
			case "qiniu":
				imgUrl = _uploadBytes4Qiniu(FileUtils.toByteArray(file), _getFileKey(title));
				break;
			case "aliyunoss":
				imgUrl = AliyunOssUtils.upload(file,  _getFileKey(title));
				break;
			default:
				imgUrl = AttachmentUtils.moveFile(file);
				break;
			}
			
			//同时上传到微信
			if(appId != null){
				AuthUser authUser = authUserService.findById(appId);
				if(authUser !=null){
					ApiResult resp = CompMediaApi.addMaterial(authUserService.getAccessToken(authUser), MediaType.IMAGE, file);
					if(resp.isSucceed()){
						mediaId = resp.getStr("media_id");
					}else{
						//上传临时素材
						resp = CompMediaApi.uploadMedia(authUserService.getAccessToken(authUser), MediaType.IMAGE, file);
						if(resp.isSucceed()){
							mediaId = resp.getStr("media_id");
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw new WxmallBaseException(e.getMessage());
		} finally {
			file.delete();
		}
		
		if(sellerId !=null && StrKit.notBlank(imgUrl)){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("seller_id", sellerId);
			jsonObj.put("group_id", groupId);
			jsonObj.put("img_url", _processPath(imgUrl));
			jsonObj.put("file_type", 1);
			jsonObj.put("media_id", mediaId);
			jsonObj.put("title", title);
			Jboot.sendEvent(new JbootEvent("file_upload", jsonObj));			
		}
		return imgUrl;
	}
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.FileService#upload(java.io.File, java.lang.Long)
	 */
	@Override
	public String upload(File file, Long sellerId) throws WxmallBaseException {
		return upload(file, sellerId, null, null);
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private String _processFile(File file) throws WxmallBaseException{
		File compressFile = null;
        if(file.length() > 500 * 1024){ //超过200Kb的图片进行压缩
        	compressFile = new File(file.getPath());
            //对文件进行压缩
            try {
				Thumbnails.of(file)
				.scale(0.5f)
				.outputQuality(1f)
				.toFile(compressFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new WxmallBaseException(e.getMessage());
			}
        }
        return compressFile == null ? AttachmentUtils.moveFile(file) : AttachmentUtils.moveFile(compressFile);  
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private String _uploadCompressFile4Qiniu(File file, String filkey) throws WxmallBaseException {
		
        long fileSize = file.length();
        
        File compressFile = null;
        if(fileSize > 500 * 1024){ //超过200Kb的图片进行压缩
        	compressFile = new File(file.getPath());
            //对文件进行压缩
            try {
				Thumbnails.of(file)
				.scale(0.5f)
				//.size(160, 160)
				//.rotate(90)
				//.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("watermark.png")), 0.5f)
				.outputQuality(1f)
				.toFile(compressFile);
			} catch (IOException e) {
				throw new WxmallBaseException(e.getMessage());
			}        	
            fileSize = compressFile.length();
        }
        
        UploadManager uploadManager = new UploadManager(new Configuration());
        try {
			Response response = uploadManager.put(compressFile != null ? compressFile : file, filkey, _getUpToken());
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
	        return putRet.key;
		} catch (QiniuException e) {
			throw new WxmallBaseException(e.getMessage());
		} finally {
			if(compressFile != null) 
				compressFile.delete();
		}
	}
	
	private String _uploadBytes4Qiniu(byte [] file, String filkey) throws WxmallBaseException {
        UploadManager uploadManager = new UploadManager(new Configuration());
        try {
			Response response = uploadManager.put(file, filkey, _getUpToken());
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
	        return putRet.key;
		} catch (QiniuException e) {
			throw new WxmallBaseException(e.getMessage());
		} finally {
		}
	}
	
	private String _getUpToken(){
		return auth.uploadToken(qiniuConfig.getQiniuBucket());
	}
	
	private static boolean _isGetThumnail(String fileName){
        return getListFileType().contains(_getExtName(fileName).toLowerCase());
    }

    private static String _getExtName(String fileName){
        return fileName.substring(fileName.lastIndexOf('.')+1);
    }

    public static List<String> getListFileType(){
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add("jpg");
        fileTypes.add("jpeg");
        fileTypes.add("bmp");
        fileTypes.add("gif");
        fileTypes.add("png");
        return fileTypes;
    }
	
	/**
	 * windows下面图片路径会为反斜杠，此方法替换反斜杠为斜杆，linux下不存在此问题
	 * @param imgUrl
	 * @return
	 */
	private String _processPath(String imgUrl){
		return imgUrl.replaceAll("\\\\", "/");
	}
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	static Object lockObj = new Object();
	
	private String _getFileKey(String filename){
		
		String uuid= "";
		synchronized (lockObj) {
			uuid = String.valueOf(new Date().getTime());
		}
		
		return _processPath(new StringBuilder().append("upload/image")
				.append(File.separator).append(dateFormat.format(new Date())).append(File.separator).append(uuid)
				.append(FileUtils.getSuffix(filename)).toString());
	
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.FileService#upload(java.io.File)
	 */
	@Override
	public String upload(File file) throws WxmallBaseException {
		return upload(file, null, null, null);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.FileService#upload(java.lang.Byte[], java.lang.Long, java.lang.Long)
	 */
	@Override
	public String upload(Byte[] file, Long sellerId, Long groupId) throws WxmallBaseException {
		return _uploadBytes4Qiniu(toPrimitives(file), "");
	}

	byte[] toPrimitives(Byte[] oBytes) {
	    byte[] bytes = new byte[oBytes.length];

	    for(int i = 0; i < oBytes.length; i++) {
	        bytes[i] = oBytes[i];
	    }

	    return bytes;
	}

	@Override
	public String upload(Long platUserId, File file) throws WxmallBaseException {
		if(!_isGetThumnail(file.getName())) {
			file.delete();
			throw new WxmallBaseException("只能上传图片文件");	
		}
		
		//文件处理器
		final String fileHandleType = WeappConstants.IMAGE_DOMAIN;
		
		String title = file.getName();
		String imgUrl = "";
		
		try {
			switch (fileHandleType) {
			case "qiniu":
				imgUrl = _uploadBytes4Qiniu(FileUtils.toByteArray(file), _getFileKey(title));
				break;
			case "aliyunoss":
				imgUrl = AliyunOssUtils.upload(file,  _getFileKey(title));
				break;
			default:
				imgUrl = AttachmentUtils.moveFile(file);
				break;
			}
			
		} catch (Exception e) {
			throw new WxmallBaseException(e.getMessage());
		} finally {
			file.delete();
		}
		
		if(platUserId !=null && StrKit.notBlank(imgUrl)){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("plat_user_id", platUserId);
			jsonObj.put("img_url", _processPath(imgUrl));
			jsonObj.put("file_type", 1);
			jsonObj.put("title", title);
			Jboot.sendEvent(new JbootEvent("file_upload_plat", jsonObj));			
		}
		return imgUrl;
	}

}

