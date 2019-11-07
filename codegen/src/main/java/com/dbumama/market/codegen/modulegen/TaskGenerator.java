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
 * 任务宝模块代码生成
 * 2019年5月12日
 */
public class TaskGenerator extends AbstractModuleGen{
	
	public static void main(String[] args) {
		new TaskGenerator().gen();
	}

	@Override
	protected String getModuleName() {
		return "task";
	}

	@Override
	protected String getDbTables() {
		return "t_task,t_task_award,t_task_award_reply_config,"
				+ "t_task_award_reply_news,t_task_award_sendrcd,"
				+ "t_task_defriend,t_task_defriend_user,t_task_message_config,"
				+ "t_task_poster_config,t_task_redpack_item,t_task_reply_config,"
				+ "t_task_reply_news,t_task_tpl_formid,t_task_user_curraward,"
				+ "t_task_user_followers,t_task_user_poster";
	}

}
