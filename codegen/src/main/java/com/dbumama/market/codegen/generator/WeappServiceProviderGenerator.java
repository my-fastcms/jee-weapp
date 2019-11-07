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

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author wangjun
 *
 * 2019年5月7日
 */
public class WeappServiceProviderGenerator extends BaseModelGenerator {

	String servicePackage;
    String basePackage;
    String modelPackage;

    public WeappServiceProviderGenerator(String servicePackage, String basePackage, String modelPackage, String baseModelOutputDir) {
        super(basePackage, baseModelOutputDir);

        this.servicePackage = servicePackage;
        this.basePackage = basePackage;
        this.modelPackage = modelPackage;
        this.template = "com/dbumama/market/codegen/templates/service_impl_template.jf";


    }

    @Override
    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate base model ...");
        System.out.println("Base Model Output Dir: " + baseModelOutputDir);

        Engine engine = Engine.create("forServiceImpl");
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", getterTypeMap);
        engine.addSharedObject("javaKeyword", javaKeyword);

        for (TableMeta tableMeta : tableMetas) {
            genBaseModelContent(tableMeta);
        }
        writeToFile(tableMetas);
    }


    @Override
    protected void genBaseModelContent(TableMeta tableMeta) {
        Kv data = Kv.by("serviceImplPackageName", baseModelPackageName);
        data.set("generateChainSetter", generateChainSetter);
        data.set("tableMeta", tableMeta);
        data.set("servicePackage", servicePackage);
        data.set("basePackage", basePackage);
        data.set("modelPackage", modelPackage);

        Engine engine = Engine.use("forServiceImpl");
        tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
    }

    /**
     * base model 覆盖写入
     */
    @Override
    protected void writeToFile(TableMeta tableMeta) throws IOException {
        File dir = new File(baseModelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = baseModelOutputDir + File.separator + tableMeta.modelName + "ServiceImpl" + ".java";

        File targetFile = new File(target);
        if (targetFile.exists()) {
            return;
        }


        FileWriter fw = new FileWriter(target);
        try {
            fw.write(tableMeta.baseModelContent);
        } finally {
            fw.close();
        }
    }
}
