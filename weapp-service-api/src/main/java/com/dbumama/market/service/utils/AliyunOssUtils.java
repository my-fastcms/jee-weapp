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
package com.dbumama.market.service.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GetObjectRequest;
import com.dbumama.market.service.config.AliyunOssConfig;
import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AliyunOssUtils {

    static Log log = Log.getLog(AliyunOssUtils.class);

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    static AliyunOssConfig aliyunOssConfig = Jboot.config(AliyunOssConfig.class);
    
    /**
     * 同步本地文件到阿里云OSS
     *
     * @param path
     * @param file
     * @return
     */
    public static void upload(String path, File file) {
        fixedThreadPool.execute(() -> {
            uploadsync(path, file);
        });
    }

    /**
     * 同步本地文件到阿里云OSS
     *
     * @param path
     * @param file
     * @return
     */
    public static boolean uploadsync(String path, File file) {

    	if (StrUtil.isBlank(path)) {
            return false;
        }

        path = removeFileSeparator(path);
        path = path.replace('\\', '/');

        String ossBucketName = aliyunOssConfig.getAliyunOssBucket();
        OSSClient ossClient = newOSSClient();

        try {
            ossClient.putObject(ossBucketName, path, file);
            boolean success = ossClient.doesObjectExist(ossBucketName, path);
            if (!success) {
                LogKit.error("aliyun oss upload error! path:" + path + "\nfile:" + file);
            }
            return success;

        } catch (Throwable e) {
            log.error("aliyun oss upload error!!!", e);
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 如果文件以 / 或者 \ 开头，去除 / 或 \ 符号
     */
    private static String removeFileSeparator(String path) {
        while (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1, path.length());
        }
        return path;
    }

    /**
     * 同步 阿里云OSS 到本地
     *
     * @param path
     * @param toFile
     * @return
     */
    public static boolean download(String path, File toFile) {

        if (StrUtil.isBlank(path)) {
            return false;
        }

        path = removeFileSeparator(path);
        String ossBucketName = aliyunOssConfig.getAliyunOssBucket();
        OSSClient ossClient = newOSSClient();
        try {
            ossClient.getObject(new GetObjectRequest(ossBucketName, path), toFile);
            return true;
        } catch (Throwable e) {
            log.error("aliyun oss download error!!!  path:" + path + "   toFile:" + toFile, e);
            if (toFile.exists()) {
                toFile.delete();
            }
            return false;
        } finally {
            ossClient.shutdown();
        }
    }
    
    public static String upload(File toFile, String path){
    	if (StrUtil.isBlank(path)) {
            return "";
        }

        path = removeFileSeparator(path);
        String ossBucketName = aliyunOssConfig.getAliyunOssBucket();
        OSSClient ossClient = newOSSClient();
        try {
            ossClient.putObject(ossBucketName, path, toFile);
            return path;
        } catch (Throwable e) {
            log.error("aliyun oss download error!!!  path:" + path + "   toFile:" + toFile, e);
            if (toFile.exists()) {
                toFile.delete();
            }
            return "";
        } finally {
            ossClient.shutdown();
        }
    }

    public static OSSClient newOSSClient() {
        String endpoint = aliyunOssConfig.getAliyunOssEndpoint();
        String accessId = aliyunOssConfig.getAliyunOssAk();
        String accessKey = aliyunOssConfig.getAliyunOssSk();
        return new OSSClient(endpoint, new DefaultCredentialProvider(accessId, accessKey), null);
    }


}