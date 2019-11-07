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

import com.jfinal.core.Controller;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.io.IOException;

@JFinalDirective("para")
public class ParaDirective extends JbootDirectiveBase {


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        String key = getPara(0, scope);
        String defaultValue = getPara(1, scope);

        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("#para(...) argument must not be empty" + getLocation());
        }

        String value = controller.getPara(key);
        if (StrUtil.isBlank(value)) {
            value = StrUtil.isNotBlank(defaultValue) ? defaultValue : "";
        }

        try {
            writer.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

