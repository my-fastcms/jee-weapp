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
package com.dbumama.market.web.core.directive;

import com.dbumama.market.utils.DateTimeUtil;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.io.IOException;
import java.util.Date;

/**
 * @author wangjun
 * 2018年5月24日
 */
@JFinalDirective(value = "purcdays")
public class PurchaseDaysDirective extends JbootDirectiveBase{

	/* (non-Javadoc)
	 * @see io.jboot.web.directive.base.JbootDirectiveBase#onRender(com.jfinal.template.Env, com.jfinal.template.stat.Scope, com.jfinal.template.io.Writer)
	 */
	@Override
	public void onRender(Env env, Scope scope, Writer writer) {
		Date endDate = getPara(0, scope);
		
		int days = DateTimeUtil.compareDay(endDate, new Date());
		
		try {
			writer.write(days <0 ? "订购已过期" : days + 1 + "天后过期");
		} catch (IOException e) {
			throw new TemplateException(e.getMessage(), location, e);
		}
	}

	@Override
    public boolean hasEnd() {
        return false;
    }
	
}
