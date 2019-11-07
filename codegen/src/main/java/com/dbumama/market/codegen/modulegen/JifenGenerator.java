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
 * 积分宝模块代码生成
 * 2019年5月12日
 */
public class JifenGenerator extends AbstractModuleGen{
	
	public static void main(String[] args) {
		new JifenGenerator().gen();
	}

	@Override
	protected String getModuleName() {
		return "jifen";
	}

	@Override
	protected String getDbTables() {
		return "t_jifen_award,t_jifen_award_rcd,t_jifen_config,"
				+ "t_jifen_config_reply,t_jifen_config_reply_news,"
				+ "t_jifen_poster_config,t_jifen_redpack_item,t_jifen_user_followers,"
				+ "t_jifen_user_poster";
	}

}
