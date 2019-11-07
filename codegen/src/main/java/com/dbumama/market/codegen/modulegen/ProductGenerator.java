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
package com.dbumama.market.codegen.modulegen;

import com.dbumama.market.codegen.generator.AbstractModuleGen;

/**
 * @author wangjun
 * 商品模块代码生成
 * 2019年5月12日
 */
public class ProductGenerator extends AbstractModuleGen{
	
	public static void main(String[] args) {
		new ProductGenerator().gen();
	}

	@Override
	protected String getModuleName() {
		return "product";
	}

	@Override
	protected String getDbTables() {
		return "t_product,t_product_brand,t_product_category,t_product_group,"
				+ "t_product_group_set,t_product_image,"
				+ "t_product_review,"
				+ "t_product_spec,t_product_spec_item,t_product_spec_value,"
				+ "t_specification,t_specification_value,"
				+ "t_delivery_set,t_delivery_template,"
				+ "t_cart";
	}

}
