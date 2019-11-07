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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dbumama.market.codegen.generator.StrUtils;
import com.dbumama.market.codegen.generator.WeappBaseModelGenerator;
import com.dbumama.market.codegen.generator.WeappModelGenerator;
import com.dbumama.market.codegen.generator.WeappServiceApiGenerator;
import com.dbumama.market.codegen.generator.WeappServiceProviderGenerator;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 
 * @author wangjun
 *
 * 2019年5月7日
 */
public class SystemGenerator {

    public static void main(String[] args) {

        String dbTables = "t_seller_user,t_user_code,t_selleruser_balance_rcd,t_selleruser_recharge_rcd,"
        		+ "t_seller_cash_rcd,t_buyer_user,"
        		+ "t_auth_user,t_auth_user_app,t_auth_user_style,t_auth_cert,"
        		+ "t_comp_ticket,t_app,t_app_order,"
        		+ "t_menu,t_employee,t_role,t_role_permission";

        final String dbUrl = "jdbc:mysql://127.0.0.1:3307/weapp";
        final String dbUser = "root";
        final String dbPassword = "root";

        String modelPackage = "com.dbumama.market.model";

        String baseModelPackage = modelPackage + ".base";

        String modelDir = PathKit.getWebRootPath() + "/../weapp-model/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/../weapp-model/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...dir:" + modelDir);

        List<TableMeta> tableMetaList = new ArrayList<>();
        Set<String> excludeTableSet = StrUtils.splitToSet(dbTables, ",");
        
        DruidPlugin druidPlugin = new DruidPlugin(dbUrl, dbUser, dbPassword);
		druidPlugin.start();
        
        MetaBuilder builder = new MetaBuilder(druidPlugin.getDataSource());
        builder.setRemovedTableNamePrefixes("t_");
        
        for (TableMeta tableMeta : builder.build()) {
            if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
                tableMetaList.add(tableMeta);
            }
        }


        new WeappBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new WeappModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        String servicePackage = "com.dbumama.market.service.api";
        String serviceProviderPackage = "com.dbumama.market.service.provider";
        String apiPath = PathKit.getWebRootPath() + "/../weapp-service-api/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = PathKit.getWebRootPath() + "/../weapp-service-provider/src/main/java/" + servicePackage.replace(".", "/") + "/provider";


        new WeappServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetaList);
        new WeappServiceProviderGenerator(servicePackage, serviceProviderPackage, modelPackage, providerPath).generate(tableMetaList);

    }

}
