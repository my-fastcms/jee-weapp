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
package com.dbumama.market.codegen.generator;

import com.dbumama.market.codegen.ModuleGenerator;
import com.jfinal.kit.StrKit;

/**
 * @author wangjun
 *
 * 2019年5月12日
 */
public abstract class AbstractModuleGen {

	protected static String dbUrl = "jdbc:mysql://127.0.0.1:3307/weapp?useInformationSchema=true";
	protected static String dbUser = "root";
	protected static String dbPassword = "root";
	
	protected abstract String getModuleName();
	protected abstract String getDbTables();
	
	public void gen() {
		
		if(StrKit.isBlank(getModuleName())) throw new RuntimeException("module name is null");
		
		if(StrKit.isBlank(getDbTables())) throw new RuntimeException("dbTables is null");
		
		ModuleGenerator moduleGenerator = new ModuleGenerator(getModuleName(), dbUrl, dbUser, dbPassword, 
				getDbTables(), "com.dbumama.market.model", "com.dbumama.market.service.api", "com.dbumama.market.service.provider", true);
        
        moduleGenerator.gen();
	}
	
}
