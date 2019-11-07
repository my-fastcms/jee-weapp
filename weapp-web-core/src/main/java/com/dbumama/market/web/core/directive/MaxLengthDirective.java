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

import java.io.IOException;

import com.dbumama.market.web.core.utils.CommonUtil;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

@JFinalDirective("maxLength")
public class MaxLengthDirective extends JbootDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String content = getPara(0, scope);
        if (StrUtil.isBlank(content)) {
            return;
        }

        int maxLength = getPara(1, scope, 0);
        if (maxLength <= 0) {
            throw new IllegalArgumentException("#maxLength(content,length) 参数错误，length必须大于0 " + getLocation());
        }

        String suffix = getPara(2, scope);
        try {
            writer.write(CommonUtil.maxLength(content, maxLength, suffix));
        } catch (IOException e) {
            throw new TemplateException(e.getMessage(), location, e);
        }
    }
}

