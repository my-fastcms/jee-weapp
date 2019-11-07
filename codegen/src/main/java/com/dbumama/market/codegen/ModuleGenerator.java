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
package com.dbumama.market.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dbumama.market.codegen.generator.StrUtils;
import com.dbumama.market.codegen.generator.WeappBaseModelGenerator;
import com.dbumama.market.codegen.generator.WeappModelGenerator;
import com.dbumama.market.codegen.generator.WeappModuleUIGenerator;
import com.dbumama.market.codegen.generator.WeappServiceApiGenerator;
import com.dbumama.market.codegen.generator.WeappServiceProviderGenerator;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;


/**
 * 
 * @author wangjun
 *
 * 2019年5月9日
 */
public class ModuleGenerator {

    private String moduleName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbTables;
    private String modelPackage;
    private String servicePackage;
    private String serviceProvierPackage;

    private String basePath;

    private boolean genUI = false;

    public ModuleGenerator(String moduleName, String dbUrl, String dbUser, String dbPassword, String dbTables, String modelPackage, String servicePackage, String serviceProviderPackage) {
        this.moduleName = moduleName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.serviceProvierPackage = serviceProviderPackage;
        this.basePath = PathKit.getWebRootPath() + "/../module-wechat-" + moduleName;
    }

    public ModuleGenerator(String moduleName, String dbUrl, String dbUser, String dbPassword, String dbTables, String modelPackage, String servicePackage, String serviceProviderPackage, boolean genUI) {
        this(moduleName, dbUrl, dbUser, dbPassword, dbTables, modelPackage, servicePackage, serviceProviderPackage);
        this.genUI = genUI;
    }

    public void gen() {

        genModule();
        genPomXml();
        genCode();
    }

    private void genModule() {
        String modelPath = basePath + "/wechat-" + moduleName + "-model";
        String webPath = basePath + "/wechat-" + moduleName + "-web";
        String serviceApiPath = basePath + "/wechat-" + moduleName + "-service-api";
        String serviceProviderPath = basePath + "/wechat-" + moduleName + "-service-provider";

        File modelFile = new File(modelPath);
        File webFile = new File(webPath);
        File serviceApiFile = new File(serviceApiPath);
        File serviceProviderFile = new File(serviceProviderPath);

        modelFile.mkdirs();
        webFile.mkdirs();
        serviceApiFile.mkdirs();
        serviceProviderFile.mkdirs();
    }

    private void genPomXml() {

        String modulePath = basePath;
        String modelPath = basePath + "/wechat-" + moduleName + "-model";
        String webPath = basePath + "/wechat-" + moduleName + "-web";
        String serviceApiPath = basePath + "/wechat-" + moduleName + "-service-api";
        String serviceProviderPath = basePath + "/wechat-" + moduleName + "-service-provider";


        File modelFile = new File(modelPath);
        File webFile = new File(webPath);
        File serviceApiFile = new File(serviceApiPath);
        File serviceProviderFile = new File(serviceProviderPath);

        makeSrcDirectory(modelFile);
        makeSrcDirectory(webFile);
        makeSrcDirectory(serviceApiFile);
        makeSrcDirectory(serviceProviderFile);

        Map<String, String> map = new HashMap<String, String>();
        map.put("moduleName", moduleName);
        Engine engine = new Engine();
        engine.setToClassPathSourceFactory();    // 从 class path 内读模板文件
        engine.addSharedMethod(new StrKit());

        File modulePomXmlFile = new File(modulePath, "pom.xml");
        if (!modulePomXmlFile.exists()) {
            engine.getTemplate("com/dbumama/market/codegen/templates/pom_module_template.jf").render(map, modulePomXmlFile);
        }

        File modelPomXmlFile = new File(modelFile, "pom.xml");
        if (!modelPomXmlFile.exists()) {
            engine.getTemplate("com/dbumama/market/codegen/templates/pom_model_template.jf").render(map, modelPomXmlFile);
        }

        File webPomXmlFile = new File(webFile, "pom.xml");
        if (!webPomXmlFile.exists()) {
            engine.getTemplate("com/dbumama/market/codegen/templates/pom_web_template.jf").render(map, webPomXmlFile);
        }

        File serviceApiPomXmlFile = new File(serviceApiFile, "pom.xml");
        if (!serviceApiPomXmlFile.exists()) {
            engine.getTemplate("com/dbumama/market/codegen/templates/pom_service_api_template.jf").render(map, serviceApiPomXmlFile);
        }

        File serviceProviderPomXmlFile = new File(serviceProviderFile, "pom.xml");
        if (!serviceProviderPomXmlFile.exists()) {
            engine.getTemplate("com/dbumama/market/codegen/templates/pom_service_provider_template.jf").render(map, serviceProviderPomXmlFile);
        }

    }

    private void makeSrcDirectory(File file) {
        if (!file.isDirectory()) {
            return;
        }

        new File(file, "src/main/java").mkdirs();
        new File(file, "src/main/resources").mkdirs();
//        new File(file, "src/test/java").mkdirs();
//        new File(file, "src/test/resources").mkdirs();
    }


    private void genCode() {

        String modelModuleName = "/wechat-" + moduleName + "-model";
        String serviceApiModuleName = "/wechat-" + moduleName + "-service-api";
        String serviceProviderModuleName = "/wechat-" + moduleName + "-service-provider";

//        JbootApplication.setBootArg("jboot.datasource.url", dbUrl);
//        JbootApplication.setBootArg("jboot.datasource.user", dbUser);
//        JbootApplication.setBootArg("jboot.datasource.password", dbPassword);

        String baseModelPackage = modelPackage + ".base";

        String modelDir = basePath + modelModuleName + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = basePath + modelModuleName + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate... dir:" + modelDir);

        DruidPlugin druidPlugin = new DruidPlugin(dbUrl, dbUser, dbPassword);
		druidPlugin.start();
        
        MetaBuilder mb  = new MetaBuilder(druidPlugin.getDataSource());
        mb.setGenerateRemarks(true);
        mb.setRemovedTableNamePrefixes("t_");
        List<TableMeta> tableMetaList = mb.build();
        if (StrKit.notBlank(dbTables)) {
            List<TableMeta> newTableMetaList = new ArrayList<TableMeta>();
            Set<String> excludeTableSet = StrUtils.splitToSet(dbTables, ",");
            for (TableMeta tableMeta : tableMetaList) {
                if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
                    newTableMetaList.add(tableMeta);
                }
            }
            tableMetaList.clear();
            tableMetaList.addAll(newTableMetaList);
        }

        new WeappBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new WeappModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        String apiPath = basePath + serviceApiModuleName + "/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = basePath + serviceProviderModuleName + "/src/main/java/" + serviceProvierPackage.replace(".", "/");

        new WeappServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetaList);
        new WeappServiceProviderGenerator(servicePackage, serviceProvierPackage, modelPackage, providerPath).generate(tableMetaList);
        if (genUI) {
            new WeappModuleUIGenerator(moduleName, modelPackage, tableMetaList).genControllers().genEdit().genList();
        }
    }

}
