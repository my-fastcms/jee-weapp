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
package com.dbumama.market.service.enmu;

/**
 * 发奖状态
 * @author wangjun
 * 2018年10月26日
 */
public enum AwardSendStatus {
	
	/** 等待发奖 **/
	WAIT_SEND,
	
	/** 发奖成功 **/
	SUCC_SEND,
	
	/** 发奖失败 **/
	FAIL_SEND
	
}
