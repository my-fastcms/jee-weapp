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
package com.dbumama.market.service.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangjun 2018年5月1日
 */
public class WxmallCheck {

	public static boolean checkPrice(String price) {
		String regex = "\\d\\.\\d*|[1-9]\\d*|\\d*\\.\\d*|\\d";
		Pattern pattern = Pattern.compile(regex); // 将给定的正则表达式编译到模式中。
		Matcher isNum = pattern.matcher(price);// 创建匹配给定输入与此模式的匹配器。
		return isNum.matches();
	}

}
