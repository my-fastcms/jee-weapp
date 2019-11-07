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
package com.dbumama.market.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.ProductReview;
import com.dbumama.market.service.api.ProductReviewService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author wangjun
 * 2018年6月8日
 */
@RequestMapping(value = "/reviews", viewPath="order")
@RequiresPermissions(value="/reviews")
public class ReviewsController extends BaseAppAdminController{

	@RPCInject
	private ProductReviewService productReviewService;
	
	public void index(){
		render("reviews_index.html");
	}
	
	public void list(){
		rendSuccessJson(productReviewService.getProductReviewsPage(getPageNo(), getPageSize(), getAuthUserId(), getParaToInt("active"), getPara("content")));
	}
	
	public void del(){
		ProductReview pr = productReviewService.findById(getParaToLong("ids"));
		if(pr != null){
			pr.setActive(false);
			productReviewService.update(pr);
		}
		rendSuccessJson();
	}
	
	public void undel(){
		ProductReview pr = productReviewService.findById(getParaToLong("ids"));
		if(pr != null){
			pr.setActive(true);
			productReviewService.update(pr);
		}
		rendSuccessJson();
	}
	
}
