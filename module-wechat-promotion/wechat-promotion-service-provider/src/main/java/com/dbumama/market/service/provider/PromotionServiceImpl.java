package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Product;
import com.dbumama.market.model.ProductSpecItem;
import com.dbumama.market.model.Promotion;
import com.dbumama.market.model.PromotionSet;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.ActivityStatus;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.utils.DateTimeUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Bean
@RPCBean
public class PromotionServiceImpl extends WxmServiceBase<Promotion> implements PromotionService {
	private static final Product productDao = new Product().dao();
	private static final ProductSpecItem prodSpecItemdao = new ProductSpecItem().dao();
	private static final Promotion promotiondao = new Promotion().dao();
	private static final PromotionSet promotionSetdao = new PromotionSet().dao();
	
	@Override
	@Before(Tx.class)
	public void save(Promotion promotion, Long appId, String promotionSetItems) throws UmpException {
		if(StrKit.isBlank(promotionSetItems) || appId == null)
			throw new UmpException("保存限时折扣出错:参数不全");
		
		if(promotion.getStartDate().before(new Date()) && promotion.getEndDate().after(new Date())){
			promotion.setStatus(ActivityStatus.activing.ordinal());//进行中
		}else if(promotion.getStartDate().after(new Date())){
			promotion.setStatus(ActivityStatus.unactive.ordinal());
		}else if(promotion.getEndDate().before(new Date())){
			promotion.setStatus(ActivityStatus.activeend.ordinal());
		}
		
		try {
			saveOrUpdate(promotion);		
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
		
		JSONArray jarr = null;
		try {
			jarr = JSONArray.parseArray(promotionSetItems);
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
		if(jarr == null || jarr.size() <=0) throw new UmpException("未设置活动折扣信息");
		
		JSONArray newArr = new JSONArray();
		for(Iterator<?> iterator = jarr.iterator(); iterator.hasNext();){
			JSONObject job = (JSONObject) iterator.next(); 
			 if(!"del".equals(job.getString("opt"))){
				newArr.add(job);
			 }
		}
		if(newArr.size()<=0) throw new UmpException("没有设置打折商品");

		//需要添加的折扣设置项
		List<PromotionSet> promotionAddSets = new ArrayList<PromotionSet>();
		//需要更新的折扣设置项
		List<PromotionSet> promotionUpdateSets = new ArrayList<PromotionSet>();
		//需要删除的折扣设置项
		/*List<PromotionSet> promotionDelSets = new ArrayList<PromotionSet>();*/
		for(int i=0;i<jarr.size();i++){
			JSONObject jsonObj = jarr.getJSONObject(i);
			Long promotionSetId = jsonObj.getLong("psetId");
			
			if(promotionSetId == null && "del".equals(jsonObj.getString("opt"))) continue;
			
			if(jsonObj == null 
					|| jsonObj.getLong("productId") == null 
					|| jsonObj.getFloat("jianjia") == null 
					|| jsonObj.getFloat("zhekou") == null
					|| jsonObj.getInteger("ptype") == null
					|| jsonObj.getFloat("promotion") == null 
					|| (!"del".equals(jsonObj.getString("opt")) && jsonObj.getFloat("promotion") <=0)) //新增或修改的时候，打折或减价后商品价格必须大于0
				throw new UmpException("保存折扣活动，折扣设置项值有误，请检查，打折后的价格必须大于0");
			
			PromotionSet promotionSet = null;
			if(promotionSetId == null){
				promotionSet = new PromotionSet();
				promotionSet.setProductId(jsonObj.getLong("productId"));
				promotionSet.setPromotionId(promotion.getId());
				promotionSet.setCreated(new Date());
				promotionSet.setActive(true);
				promotionSet.setPromotionSetJianjia(jsonObj.getFloat("jianjia"));
				promotionSet.setPromotionSetZhekou(jsonObj.getFloat("zhekou"));
				promotionSet.setPromotionType(jsonObj.getInteger("ptype"));
				promotionSet.setPromotionValue(jsonObj.getFloat("promotion"));
				promotionSet.setUpdated(new Date());
				promotionAddSets.add(promotionSet);
			}else{
				promotionSet = promotionSetdao.findById(promotionSetId);
				if(promotionSet == null) throw new UmpException("折扣设置记录不存在");
				if("del".equals(jsonObj.getString("opt"))){
					/*promotionDelSets.add(promotionSet);*/
					try {
						promotionSet.delete();	
					} catch (Exception e) {
						throw new UmpException(e.getMessage());
					}
				}
				if("updated".equals(jsonObj.getString("opt"))){
					promotionSet.setPromotionSetJianjia(jsonObj.getFloat("jianjia"));
					promotionSet.setPromotionSetZhekou(jsonObj.getFloat("zhekou"));
					promotionSet.setPromotionType(jsonObj.getInteger("ptype"));
					promotionSet.setPromotionValue(jsonObj.getFloat("promotion"));
					promotionSet.setUpdated(new Date());
					promotionUpdateSets.add(promotionSet);
				}
			}
		}
		
		/*if(promotionAddSets.size() <=0 && promotionUpdateSets.size()<=0)
			throw new UmpException("没有设置折扣项数据");*/
		
		try {
			if(promotionAddSets.size() > 0){
				Db.batchSave(promotionAddSets, promotionAddSets.size());	
			}
			if(promotionUpdateSets.size() > 0){
				Db.batchUpdate(promotionUpdateSets, promotionUpdateSets.size());	
			}
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
	}
	
	@Override
	public Page<ProductResultDto> getProductsNoPromotionPage(ProductParamDto productParamDto) throws ProductException {
		if(productParamDto == null || productParamDto.getAuthUserId() == null)
			throw new ProductException("获取未打折商品列表出错");

		//查询出有效打折数据
		List<ProductResultDto> resultDtos = new ArrayList<ProductResultDto>();
		
		final String select = "select * ";
		String sqlExceptSelect = " from "+Product.table+" where app_id=? and is_marketable=1 and active=1 ";
		
		Date currDate = new Date();
		List<Promotion> promotions = DAO.find("select * from "+Promotion.table+" where app_id=? and active=1 and start_date<=? and end_date >=? ", productParamDto.getAuthUserId(), currDate, currDate);
		if(promotions != null && promotions.size()>0){
			StringBuffer sbuff = new StringBuffer();
			for(Promotion promotion : promotions){
				sbuff.append(promotion.getId()).append(",");
			}
			sbuff.deleteCharAt(sbuff.length() - 1);
			
			String where = " and id not in"
					+ " (select product_id from " + PromotionSet.table + " where promotion_id "
					+ "in ("+sbuff.toString()+"))";
			
			sqlExceptSelect += where;
		}
		
		
		Page<Product> pages = productDao.paginate(productParamDto.getPageNo(), productParamDto.getPageSize(),
				select, sqlExceptSelect,productParamDto.getAuthUserId());
		for(Product product : pages.getList()){
			ProductResultDto resultDto = new ProductResultDto();
			resultDto.setId(product.getId());
			resultDto.setImg(getImageDomain() + product.getImage());
			resultDto.setName(product.getName().length()>20 ? product.getName().substring(0, 20).concat("...") : product.getName());
			resultDto.setPrice(product.getPrice());
			resultDto.setStock(product.getStock());
			resultDtos.add(resultDto);
		}
	
		return new Page<ProductResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public PromotionResultDto getPromotionInfo(Long id) throws UmpException {
		if(id == null) return null;
		Promotion promotion = promotiondao.findById(id);
		if(promotion == null) return null;
		return getPromotionInfo(promotion);
	}

	@Override
	public PromotionResultDto getPromotionInfo(Promotion promotion) throws UmpException {
		PromotionResultDto promotionDto = new PromotionResultDto();
		promotionDto.setPromotionId(promotion.getId());
		promotionDto.setPromotionName(promotion.getPromotionName());
		promotionDto.setPromotionTag(promotion.getPromotionTag());
		promotionDto.setStartDate(promotion.getStartDate());
		promotionDto.setEndDate(promotion.getEndDate());
		List<Record> promotionSets = Db.find("select ps.*, p.id as product_id, p.name, p.image, p.price from " + PromotionSet.table + " ps " 
				+ " left join " + Product.table + " p on ps.product_id=p.id "
				+ " where promotion_id=? ", promotion.getId());
		List<PromotionSetResultDto> setResultDtos = new ArrayList<PromotionSetResultDto>();
		for(Record record : promotionSets){
			PromotionSetResultDto psrDto = new PromotionSetResultDto();
			psrDto.setId(record.getLong("id"));
			psrDto.setProductId(record.getLong("product_id"));
			psrDto.setPromotinId(promotion.getId());
			psrDto.setProductName(record.getStr("name").length()>20 ? record.getStr("name").substring(0, 20).concat("...") : record.getStr("name"));
			psrDto.setProductImg(getImageDomain() + record.getStr("image"));
			psrDto.setProductPrice(record.getStr("price"));
			psrDto.setType(record.getInt("promotion_type"));
			psrDto.setJianjia(new BigDecimal(record.getFloat("promotion_set_jianjia")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			psrDto.setZhekou(new BigDecimal(record.getFloat("promotion_set_zhekou")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			psrDto.setPromotionValue(new BigDecimal(record.getFloat("promotion_value")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			setResultDtos.add(psrDto);
		}
		promotionDto.setPromotionSets(setResultDtos);
		return promotionDto;
	}
	
	@Override
	public Page<PromotionResultDto> list(PromotionParamDto promotionParam) throws UmpException{
		if(promotionParam == null)
			throw new UmpException("获取限时打折列表数据参数错误");
		
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", promotionParam.getAuthUserId()));
		columns.add(Column.create("active", promotionParam.getActive()));
		columns.add(Column.create("status", promotionParam.getStatus()));
		
		Page<Promotion> pages = promotiondao.paginateByColumns(promotionParam.getPageNo(), promotionParam.getPageSize(), columns, " updated desc ");
		
		List<PromotionResultDto> promotionDtos = new ArrayList<PromotionResultDto>();
		
		for(Promotion promotion : pages.getList()){
			PromotionResultDto promotionDto = new PromotionResultDto();
			promotionDto.setPromotionId(promotion.getId());
			promotionDto.setPromotionName(promotion.getPromotionName());
			promotionDto.setPromotionTag(promotion.getPromotionTag());
			promotionDto.setStartDate(promotion.getStartDate());
			promotionDto.setEndDate(promotion.getEndDate());
			promotionDto.setStatus(promotion.getStatus());
			promotionDto.setActive(promotion.getActive() == true ? 1 : 0);
			promotionDtos.add(promotionDto);
		}
		
		return new Page<PromotionResultDto> (promotionDtos, promotionParam.getPageNo(), promotionParam.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public ProdPromotionResultDto getProductPromotion(Product product) throws UmpException {
		//查询卖家有效时间范围内的促销活动
        List<Promotion> promotions = promotiondao.find(
        		"select * from " + Promotion.table + " where app_id=? and active=1 and start_date<=? and end_date>=? ",
        		product.getAppId(), new Date(), new Date());
        for(Promotion promo : promotions){
        	PromotionResultDto promotionResultDto=getPromotionInfo(promo);
        	List<PromotionSetResultDto> promotionSets = promotionResultDto.getPromotionSets();
        	for(PromotionSetResultDto psrDto : promotionSets){
        		if(psrDto.getProductId() == product.getId()){
        			//获取该商品对应的打折数据
        			ProdPromotionResultDto prodprom = new ProdPromotionResultDto();
        			prodprom.setPromotionTag(promo.getPromotionTag());	//活动标签
        			prodprom.setPromotionPrice(psrDto.getPromotionValue().toString());//打折后的价格
        			if(psrDto.getType() == 1){
        				prodprom.setPromotionInfo(psrDto.getZhekou() + "折 ");
        			}else{
        				prodprom.setPromotionInfo("减￥" + new BigDecimal(psrDto.getJianjia()).setScale(2, BigDecimal.ROUND_HALF_UP));
        			}
        			if(promo.getStartDate().after(new Date())){
        				prodprom.setPromotionTime("还差" + DateTimeUtil.compareDay(promo.getStartDate(), new Date()) + "天开始");        				
        			}else {
        				prodprom.setPromotionTime("剩余" + DateTimeUtil.compareDay(promo.getEndDate(), new Date()) + "天结束");
        			}
        			//如果商品是多规格的话，折扣价也是一个范围
        			String promotionPrice = getProductPromotionPriceSection(product, psrDto);
        			if(StrKit.notBlank(promotionPrice)) prodprom.setPromotionPrice(promotionPrice);
        			return prodprom;
        		}
        	}
        }
		return null;
	}

	@Override
	public String getProductPromotionPriceSection(Product product, PromotionSetResultDto promotionSetParam) throws UmpException {
		List<ProductSpecItem> productSpecItems = prodSpecItemdao.find(" select * from "
				+ ProductSpecItem.table + " where product_id=? ", product.getId());
		ProductSpecItem prodSpecFirst = null;
		if(productSpecItems !=null && productSpecItems.size()>0){
			prodSpecFirst = productSpecItems.get(0);
			BigDecimal min, max; min = max = prodSpecFirst.getPrice();
			for(ProductSpecItem productSpecItem : productSpecItems){
		          BigDecimal bprice = productSpecItem.getPrice();
		          if(bprice.compareTo(max)==1){
		        	  max= bprice;
		          }
		          if(bprice.compareTo(min)==-1){
		        	  min= bprice;
		          }
		    }
			BigDecimal minProm, maxProm; String promotionPrice = "";
			if(max.compareTo(min)==0) {
				if(promotionSetParam.getType() == 1) {
					//打折 保留两位小数，四舍五入
					minProm = min.multiply(new BigDecimal(promotionSetParam.getZhekou()/10)).setScale(2, BigDecimal.ROUND_HALF_UP);
				}else {
					//减价
					minProm = min.subtract(new BigDecimal(promotionSetParam.getJianjia())).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				promotionPrice = minProm.toString();
			}else {  
				if(promotionSetParam.getType() ==1) {
					//打折
					minProm = min.multiply(new BigDecimal(promotionSetParam.getZhekou()/10)).setScale(2, BigDecimal.ROUND_HALF_UP);
					maxProm = max.multiply(new BigDecimal(promotionSetParam.getZhekou()/10)).setScale(2, BigDecimal.ROUND_HALF_UP);
				}else {
					//减价
					minProm = min.subtract(new BigDecimal(promotionSetParam.getJianjia())).setScale(2, BigDecimal.ROUND_HALF_UP);
					maxProm = max.subtract(new BigDecimal(promotionSetParam.getJianjia())).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				promotionPrice = minProm.toString() + " ~ " + maxProm.toString();
			}
			return promotionPrice;
		}
		return null;
	}

	@Override
	public PromotionSet getProductPromotionSet(Product product) throws UmpException {
		List<Promotion> promotions = promotiondao.find(
        		"select * from " + Promotion.table + " where app_id=? and active=1 and start_date<=? and end_date>=? ",
        		product.getAppId(), new Date(), new Date());
		for(Promotion promo : promotions){
			List<PromotionSet> promotionSets = promotionSetdao.find("select * from " + PromotionSet.table + " where promotion_id=? ", promo.getId());
			for(PromotionSet ps : promotionSets){
				if(ps.getProductId() == product.getId())
					return ps;
			}
		}
		return null;
	}

	@Override
	public String getProductPromotionPrice(Product product, ProductSpecItem specItem) throws UmpException {
		PromotionSet promotionSet = getProductPromotionSet(product);
		if(promotionSet ==null){
			return null;
		}
		
		if(product.getIsUnifiedSpec() !=null && product.getIsUnifiedSpec() == false){
			//多规格商品
			if(specItem == null) return null;
		}
		
		BigDecimal promoPrice = null;
		BigDecimal oldPrice = specItem != null ? specItem.getPrice() : new BigDecimal(product.getPrice());
		if(promotionSet.getPromotionType() == 1){//打折
			promoPrice = oldPrice.multiply(new BigDecimal(promotionSet.getPromotionSetZhekou()/10));
		}else{
			//减价
			promoPrice = oldPrice.subtract(new BigDecimal(promotionSet.getPromotionSetJianjia()));
		}
		if(promoPrice == null) {
			return null;
		}
		
		promoPrice = promoPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		return promoPrice.toString();
		
	}

	@Override
	public String getProductPromotionPrice(Product product) throws UmpException {
		return getProductPromotionPrice(product, null);
	}

	@Override
	public Page<ProductResultDto> getProductsPromotionPage(ProductParamDto productParamDto) throws ProductException {
		if(productParamDto == null || productParamDto.getAuthUserId() == null || productParamDto.getPromotionId() == null) throw new ProductException("获取未打折商品列表出错");
		List<ProductResultDto> resultDtos = new ArrayList<ProductResultDto>();
		
		//查询出有效打折数据
		Date currDate = new Date();
		final String select = "select * ";
		final String sqlExceptSelect = " from "+Product.table+" where app_id= ? and is_marketable=1 and active=1 and id  in"
				+ " (select product_id from " + PromotionSet.table + " where promotion_id "
				+ "in (select id from "+Promotion.table+" where app_id=? and active=1 and start_date<=? and end_date >=? and id= ? ))";
		Page<Product> pages = productDao.paginate(productParamDto.getPageNo(), productParamDto.getPageSize(),
				select, sqlExceptSelect, productParamDto.getAuthUserId(), productParamDto.getAuthUserId(), currDate, currDate,productParamDto.getPromotionId());
		for(Product product : pages.getList()){
			ProductResultDto resultDto = new ProductResultDto();
			resultDto.setId(product.getId());
			resultDto.setImg(getImageDomain() + product.getImage());
			resultDto.setName(product.getName().length()>10?product.getName().substring(0, 10).concat("..."):product.getName());
			resultDto.setPrice(product.getPrice());
			resultDto.setStock(product.getStock());
			resultDtos.add(resultDto);
		}
	
		return new Page<ProductResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public PromotionResultDto getDiscountInfo(Promotion promotion) throws UmpException {
		PromotionResultDto promotionDto = new PromotionResultDto();
		promotionDto.setPromotionId(promotion.getId());
		promotionDto.setPromotionName(promotion.getPromotionName());
		promotionDto.setPromotionTag(promotion.getPromotionTag());
		promotionDto.setStartDate(promotion.getStartDate());
		promotionDto.setEndDate(promotion.getEndDate());
		List<Record> promotionSets = Db.find("select ps.*, p.id as product_id, p.name, p.image, p.price from " + PromotionSet.table + " ps " 
				+ " left join " + Product.table + " p on ps.product_id=p.id "
				+ " where promotion_id=? ", promotion.getId());
		List<PromotionSetResultDto> setResultDtos = new ArrayList<PromotionSetResultDto>();
		for(Record record : promotionSets){
			PromotionSetResultDto psrDto = new PromotionSetResultDto();
			psrDto.setId(record.getLong("id"));
			psrDto.setProductId(record.getLong("product_id"));
			psrDto.setPromotinId(promotion.getId());
			psrDto.setProductName(record.getStr("name").length()>10 ? record.getStr("name").substring(0, 10).concat("...") : record.getStr("name"));
			psrDto.setProductImg(getImageDomain() + record.getStr("image"));
			psrDto.setProductPrice(record.getStr("price"));
			psrDto.setType(record.getInt("promotion_type"));
			psrDto.setJianjia(new BigDecimal(record.getFloat("promotion_set_jianjia")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			psrDto.setZhekou(new BigDecimal(record.getFloat("promotion_set_zhekou")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			psrDto.setPromotionValue(new BigDecimal(record.getFloat("promotion_value")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			setResultDtos.add(psrDto);
		}
		promotionDto.setPromotionSets(setResultDtos);
		return promotionDto;
	}

	@Override
	public List<Record> getPromotionServiceMini(Long appId,
			int pageNo, int pageSize) {
		if (appId == null)
			throw new ProductException("微信端获取商品列表缺少必要参数");
		pageSize = 4;
		String select = "SELECT pts.promotion_value, pts.promotion_set_zhekou, p.id, p.stock, p.name, p.image,p.price";
		String sqlExceptSelect = "FROM " + Promotion.table + " pt "
				+ " left join " + PromotionSet.table + " pts on pts.promotion_id=pt.id " 
				+ " left join " + Product.table + " p on p.id=pts.product_id " ;

		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("pt.app_id", appId).addWhere("pt.active", 1).addWhere("pt.status", 0).build();

		Page<Record> pager = Db.paginate(pageNo, pageSize,
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		for (Record product : pager.getList()) {
			product.set("image", (getImageDomain() + product.getStr("image")));
			product.set("promotion_value", (new BigDecimal(product.getStr("promotion_value")).doubleValue()));
			product.set("promotion_set_zhekou", (new BigDecimal(product.getStr("promotion_set_zhekou")).doubleValue()));
			/*Double price = new BigDecimal(product.getStr("promotion_value")).doubleValue() / 
			(new BigDecimal(product.getStr("promotion_set_zhekou")).doubleValue() / 10);
			product.set("promotion_price", (price));*/
			product.set("promotion_price", product.getStr("price"));
		}
		return pager.getList();
	}

}