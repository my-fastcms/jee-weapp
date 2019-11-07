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
package com.dbumama.market.web.core.route;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;

import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2017年7月10日
 */
public class PlatFrontRoutes extends AutoBindRoutes {

	private List<Class<Controller>> controllerClassList = new ArrayList<Class<Controller>>(); 
	
	public PlatFrontRoutes(List<Class<Controller>> controllerClassList){
		this.controllerClassList = controllerClassList;
	}
	
	@Override
	public void config() {
		super.config();
		
		for (Class<Controller> clazz : controllerClassList) {
            RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
            if (mapping == null) continue;

            String value = AnnotationUtil.get(mapping.value());
            if (value == null) continue;

            String viewPath = AnnotationUtil.get(mapping.viewPath());

            if (StrUtil.isNotBlank(viewPath)) {
                add(value, clazz, viewPath);
            } else {
                add(value, clazz);
            }
        }
	}
	
}
