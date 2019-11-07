package com.dbumama.market.model;

import io.jboot.db.annotation.Table;
import com.dbumama.market.model.base.BaseProductImage;

/**
 * Generated by Wxmall, do not modify this file.
 * http://www.dbumama.com
 */
@SuppressWarnings("serial")
@Table(tableName = "t_product_image", primaryKey = "id")
public class ProductImage extends BaseProductImage<ProductImage> {
	public static final String table = "t_product_image";
	public static final ProductImage dao = new ProductImage().dao();
}
