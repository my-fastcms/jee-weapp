package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseProductSpecItem;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_product_spec_item", primaryKey = "id")
public class ProductSpecItem extends BaseProductSpecItem<ProductSpecItem> {
	public static final String table = "t_product_spec_item";
	public static final ProductSpecItem dao = new ProductSpecItem().dao();
}
