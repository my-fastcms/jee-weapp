package com.dbumama.market.service.provider;

import java.util.List;

import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductReview;
import com.dbumama.market.service.api.ProductReviewService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ProductReviewServiceImpl extends WxmServiceBase<ProductReview> implements ProductReviewService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.ProductReviewService#getProductReviews(java.lang.Integer, java.lang.Integer, java.lang.Long)
	 */
	@Override
	public Page<Record> getProductReviews(Integer pageNo, Integer pageSize, Long productId) {
		return Db.paginate(pageNo, pageSize, "select p.*, b.nickname, b.headimgurl " ," from " + ProductReview.table + " p"
        		+ " left join " + BuyerUser.table + " b on p.buyer_id=b.id" + " where product_id=? and p.active = 1 order by p.created desc", productId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.ProductReviewService#getReviewsByBuyer(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<ProductReview> getReviewsByBuyer(Long buyerId, Long orderId, Long productId) {
		return DAO.find("select * from " + ProductReview.table + " where buyer_id=? and order_id=? and product_id=? order by created desc ", buyerId, orderId, productId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.ProductReviewService#getProductReviewsPage(java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.String)
	 */
	@Override
	public Page<Record> getProductReviewsPage(Integer pageNo, Integer pageSize, Long appId, Integer active,
			String content) {
		
		String select = " SELECT pr.id as pr_id, pr.created as pr_created, pr.active, pr.score, pr.content, p.name, p.image, b.nickname "; //o.order_sn, o.order_type ";
		String sqlExceptSelect = " FROM "  + ProductReview.table + " pr "
				+ " left join " + Product.table + " p on pr.product_id=p.id "
				+ " left join " + BuyerUser.table + " b on pr.buyer_id=b.id ";
//				+ " left join " + Order.table + " o on pr.order_id=o.id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("pr.app_id", appId).addWhere("pr.active", active).addWhereLike("pr.content", content).build();
		
		return Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

}