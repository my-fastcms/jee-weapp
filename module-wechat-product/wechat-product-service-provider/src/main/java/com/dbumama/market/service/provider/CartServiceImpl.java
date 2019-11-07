package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Cart;
import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductSpecItem;
import com.dbumama.market.model.SpecificationValue;
import com.dbumama.market.service.api.CartItemResultDto;
import com.dbumama.market.service.api.CartService;
import com.dbumama.market.service.api.ProdFullCutResultDto;
import com.dbumama.market.service.api.ProductSpecItemService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class CartServiceImpl extends WxmServiceBase<Cart> implements CartService {
	@Inject
	private ProductSpecItemService itemService;
//	@Inject
//	private AgentService agentService;
//	@Inject
//	private AgentProductService agentProductService;
//	
	private static final Cart cartDao = new Cart().dao();
	private static final Product productDao = new Product().dao();
	private static final ProductSpecItem prodSpecItemdao = new ProductSpecItem().dao();
	private static final SpecificationValue specValueDao = new SpecificationValue().dao();
	
	/* (non-Javadoc)
	 * @see com.jfinalshop.service.CartService#add(java.lang.String, int)
	 */
	@Override
	public void add(Long buyerId, Long productId, int quantity, String speci) throws WxmallBaseException {
    	
		if(productId == null){
        	throw new WxmallBaseException("请传入商品id");
        }
        
        Product product = productDao.findById(productId);
        if(product == null){
        	throw new WxmallBaseException("商品不存在");
        }
        
        //定义该商品的库存
    	Integer stock = product.getStock(); 
        
        if(product.getIsUnifiedSpec() !=null && product.getIsUnifiedSpec() == false){
        	//多规格商品，必须传规格值
        	if(StrKit.isBlank(speci)) throw new WxmallBaseException("请选择商品规格");
        	
        	JSONArray jsonArr = JSONArray.parseArray(speci);
        	
        	if(jsonArr == null || jsonArr.size()<=0) throw new WxmallBaseException("请选择商品规格");
        	
        	//多规格下查询改规格库存进行比较
        	ProductSpecItem productSpecItem = itemService.getProductSpecItemByPIDAndSFV(productId, speci);

        	if(productSpecItem != null)
        		stock = productSpecItem.getStock();
        	
        	if(stock == null)
        		throw new WxmallBaseException("商品库存不足");
        	
        	if(stock < quantity)
        		throw new WxmallBaseException("商品库存不足");
        	
        } else{
        	//统一规格下库存与购买量比较
        	if(product.getStock() < quantity)
        		throw new WxmallBaseException("商品库存不足");
        }
        
        if(product.getIsMarketable() == false){
        	throw new WxmallBaseException("商品已下架");
        }
        
        if(stock == null || stock <=0){
        	throw new WxmallBaseException("商品库存不够");
        }
        
        Cart cart = cartDao.findFirst("select * from "+Cart.table+" where buyer_id=? and product_id=? and specification=? ", buyerId, productId, speci);
        if(cart == null){
        	cart = new Cart();
        	cart.setCreated(new Date());
        	cart.setUpdated(new Date());
        	cart.setQuantity(quantity);
        	cart.setProductId(productId);
        	cart.setBuyerId(buyerId);
        	cart.setActive(1);
        	cart.setSpecification(speci);
        	cart.save();
        }else {
        	Integer newStock = cart.getQuantity() + quantity;
        	if(stock < newStock)
        		throw new WxmallBaseException("您购买的数量已经大于库存数量");
        	cart.setQuantity(cart.getQuantity() + quantity);
        	cart.setSpecification(speci);
        	cart.setUpdated(new Date());
        	cart.update();
        }		
	}

	@Override
	public List<CartItemResultDto> getCartsByBuyer(Long buyerId) throws WxmallBaseException {
		if(buyerId == null) throw new WxmallBaseException("获取购物车失败，请检查参数");
		//查找购物车
    	List<Record> records = Db.find(
    			"select c.id as item_id, c.quantity, c.specification, p.`name`, p.price, p.image, p.id as p_id from "+Cart.table+" c "
    			+ "left JOIN "+Product.table+" p on c.product_id=p.id where c.buyer_id=? ", buyerId);
    	
    	List<CartItemResultDto> items = new ArrayList<CartItemResultDto>();
    	for(Record record : records){
    		//获取商品限时打折信息
    		Product product = productDao.findById(record.getLong("p_id"));
    		CartItemResultDto cv = new CartItemResultDto();
    		cv.setGoodPrice(record.getStr("price"));	
			cv.setId(record.getLong("item_id"));
			cv.setGoodId(record.getLong("p_id").toString());
			cv.setGoodName(record.getStr("name").length()>12 ? record.getStr("name").substring(0, 12).concat("...") : record.getStr("name"));
			cv.setGoodImg(record.getStr("image"));
			cv.setSelected(true);
			cv.setMainImg(getImageDomain()+record.getStr("image"));
			cv.setQuantity(record.getInt("quantity"));
			//统一规格下，总的商品库存
			cv.setStock(product.getStock());  
			
			String sfvalue="";
    		if(StrKit.isBlank(record.getStr("specification"))){
    			//统一规格下，获取商品限时打折
//    			String promoPrice = promotionService.getProductPromotionPrice(product);
//    			if(StrKit.notBlank(promoPrice)) cv.setGoodPrice(promoPrice);
    		}else{
    			//多规格
    			List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();

        		JSONArray jsonArr = JSON.parseArray(record.getStr("specification"));
        		if(jsonArr.size()>0){
            		for(int i=0;i<jsonArr.size();i++){
            			JSONObject json = jsonArr.getJSONObject(i);
            			Long spvid = json.getLong("spvId");
            			sfvalue+=json.getString("spvId")+",";
            			specificationValues.add(specValueDao.findById(spvid));
            		}
            		sfvalue=sfvalue.substring(0,sfvalue.length()-1);
            		cv.setSpecificationValues(specificationValues);	
        		}

        		ProductSpecItem stock = prodSpecItemdao.findFirst(
        				"select * FROM "+ProductSpecItem.table+" WHERE product_id = ? and specification_value = ?", record.getLong("p_id"), sfvalue);
        		if(stock != null){
        			String price=stock.getPrice().toString();
            		if(StrKit.notBlank(price)){
            			cv.setGoodPrice(price);
            			//多规格下，该规格的库存数量
            			cv.setStock(stock.getStock());
            		}	
        		}
//        		String promoPrice = promotionService.getProductPromotionPrice(product, stock);
//        		if(StrKit.notBlank(promoPrice)){
//        			cv.setGoodPrice(promoPrice);
//        		}
    		}	
    		
    		//是否分销商品，如果是分销商品，设置分销价格
//			List<AgentProduct> agentProducts = agentProductService.getAgentProducts(product.getId());
//			if(agentProducts != null && agentProducts.size() >0){
//				Agent agent = agentService.findByBuyerId(buyerId);
//				if(agent != null){
//					AgentRank rank =  agentService.getAgentRank(agent);
//					AgentProduct agentProduct = agentProductService.findByProductId(product.getId(), rank.getId(), sfvalue);
//					if(agentProduct != null){
//						cv.setGoodPrice(agentProduct.getAgentPrice().toString());
//					}
//				}
//			}

    		//获取商品满减信息
//    		List<ProdFullCutResultDto> productFullCutResultDtos = fullCutService.getProductFullCut(product);
//    		cv.setFullCutResultDtos(productFullCutResultDtos);
    		
    		items.add(cv);
    	}
    	return items;
	}

	@Override
	public Long getCartItemCountByBuyer(Long buyerId) throws WxmallBaseException {
		return Db.queryLong("select count(id) from " + Cart.table + " t where t.buyer_id=?", buyerId);
	}

	@Override
	public List<ProdFullCutResultDto> getCartFullCat(List<CartItemResultDto> cartItemParamDtos) {
		List<ProdFullCutResultDto> resultDtos = new ArrayList<ProdFullCutResultDto>();
		for(CartItemResultDto cartItemParamDto : cartItemParamDtos){
			if(cartItemParamDto.getFullCutResultDtos() != null && cartItemParamDto.getFullCutResultDtos().size()>0){
				resultDtos.addAll(cartItemParamDto.getFullCutResultDtos());
			}
		}
		//按满减的金额进行升序排列
		Collections.sort(resultDtos, new Comparator<ProdFullCutResultDto>(){
			@Override
			public int compare(ProdFullCutResultDto o1, ProdFullCutResultDto o2) {
				return o1.getMeet().subtract(o2.getMeet()).intValue();
			}
		});
		return resultDtos;
	}

}