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
package com.dbumama.market.service.api;

import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

import com.dbumama.weixin.api.CompMediaApi;
import com.dbumama.weixin.utils.HttpUtils;

/**
 * 其他类型（图片、语音、视频）的返回如下
 * @author wangjun
 * @date 2018年8月18日
 */
@SuppressWarnings("serial")
public class MediaItemResDto implements Serializable{

	private String mediaId;
	private String name;
	private Integer updateTime;
	private String url;
	private String type;
	private String displayUrl;//解决微信防盗链的问题
	
	public MediaItemResDto(){}
	
	public MediaItemResDto(String mediaId, String name, Integer updateTime, String url, String type){
		this.mediaId = mediaId;
		this.name = name;
		this.updateTime = updateTime;
		this.url = url;
		this.type = type;
		
		if(CompMediaApi.MediaType.IMAGE.get().equals(this.type)){
			try {
				Base64 base64 = new Base64();
	            final String result = "data:image/jpg;base64," + base64.encodeAsString(HttpUtils.getBytes(url));
	            setDisplayUrl(result);
			} catch (Exception e) {
				e.printStackTrace();
				setDisplayUrl("");
			}
		}
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the displayUrl
	 */
	public String getDisplayUrl() {
		return displayUrl;
	}
	/**
	 * @param displayUrl the displayUrl to set
	 */
	public void setDisplayUrl(String displayUrl) {
		this.displayUrl = displayUrl;
	}
	/**
	 * @return the mediaId
	 */
	public String getMediaId() {
		return mediaId;
	}
	/**
	 * @param mediaId the mediaId to set
	 */
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the updateTime
	 */
	public Integer getUpdateTime() {
		return updateTime;
	}
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Integer updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
