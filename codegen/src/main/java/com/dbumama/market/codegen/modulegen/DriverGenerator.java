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
package com.dbumama.market.codegen.modulegen;

import com.dbumama.market.codegen.generator.AbstractModuleGen;

/**
 * @author wangjun
 * 驾校模块代码生成
 * 2019年5月12日
 */
public class DriverGenerator extends AbstractModuleGen{
	
	public static void main(String[] args) {
		new DriverGenerator().gen();
	}

	@Override
	protected String getModuleName() {
		return "driver";
	}

	@Override
	protected String getDbTables() {
		return "t_driver_appoint,t_driver_commend,t_driver_course,t_driver_eval,t_driver_exam,t_driver_exam_appoint,t_driver_stu,t_driver_teacher";
	}

}
