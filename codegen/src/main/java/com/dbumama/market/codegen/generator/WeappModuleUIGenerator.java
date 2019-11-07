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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

/**
 * 
 * @author wangjun
 *
 * 2019年5月7日
 */
public class WeappModuleUIGenerator {

    @SuppressWarnings("unused")
	private String moduleName;//product
    private String modulePackage;//

    @SuppressWarnings("unused")
	private String modelPackage;


    private String basePath;
    private String webPath;
    private String templatesDir = "com/dbumama/market/codegen/templates/";

    private Kv data;
    private String[] templates = {"ui_controller_template.jf", "ui_edit_template.jf", "ui_list_template.jf"};
    public static final int UI_CONTROLLER = 0;
    public static final int UI_EDIT = 1;
    public static final int UI_LIST = 2;


    private String controllerOutputDir;
    private String htmlOutputDir;

    private String targetTemplate;
    private String targetOutputDirFile;

    List<TableMeta> tableMetaList;

    private Engine engine = Engine.create("forUI");

    public static void main(String[] args) {

    }

    public WeappModuleUIGenerator(String moduleName, String modelPackage, List<TableMeta> tableMetaList) {

        this.tableMetaList = tableMetaList;
        this.moduleName = moduleName;
        this.modelPackage = modelPackage;
        modulePackage = modelPackage.substring(0, modelPackage.lastIndexOf("."));

        basePath = PathKit.getWebRootPath() + "/../module-wechat-" + moduleName;
        webPath = basePath + "/wechat-" + moduleName + "-web";

        String upcasedModuleName = StrKit.firstCharToUpperCase(moduleName);

        String moduleListenerPakcage = modelPackage.substring(0, modelPackage.lastIndexOf("."));
        String controllerPackage = modelPackage.substring(0, modelPackage.lastIndexOf(".")) + ".web.controller";


        controllerOutputDir = webPath + "/src/main/java/" + controllerPackage.replace(".", "/");
        htmlOutputDir = webPath + "/src/main/webapp/WEB-INF/template/" + moduleName;

        data = Kv.by("moduleName", moduleName);//product
        data.set("upcasedModuleName", upcasedModuleName);//Product
        data.set("modulePackage", modulePackage);//com.dbumama.module.product
        data.set("modelPackage", modelPackage);//com.dbumama.module.product.model
        data.set("moduleListenerPakcage", moduleListenerPakcage);//com.dbumama.module.product
        data.set("controllerPackage", controllerPackage);//com.dbumama.module.product.controller

        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrUtils());

    }

    public WeappModuleUIGenerator genControllers() {
        generate(WeappModuleUIGenerator.UI_CONTROLLER);
        return this;
    }

    public WeappModuleUIGenerator genEdit() {
        generate(WeappModuleUIGenerator.UI_EDIT);
        return this;
    }

    public WeappModuleUIGenerator genList() {
        generate(WeappModuleUIGenerator.UI_LIST);
        return this;
    }

    public void generate(int genType) {

        String targetOutputDir = "";

        for (TableMeta tableMeta : tableMetaList) {
            data.set("tableMeta", tableMeta);
            String lowerCaseModelName = StrKit.firstCharToLowerCase(tableMeta.modelName);
            data.set("lowerCaseModelName", lowerCaseModelName);

            if (WeappModuleUIGenerator.UI_CONTROLLER == genType) {
                targetTemplate = templatesDir + templates[0];
                targetOutputDir = controllerOutputDir;
                targetOutputDirFile = targetOutputDir + File.separator + tableMeta.modelName + "Controller" + ".java";
            }

            if (WeappModuleUIGenerator.UI_EDIT == genType) {
                targetTemplate = templatesDir + templates[1];
                targetOutputDir = htmlOutputDir;
                targetOutputDirFile = targetOutputDir + File.separator + lowerCaseModelName.toLowerCase()+"_edit.html";
            }
            if (WeappModuleUIGenerator.UI_LIST == genType) {
                targetTemplate = templatesDir + templates[2];
                targetOutputDir = htmlOutputDir;
                targetOutputDirFile = targetOutputDir + File.separator + lowerCaseModelName.toLowerCase()+ "_index.html";
            }
            // tableMeta.columnMetas.get(0).remarks
            String content = engine.getTemplate(targetTemplate).renderToString(data);

            //
            File dir = new File(targetOutputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File targetFile = new File(targetOutputDirFile);
            if (targetFile.exists()) {
                return;
            }
            try {
                FileWriter fw = new FileWriter(targetOutputDirFile);
                try {
                    fw.write(content);
                } finally {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
