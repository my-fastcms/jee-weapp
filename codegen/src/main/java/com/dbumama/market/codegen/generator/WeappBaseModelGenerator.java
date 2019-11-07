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

/**
 * @author wangjun
 *
 * 2019年5月7日
 */
public class WeappBaseModelGenerator extends com.jfinal.plugin.activerecord.generator.BaseModelGenerator {

    public WeappBaseModelGenerator(String baseModelPackageName,
                              String baseModelOutputDir) {
        super(baseModelPackageName, baseModelOutputDir);

        this.template = "/com/dbumama/market/codegen/templates/base_model_template.jf";

        setGenerateChainSetter(true);

    }


}
