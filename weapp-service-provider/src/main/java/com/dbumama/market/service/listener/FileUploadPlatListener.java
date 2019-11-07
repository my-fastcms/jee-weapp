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
package com.dbumama.market.service.listener;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.PlatImages;
import com.dbumama.market.service.api.PlatImagesService;

import io.jboot.Jboot;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.event.annotation.EventConfig;

/**
 * @author wangjun
 *
 */
@EventConfig(action = {"file_upload_plat"})
public class FileUploadPlatListener implements JbootEventListener{

	private PlatImagesService platImagesService = Jboot.service(PlatImagesService.class);
	
	/* (non-Javadoc)
	 * @see io.jboot.event.JbootEventListener#onEvent(io.jboot.event.JbootEvent)
	 */
	@Override
	public void onEvent(JbootEvent event) {
		JSONObject jsonObj = event.getData();
		PlatImages platImages = new PlatImages();
		platImages.setPlatUserId(jsonObj.getLong("plat_user_id"));
		platImages.setImgPath(jsonObj.getString("img_url"));
		platImages.setFileType(jsonObj.getInteger("file_type"));
		platImages.setTitle(jsonObj.getString("title"));
		platImages.setActive(1);
		platImages.setCreated(new Date());
		platImages.setUpdated(new Date());
		platImagesService.save(platImages);		
	}

}
