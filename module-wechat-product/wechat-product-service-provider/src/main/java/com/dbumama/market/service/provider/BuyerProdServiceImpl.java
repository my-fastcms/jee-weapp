package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbumama.market.model.BuyerProd;
import com.dbumama.market.model.Product;
import com.dbumama.market.service.api.BuyerProdItemResultDto;
import com.dbumama.market.service.api.BuyerProdService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class BuyerProdServiceImpl extends WxmServiceBase<BuyerProd> implements BuyerProdService {
	
	private static final BuyerProd buyerProdDao = new BuyerProd().dao();
	private static final Product productDao = new Product().dao();

	@Override
	public List<BuyerProdItemResultDto> getProdByBuyer(Long buyerId) throws WxmallBaseException {
		
		if(buyerId == null) throw new WxmallBaseException("获取购物车失败，请检查参数");
		//查找改用户收藏的商品
    	List<Record> records = Db.find(
    			"select b.id as item_id, p.name, p.price, p.image, p.id as p_id from "+BuyerProd.table+" b "
    			+ "left JOIN "+Product.table+" p on b.product_id=p.id where b.buyer_id=? and b.active=1", buyerId);
    	
    	List<BuyerProdItemResultDto> items = new ArrayList<BuyerProdItemResultDto>();
    	for(Record record : records){
    		BuyerProdItemResultDto bp = new BuyerProdItemResultDto();
    		bp.setGoodPrice(record.getStr("price"));	
    		bp.setId(record.getLong("item_id"));
    		bp.setGoodId(record.getLong("p_id").toString());
    		bp.setGoodName(record.getStr("name").length()>12 ? record.getStr("name").substring(0, 12).concat("...") : record.getStr("name"));
    		bp.setGoodImg(record.getStr("image"));
    		bp.setMainImg(getImageDomain()+record.getStr("image"));
    		
    		items.add(bp);
    	}	
		return items;
		
	}
	
	@Override
	public Long getProdCountByBuyer(Long buyerId) throws WxmallBaseException {
		return Db.queryLong("select count(id) from " + BuyerProd.table + " t where t.buyer_id=?", buyerId);
	}

	@Override
	public void add(Long buyerId, Long productId) throws WxmallBaseException {
		if(productId == null){
        	throw new WxmallBaseException("请传入商品id");
        }
        
        Product product = productDao.findById(productId);
        if(product == null){
        	throw new WxmallBaseException("商品不存在");
        }
        if(product.getIsMarketable() == false){
        	throw new WxmallBaseException("商品已下架");
        }
        BuyerProd buyerProd = buyerProdDao.findFirst("select * from "+BuyerProd.table+" where buyer_id=? and product_id=?", buyerId, productId);
        if(buyerProd == null){
        	buyerProd = new BuyerProd();
        	buyerProd.setCreated(new Date());
        	buyerProd.setUpdated(new Date());
        	buyerProd.setProductId(productId);
        	buyerProd.setBuyerId(buyerId);
        	buyerProd.setActive(true);
        	buyerProd.save();
        }else {
        	buyerProd.setUpdated(new Date());
        	buyerProd.update();
        }	
	}
    	
	
	
}