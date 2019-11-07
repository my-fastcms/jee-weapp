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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.dbumama.market.model.PlatImages;
import com.dbumama.market.model.PlatUser;
import com.dbumama.market.model.SellerUser;
import com.dbumama.market.service.api.PlatImagesService;
import com.dbumama.market.web.core.ueditor.define.AppInfo;
import com.dbumama.market.web.core.ueditor.define.BaseState;
import com.dbumama.market.web.core.ueditor.define.MultiState;
import com.dbumama.market.web.core.ueditor.define.State;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.Jboot;
import io.jboot.db.model.Columns;

/**
 * @author wangjun
 * 2017年7月10日
 */
public abstract class AbstractPlatFileManager implements UeditorFileManager {
	
	private PlatImagesService userImagesService = Jboot.service(PlatImagesService.class);
	
	@Override
	public State list(Map<String, Object> conf, int start) {
//		final String rootPath = (String) conf.get("imgPrefix");
//		String dirPath = rootPath + (String) conf.get("dir");
//		List<String> allowFiles = getAllowFiles(conf.get("allowFiles"));
		int count = (Integer) conf.get("count");
		
		PlatUser user = (PlatUser) getSubject().getPrincipal();
		if(user == null){
			return new BaseState(false, AppInfo.NOT_EXIST);			
		}
		
		int pageNo = start/ count + 1;
		Page<PlatImages> pages = userImagesService.paginateByColumns(pageNo, count, Columns.create("plat_user_id", user.getId()));
				
		State state = null;
		if (start < 0 || start > pages.getTotalRow()) {
			state = new MultiState(true);
		} else {
			state = getState(pages.getList());
		}

		state.putInfo("start", start);
		state.putInfo("total", pages.getTotalRow());
		
		return state;
	}

	@Override
	public State saveFile(byte[] data, String rootPath, String savePath) {
		return null;
	}

	@Override
	public State saveFile(InputStream is, String rootPath, String savePath, String fileName, long maxSize,Integer fileType) {
		return null;
	}

	@Override
	public State saveFile(InputStream is, String rootPath, String savePath) {
		return null;
	}
	
	private State getState(List<PlatImages> userImages) {
		MultiState state = new MultiState(true);
		BaseState fileState = null;

		for (PlatImages image : userImages) {
			fileState = new BaseState(true);
			fileState.putInfo("url", image.getImgPath());
			state.addState(fileState);
		}

		return state;
	}

	protected List<String> getAllowFiles(Object fileExt) {
		List<String> list = new ArrayList<String>();
		if (fileExt == null) {
			return list;
		}

		String[] exts = (String[]) fileExt;

		for (int i = 0, len = exts.length; i < len; i++) {
			String ext = exts[i];
			list.add(ext.replace(".", ""));
		}
		return list;
	}
	
	protected Subject getSubject() {
		return SecurityUtils.getSubject();
	}
	
	protected SellerUser getSellerUser(){
		return (SellerUser) getSubject().getPrincipal();
	}
	
	protected String getFileName(String savePath) {
		return FilenameUtils.getBaseName(savePath);
	}
}

