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

import java.io.File;

import com.dbumama.market.service.api.WxmallBaseException;

/**
 * @author wangjun
 * 2017年7月12日
 */
public interface FileService {

	public String upload(File file, Long sellerId, Long appId, Long groupId) throws WxmallBaseException;
	public String upload(File file, Long sellerId) throws WxmallBaseException;
	public String upload(File file) throws WxmallBaseException;
	public String upload(Byte [] file, Long sellerId, Long groupId) throws WxmallBaseException;
	
	/**
	 * 运维管理员上传图片
	 * @param file
	 * @param platUserId
	 * @return
	 * @throws WxmallBaseException
	 */
	public String upload(Long platUserId, File file) throws WxmallBaseException;
	
}

