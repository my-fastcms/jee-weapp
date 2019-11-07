package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.service.utils.WxmallCheck;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.math.BigDecimal;
import java.util.*;

@Bean
@RPCBean
public class ProductServiceImpl extends WxmServiceBase<Product> implements ProductService {

	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	@Inject
	private SerinumService serinumService;
	@Inject
	private DeliveryTemplateService deliveryTemplateService;
	@Inject
	private ProductSpecService productSpecService;
	@Inject
	private ShopService shopService;

	private static final ProductImage prodImgdao = new ProductImage().dao();
	private static final ProductSpec prodSpecidao = new ProductSpec().dao();
	private static final ProductSpecValue prodSpecValuedao = new ProductSpecValue().dao();
	private static final ProductSpecItem prodSpecItemdao = new ProductSpecItem().dao();
	private static final SpecificationValue specValueDao = new SpecificationValue().dao();
	private static final Specification specDao = new Specification().dao();

	@Override
	public void saveOrUpdate(ProductSubmitParamDto productSubmitParamDto) throws ProductException {
		if (productSubmitParamDto == null || productSubmitParamDto.getProductCategoryId() == null || productSubmitParamDto.getImages() == null
				|| productSubmitParamDto.getAuthUserId() == null)
			throw new ProductException("保存商品出错,请检查参数");

//		if(productSubmitParamDto.getIsCityDis() != null && productSubmitParamDto.getIsCityDis()){
//			List<Shop> shops = shopService.getShopByAppId(productSubmitParamDto.getAuthUserId());
//			if(shops == null || shops.size() <= 0){
//				throw new ProductException("请先到门店管理添加门店");
//			}
//		}
		
		if (productSubmitParamDto.getDeliveryType() == 1) {
			// 按运费模板
			DeliveryTemplate dt = deliveryTemplateService.findById(productSubmitParamDto.getDeliveryTemplateId());

			if (dt == null || dt.getActive() == 0)
				throw new ProductException("运费模板不存在或者已删除");

		}

		Product product = findById(productSubmitParamDto.getProductId());

		if (product == null) {
			product = new Product();
			product.setAppId(productSubmitParamDto.getAuthUserId());
			product.setCreated(new Date());
			product.setIsList(true);
			product.setSn(serinumService.getProductSn());
			product.setActive(true);
			product.setSales(new Long(0));
		}

		product.setName(productSubmitParamDto.getName());
		product.setImage(productSubmitParamDto.getMainImage());
		product.setIntroduction(productSubmitParamDto.getIntroduction());
		product.setProductCategoryId(productSubmitParamDto.getProductCategoryId());
		product.setUpdated(new Date());
		product.setIsMarketable(productSubmitParamDto.getIsMarketable());
		product.setIsVirtualGoods(productSubmitParamDto.getIsVirtualGoods());
		product.setIsCityDis(productSubmitParamDto.getIsCityDis());
		product.setIsPurchaseLimitation(productSubmitParamDto.getIsPurchaseLimitation());
		product.setIsPickUp(productSubmitParamDto.getIsPickUp());
		product.setIsUnifiedSpec(productSubmitParamDto.getIsUnifiedSpec());
		product.setDeliveryType(productSubmitParamDto.getDeliveryType());
		product.setDeliveryFees(productSubmitParamDto.getDeliveryFees());
		product.setDeliveryTemplateId(productSubmitParamDto.getDeliveryTemplateId());
		product.setDeliveryWeight(productSubmitParamDto.getDeliveryWeight());
		if (StrKit.notBlank(productSubmitParamDto.getMarketPrice())) {
			product.setMarketPrice(new BigDecimal(productSubmitParamDto.getMarketPrice()));
		}
		if(productSubmitParamDto.getIsPurchaseLimitation()){
			try {
				product.setPurchaseCount(Integer.valueOf(productSubmitParamDto.getPurchaseCount()));
			} catch (Exception e) {
				throw new ProductException("限购数量只能填写正整数");
			}
		}else{
			product.setPurchaseCount(null);
		}
		if (productSubmitParamDto.getIsUnifiedSpec()) {
			// 说明统一规格
			// check
			if (StrKit.isBlank(productSubmitParamDto.getPrice())) {
				throw new ProductException("统一规格下，商品价格不能为空");
			}

			if (!WxmallCheck.checkPrice(productSubmitParamDto.getPrice())) {
				throw new ProductException("统一规格下，请填写正确的价格");
			}

			if (StrKit.isBlank(productSubmitParamDto.getStock()))
				throw new ProductException("统一规格下，商品库存不能为空");

			product.setPrice(productSubmitParamDto.getPrice());
			try {
				product.setStock(Integer.valueOf(productSubmitParamDto.getStock()));
			} catch (Exception e) {
				throw new ProductException("统一规格下，商品库存只能填写正整数");
			}
		} else {
			// 说明是多规格
			product.setPrice(getSpvPrice(productSubmitParamDto));
			product.setStock(getTotalStock(productSubmitParamDto));

			if (StrKit.isBlank(product.getPrice()) || product.getStock() == null) {
				throw new ProductException("多规格下，商品价格或者总库存为空，请检查");
			}
		}

		try {
			product.saveOrUpdate();
		} catch (Exception e) {
			throw new ProductException(e.getMessage());
		}

		// 保存商品图片
		List<ProductImage> productImages = new ArrayList<ProductImage>();
		for (int i = 0; i < productSubmitParamDto.getImages().length; i++) {
			ProductImage productImage = new ProductImage();
			productImage.setProductId(product.getId());
			productImage.setLarge(productSubmitParamDto.getImages()[i]);
			productImage.setOrders(i + 1);
			productImage.setActive(1);
			productImage.setCreated(new Date());
			productImage.setUpdated(new Date());
			productImages.add(productImage);
		}

		try {
			Db.deleteById(ProductImage.table, "product_id", productSubmitParamDto.getProductId());
			Db.batchSave(productImages, productImages.size());
		} catch (Exception e) {
			throw new ProductException(e.getMessage());
		}

		if (!productSubmitParamDto.getIsUnifiedSpec()) {
			// 保存选择的规格跟规格值
			if (productSubmitParamDto.getSpecificationIds() != null
					&& productSubmitParamDto.getSpecificationIds().length > 0
					&& productSubmitParamDto.getSpecificationValues() != null
					&& productSubmitParamDto.getSpecificationValues().length > 0) {
				List<ProductSpec> specifications = new ArrayList<ProductSpec>();
				List<ProductSpecValue> specificationValues = new ArrayList<ProductSpecValue>();
				for (int i = 0; i < productSubmitParamDto.getSpecificationIds().length; i++) {
					ProductSpec ps = new ProductSpec();
					ps.setProductId(product.getId());
					ps.setSpecificationId(productSubmitParamDto.getSpecificationIds()[i]);
					ps.setActive(1);
					ps.setCreated(new Date());
					ps.setUpdated(new Date());
					specifications.add(ps);
				}
				try {
					Db.deleteById(ProductSpec.table, "product_id", productSubmitParamDto.getProductId());
					Db.batchSave(specifications, specifications.size());
				} catch (Exception e) {
					throw new ProductException(e.getMessage());
				}
				for (int i = 0; i < productSubmitParamDto.getSpecificationValues().length; i++) {
					ProductSpecValue psValue = new ProductSpecValue();
					String[] psname = productSubmitParamDto.getSpecificationValues()[i].split("_");
					if(psname != null && psname.length == 2){
						psValue.setSpecificationId(new Long(psname[1]));
						psValue.setSpecificationValueId(new Long(psname[0]));
						psValue.setProductId(product.getId());
						psValue.setActive(1);
						psValue.setCreated(new Date());
						psValue.setUpdated(new Date());
						specificationValues.add(psValue);						
					}
				}

				try {
					Db.deleteById(ProductSpecValue.table, "product_id", productSubmitParamDto.getProductId());
					Db.batchSave(specificationValues, specificationValues.size());
				} catch (Exception e) {
					throw new ProductException(e.getMessage());
				}
			}

			// 选择规格的价格与库存
			if (StrKit.notBlank(productSubmitParamDto.getPriceAndStock())) {
				List<ProductSpecItem> stocks = new ArrayList<ProductSpecItem>();
				String stock = productSubmitParamDto.getPriceAndStock();
				JSONArray jarr = JSONArray.parseArray(stock);
				for (Iterator<?> iterator = jarr.iterator(); iterator.hasNext();) {
					JSONObject job = (JSONObject) iterator.next();
					ProductSpecItem stockss = new ProductSpecItem();
					stockss.setProductId(product.getId());
					stockss.setSpecificationValue(job.get("specificationValue").toString());
					BigDecimal bprice = new BigDecimal(job.get("price").toString().trim());
					stockss.setPrice(bprice);
					stockss.setStock(Integer.parseInt(job.get("stock").toString().trim()));
					if (!StrKit.isBlank(job.get("weight").toString())) {
						BigDecimal bweight = new BigDecimal(job.get("weight").toString().trim());
						stockss.setWeight(bweight);
					}

					stockss.setActive(1);
					stockss.setCreated(new Date());
					stockss.setUpdated(new Date());
					stocks.add(stockss);
				}
				try {
					Db.deleteById(ProductSpecItem.table, "product_id", productSubmitParamDto.getProductId());
					Db.batchSave(stocks, stocks.size());
				} catch (Exception e) {
					throw new ProductException(e.getMessage());
				}
			}
		}

	}

	@Override
	public Page<ProductResultDto> list(ProductParamDto productParamDto) throws ProductException {
		if (productParamDto == null 
				|| productParamDto.getIsMarketable() == null || productParamDto.getAuthUserId() == null)
			throw new ProductException("管理端获取商品列表缺少必要参数");

		QueryHelper helper = new QueryHelper("select * ", "from " + Product.table);
		helper.addWhere("app_id", productParamDto.getAuthUserId())
				.addWhere("active", 1).addWhere("product_category_id", productParamDto.getCategoryId())
				.addWhereLike("name", productParamDto.getName())
				.addWhere("is_marketable", productParamDto.getIsMarketable())
				.addWhereNotIn("id", productParamDto.getProductIds()).addOrderBy("desc", "updated").build();

		Page<Product> productPage = DAO.paginate(productParamDto.getPageNo(), productParamDto.getPageSize(),
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());

		List<Product> products = productPage.getList();

		if ("1".equals(productParamDto.getSaleOver())) {
			List<Product> _prods = new ArrayList<Product>();
			// 查询已售罄的商品
			for (Product product : productPage.getList()) {
				if (product.getSales().intValue() == product.getStock().intValue()) {
					_prods.add(product);
				}
			}
			products = _prods;
		}

		boolean flag = false;
		if(productParamDto.getNeedImageDomain() != null)	{
			flag = productParamDto.getNeedImageDomain();
		}
		
		List<ProductResultDto> productResults = new ArrayList<ProductResultDto>();
		for (Product product : products) {
			ProductResultDto productResultDto = new ProductResultDto();
			productResultDto.setId(product.getId());
			productResultDto.setName(product.getName());
			if(flag){
				productResultDto.setImg(product.getImage());
			}else{
				productResultDto.setImg(getImageDomain() + product.getImage());
			}
			productResultDto.setPrice(product.getPrice());
			productResultDto.setStock(product.getStock());
			productResultDto.setStartDate(product.getCreated());
			productResultDto.setSales(product.getSales());
			productResults.add(productResultDto);
		}
		return new Page<ProductResultDto>(productResults, productParamDto.getPageNo(), productParamDto.getPageSize(),
				productPage.getTotalPage(), productPage.getTotalRow());
	}

	@Override
	public List<ProductResultDto> getMarketableByIds(Long appId, String ids) throws ProductException {

		String sql = "select * from " + Product.table
				+ " where   active=1 and is_marketable = 1 and app_id = ? and id in(" + ids + ")";
		List<Product> list = DAO.find(sql, appId);

		List<ProductResultDto> productResults = new ArrayList<ProductResultDto>();
		for (Product product : list) {
			ProductResultDto productResultDto = new ProductResultDto();
			productResultDto.setId(product.getId());
			productResultDto.setName(product.getName());
			productResultDto.setImg(getImageDomain() + product.getImage());
			productResultDto.setPrice(product.getPrice());
			productResultDto.setStock(product.getStock());
			productResultDto.setStartDate(product.getCreated());
			productResultDto.setSales(product.getSales());

			productResults.add(productResultDto);
		}

		return productResults;
	}

	@Override
	public ProductDetailResultDto getMobieDetail(Long productId) throws UmpException{
		if (productId == null)
			throw new UmpException("获取商品详情缺少必要参数productId is null");

		Product product = DAO.findById(productId);
		if (product == null)
			throw new UmpException("商品不存在");
		if (product.getIsMarketable() == false)
			throw new UmpException("商品已下架");
		if (product.getActive() == false)
			throw new UmpException("商品已删除");

		ProductDetailResultDto productDetail = new ProductDetailResultDto();
		productDetail.setProduct(product);

		List<String> imgList = new ArrayList<String>();
		List<ProductImage> images = prodImgdao.find("select * from " + ProductImage.table + " where product_id = ?", product.getId());
		for (ProductImage productImage : images) {
			imgList.add(getImageDomain() + productImage.getLarge());
		}
		productDetail.setImageList(imgList);
		productDetail.setSpecifications(productSpecService.getProductSpecSkus(product));
		
		//ProdPromotionResultDto productPromotionset = promotionService.getProductPromotion(product);
		// 限时打折
//		productDetail.setPromotionInfo(promotionService.getProductPromotion(product));
//		// 满减
//		productDetail.setFullCutInfo(fullCutService.getProductFullCut(product));
//		// 订单返现
//		productDetail.setCashback(cashbackService.getProductCashBack(product));
//		// 拼团活动信息
//		productDetail.setGroupInfo(multiGroupService.getProductGroup(product));
//		// 拼团正在进行中的组团列表
//		productDetail.setGroupings(grouponService.getGroupsByProduct(product));
		
		// sku组合 sku作为key 价格，库存作为值
		productDetail.setPriceMap(getProductSpecPrice(productId));
		return productDetail;
	}

	@Override
	public ProductAllResultDto findAllResultDto(ProductParamDto productParamDto) {
		if (productParamDto.getProductId() == null)
			return null;

		Product product = DAO.findById(productParamDto.getProductId());
		if (product == null)
			return null;

		ProductAllResultDto results = new ProductAllResultDto();
		List<ProductImage> productImages = prodImgdao
				.find("SELECT * FROM " + ProductImage.table + " WHERE product_id= ? and active=1 ", product.getId());
		List<ImagepathResultDto> imageList = new ArrayList<ImagepathResultDto>();
		for (ProductImage image : productImages) {
			ImagepathResultDto imgDto = new ImagepathResultDto();
			imgDto.setImgPath(getImageDomain() + image.getLarge());
			imgDto.setImgUrl(image.getLarge());
			imageList.add(imgDto);
		}
		results.setImageList(imageList);

		List<Specification> specifications = new ArrayList<Specification>();
		List<ProductSpec> prodSpecList = prodSpecidao
				.find("SELECT * FROM " + ProductSpec.table + " WHERE product_id = ? and active=1 ", product.getId());
		for (ProductSpec prodSpec : prodSpecList) {
			Specification spec = specDao.findById(prodSpec.getSpecificationId());
			if (spec != null && spec.getActive() == 1)
				specifications.add(spec);
		}
		results.setSpecifications(SpecificationUtil.getSpecDtoList(specifications));

		List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();
		List<ProductSpecValue> prodSpecValueList = prodSpecValuedao.find(
				"SELECT * FROM " + ProductSpecValue.table + " WHERE product_id = ? and active=1 ", product.getId());
		for (ProductSpecValue psv : prodSpecValueList) {
			SpecificationValue specValue = specValueDao.findById(psv.getSpecificationValueId());
			if (specValue != null && specValue.getActive() == 1)
				specificationValues.add(specValue);
		}
		results.setSpecificationValues(SpecificationUtil.getSpecValueDtoList(specificationValues));

		List<ProductSpecItem> stocks = prodSpecItemdao
				.find("SELECT * FROM " + ProductSpecItem.table + " WHERE `product_id`= ?", product.getId());
		results.setStocks(stocks);
		return results;
	}

	@Override
	public List<ProductMobileResultDto> findProducts4Mobile(ProductMobileParamDto mobileParamDto)
			throws ProductException {
		if (mobileParamDto == null || mobileParamDto.getAuthUserId() == null)
			throw new ProductException("微信端获取商品列表缺少必要参数");

		//查询出限时打折商品id
		String listSql =  "SELECT pts.product_id FROM " + Promotion.table + " pt "
				+ " left join " + PromotionSet.table + " pts on pts.promotion_id=pt.id where pt.app_id = ? "
				+ "and pt.active = ? and pt.status = ?";

		List<Record> list = Db.find(listSql, mobileParamDto.getAuthUserId() ,1 ,0);
		List<String> ids =  new ArrayList<String>();
		for (Record re: list) {
			ids.add(re.getStr("product_id"));
		}
		
		//查询出分销商品id
//		listSql = "select product_id from " + AgentProduct.table + " where app_id = ? and active = ? GROUP BY product_id ";
//		list = Db.find(listSql, mobileParamDto.getAuthUserId() ,1);
//		for (Record re: list) {
//			ids.add(re.getStr("product_id"));
//		}
		
		String select = "SELECT * ";
		String sqlExceptSelect = "FROM " + Product.table;
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("app_id", mobileParamDto.getAuthUserId())
				.addWhere("active", 1).addWhere("is_marketable", 1)
				.addWhere("product_type", 1)
				.addWhere("product_category_id", mobileParamDto.getCategId())
				.addWhereLike("name", mobileParamDto.getKeyword())
				.addWhereNotIn("id", ids)
				.addOrderBy(mobileParamDto.getOrderType(), mobileParamDto.getOrderBy()).build();

		Page<Product> pager = DAO.paginate(mobileParamDto.getPageNo(), mobileParamDto.getPageSize(),
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());

		List<ProductMobileResultDto> productVos = new ArrayList<ProductMobileResultDto>();
		for (Product product : pager.getList()) {
			ProductMobileResultDto pv = new ProductMobileResultDto();
			pv.setId(product.getLong("id").toString());
			if (StrKit.notBlank(product.getStr("name"))) {
				pv.setName(product.getStr("name"));
			}
			pv.setMainPic(getImageDomain() + product.getImage());
			pv.setStore(product.getStock() == null ? "0" : product.getStock().toString());
			pv.setPrice(product.getPrice());
			if (product.getMarketPrice() != null)
				pv.setMarketPrice(product.getMarketPrice().toString());
			productVos.add(pv);
		}

		return productVos;
	}

	/**
	 * 获取规格价格区间
	 * 
	 * @param productSubmitParamDto
	 * @return
	 */
	private String getSpvPrice(ProductSubmitParamDto productSubmitParamDto) {
		String stockAndPrice = productSubmitParamDto.getPriceAndStock();
		JSONArray jarr = JSONArray.parseArray(stockAndPrice);
		BigDecimal min, max;
		min = max = new BigDecimal(jarr.getJSONObject(0).get("price").toString());
		for (int i = 0; i < jarr.size(); i++) {
			JSONObject json = jarr.getJSONObject(i);
			BigDecimal bprice = new BigDecimal(json.get("price").toString());
			if (bprice.compareTo(max) == 1) {
				max = bprice;
			}
			if (bprice.compareTo(min) == -1) {
				min = bprice;
			}
		}
		if (max.compareTo(min) == 0) {
			return max.toString();
		} else {
			return min + " ~ " + max;
		}
	}

	private Integer getTotalStock(ProductSubmitParamDto productSubmitParamDto) {
		int stockTotal = 0;
		String stock = productSubmitParamDto.getPriceAndStock();
		JSONArray jarr = JSONArray.parseArray(stock);
		for (int i = 0; i < jarr.size(); i++) {
			JSONObject json = jarr.getJSONObject(i);
			int stocknum = Integer.parseInt(json.get("stock").toString());
			stockTotal = stockTotal + stocknum;
		}
		return stockTotal;
	}

	@Override
	public HashMap<String, ProductSpecPriceResultDto> getProductSpecPrice(Long productId) throws ProductException {
		if (productId == null)
			throw new ProductException("获取商品规格价格参数错误");
		Product product = DAO.findById(productId);
		if (product == null)
			throw new ProductException("商品不存在");

		HashMap<String, ProductSpecPriceResultDto> result = new HashMap<String, ProductSpecPriceResultDto>();
		
		//单规格商品时，查询是否属于分销商品，设置用户等级的分销价格
//		if(product.getIsUnifiedSpec()){
//			List<AgentProduct> list = agentProductService.findBySpeAndProductId(productId,null);
//			if(list != null && list.size() > 0){
//				//匹配该用户的等级分销价格
//				ProductSpecPriceResultDto uniDto = new ProductSpecPriceResultDto();
//				for (AgentProduct agentProduct : list) {
//					if(agentProduct.getAgentPrice() != null){
//						uniDto.setAgentPrice(agentProduct.getAgentPrice().toString());
//						result.put("agentPrice",uniDto);
//					}
//				}
//			}
//			return result;
//		}
		
//		BigDecimal min = null;
//		BigDecimal max = null;
		
		List<ProductSpecItem> specItems = prodSpecItemdao
				.find("select * FROM " + ProductSpecItem.table + " WHERE product_id = ? ", productId);
		for (ProductSpecItem specItem : specItems) {
			if (StrKit.isBlank(specItem.getSpecificationValue()))
				continue;

			ProductSpecPriceResultDto priceDto = new ProductSpecPriceResultDto();
			priceDto.setPrice(specItem.getPrice().toString());
			priceDto.setStock(specItem.getStock().toString());
			if (specItem.getWeight() != null) {
				priceDto.setWeight(specItem.getWeight().toString());
			}

			// 检查对于商品是否有促销，限时打折
//			String promoPrice = promotionService.getProductPromotionPrice(product, specItem);
//			if (StrKit.notBlank(promoPrice))
//				priceDto.setPromPrice(promoPrice.toString());
//			// 商品是否有拼团价
//			BigDecimal collagePrice = multiGroupService.getCollagePrice(product, specItem.getSpecificationValue());
//			if (collagePrice != null)
//				priceDto.setCollagePrice(collagePrice.toString());
//			//商品是否有分销价
//			List<AgentProduct> list = agentProductService.findBySpeAndProductId(productId,specItem.getSpecificationValue());
//			if(list != null && list.size() > 0){
//				//匹配该用户的等级分销价格
//				for (AgentProduct agentProduct : list) {
//					if(min == null && max == null){
//						min = agentProduct.getAgentPrice();
//						max = agentProduct.getAgentPrice();
//					}else if(min.compareTo(agentProduct.getAgentPrice()) == 1){
//						min = agentProduct.getAgentPrice();
//					}else if(max.compareTo(agentProduct.getAgentPrice()) == -1){
//						max = agentProduct.getAgentPrice();
//					}
//					priceDto.setAgentPrice(agentProduct.getAgentPrice().toString());
//				}
//			}
			
			result.put(specItem.getSpecificationValue(), priceDto);
		}
//		if(min != null && max != null){
//			ProductSpecPriceResultDto agentPrice = new ProductSpecPriceResultDto();
//			agentPrice.setAgentPrice(min+" ~ "+max);
//			result.put("agentPrice", agentPrice);
//		}
		return result;
	}

	@Override
	public List<ProductResultDto> getProducts(String productIds) throws ProductException {
		if (StrKit.isBlank(productIds))
			throw new ProductException("调用批量获取商品详情接口参数错误");

		List<ProductResultDto> resultDtos = new ArrayList<ProductResultDto>();
		final String[] productIdArrs = productIds.split("-");
		List<Object> params = new ArrayList<Object>();
		final StringBuffer condition = new StringBuffer();
		for (String id : productIdArrs) {
			params.add(Long.valueOf(id));
			condition.append("?").append(",");
		}
		condition.deleteCharAt(condition.length() - 1);

		final StringBuffer where = new StringBuffer(" where 1=1 ");
		where.append(" and id in (" + condition + ")");
		final String select = "select * ";
		final String sqlExceptSelect = select + " from " + Product.table + " " + where.toString();
		List<Product> products = DAO.find(sqlExceptSelect, params.toArray());
		for (Product product : products) {
			ProductResultDto resultDto = new ProductResultDto();
			resultDto.setId(product.getId());
			resultDto.setImg(getImageDomain() + product.getImage());
			resultDto.setName(product.getName());
			resultDto.setPrice(product.getPrice());
			resultDto.setStock(product.getStock());
			resultDtos.add(resultDto);
		}
		return resultDtos;
	}

	@Override
	public Page<ProductMobileResultDto> getMobilePromotionProduct(ProductMobileParamDto mobileParamDto)
			throws ProductException {
		if (mobileParamDto == null || mobileParamDto.getAuthUserId() == null)
			throw new ProductException("获取打折商品缺少必要参数");
		List<ProductMobileResultDto> resultDtos = new ArrayList<ProductMobileResultDto>();

		// 查询出有效打折数据
		Date currDate = new Date();
		final String select = "select * ";
		final String sqlExceptSelect = " from " + Product.table
				+ " where app_id=? and is_marketable=1 and active=1 and id in"
				+ " (select product_id from " + PromotionSet.table + " where promotion_id " + "in (select id from "
				+ Promotion.table + " where app_id=? and start_date<=? and end_date >=? ))";
		Page<Product> pages = DAO.paginate(mobileParamDto.getPageNo(), mobileParamDto.getPageSize(), select,
				sqlExceptSelect, mobileParamDto.getAuthUserId(), mobileParamDto.getAuthUserId(),
				currDate, currDate);
		for (Product product : pages.getList()) {
			ProductMobileResultDto resultDto = new ProductMobileResultDto();
			resultDto.setId(product.getId().toString());
			resultDto.setMainPic(getImageDomain() + product.getImage());
			if (StrKit.notBlank(product.getStr("name"))) {
				resultDto.setName(product.getStr("name").length() > 10
						? product.getStr("name").substring(0, 10).concat("...") : product.getStr("name"));
			}
			resultDto.setPrice(product.getPrice());
			resultDto.setStore(product.getStock().toString());
			resultDtos.add(resultDto);
		}

		return new Page<ProductMobileResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(),
				pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public List<ProductMobileResultDto> getHotProduct(ProductMobileParamDto mobileParamDto) throws ProductException {
		String sql = "select * from " + Product.table
				+ " where app_id=? and is_marketable=1 and product_type=1 and active=1 order by sales desc LIMIT 3 ";
		List<Product> products = DAO.find(sql, mobileParamDto.getAuthUserId());
		List<ProductMobileResultDto> resultDtos = new ArrayList<ProductMobileResultDto>();
		for (Product product : products) {
			ProductMobileResultDto mobileResultDto = new ProductMobileResultDto();
			resultDtos.add(mobileResultDto);
			mobileResultDto.setId(product.getId().toString());
			mobileResultDto.setMainPic(getImageDomain() + product.getImage());
			if (StrKit.notBlank(product.getName())) {
				mobileResultDto.setName(product.getName().length() > 10
						? product.getName().substring(0, 10).concat("...") : product.getName());
			}
			mobileResultDto.setPrice(product.getPrice());
		}
		return resultDtos;
	}

	@Override
	public List<ProductMobileResultDto> getNewProduct(ProductMobileParamDto mobileParamDto) throws ProductException {
		String sql = "select * from " + Product.table
				+ " where app_id=? and is_marketable=1 and product_type=1 and active=1 order by created desc LIMIT 3 ";
		List<Product> products = DAO.find(sql, mobileParamDto.getAuthUserId());
		List<ProductMobileResultDto> resultDtos = new ArrayList<ProductMobileResultDto>();
		for (Product product : products) {
			ProductMobileResultDto mobileResultDto = new ProductMobileResultDto();
			resultDtos.add(mobileResultDto);
			mobileResultDto.setId(product.getId().toString());
			mobileResultDto.setMainPic(getImageDomain() + product.getImage());
			if (StrKit.notBlank(product.getName())) {
				mobileResultDto.setName(product.getName().length() > 10
						? product.getName().substring(0, 10).concat("...") : product.getName());
			}
			mobileResultDto.setPrice(product.getPrice());
		}
		return resultDtos;
	}

	@Override
	public List<ProductMobileResultDto> getRecommendProduct(ProductMobileParamDto mobileParamDto)
			throws ProductException {
		String sql = "select * from " + Product.table
				+ " where app_id= ? and is_marketable=1 and product_type=1 and active=1 order by price desc LIMIT 3 ";// 暂时按价格排序
		List<Product> products = DAO.find(sql, mobileParamDto.getAuthUserId());
		List<ProductMobileResultDto> resultDtos = new ArrayList<ProductMobileResultDto>();
		for (Product product : products) {
			ProductMobileResultDto mobileResultDto = new ProductMobileResultDto();
			resultDtos.add(mobileResultDto);
			mobileResultDto.setId(product.getId().toString());
			mobileResultDto.setMainPic(getImageDomain() + product.getImage());
			if (StrKit.notBlank(product.getName())) {
				mobileResultDto.setName(product.getName().length() > 10
						? product.getName().substring(0, 10).concat("...") : product.getName());
			}
			mobileResultDto.setPrice(product.getPrice());
		}
		return resultDtos;
	}

	@Override
	public List<ProductMobileResultDto> getIndexProduct(ProductMobileParamDto mobileParamDto) throws ProductException {
		String sql = "select * from " + Product.table
				+ " where app_id=? and is_marketable=1 and product_type=1 and active=1 order by price desc LIMIT 30 ";// 暂时按价格排序
																													// 取30条数据
		List<Product> products = DAO.find(sql, mobileParamDto.getAuthUserId());
		List<ProductMobileResultDto> resultDtos = new ArrayList<ProductMobileResultDto>();
		for (Product product : products) {
			ProductMobileResultDto mobileResultDto = new ProductMobileResultDto();
			resultDtos.add(mobileResultDto);
			mobileResultDto.setId(product.getId().toString());
			mobileResultDto.setMainPic(getImageDomain() + product.getImage());
			if (StrKit.notBlank(product.getName())) {
				mobileResultDto.setName(product.getName().length() > 10
						? product.getName().substring(0, 10).concat("...") : product.getName());
			}
			mobileResultDto.setPrice(product.getPrice());
		}
		return resultDtos;
	}

	@Override
	public Page<ProductMobileResultDto> getMobileGroupProduct(ProductMobileParamDto mobileParamDto) throws ProductException {
		if (mobileParamDto == null)
			throw new ProductException("获取打折商品缺少必要参数");
//		List<ProductMobileResultDto> resultDtos = new ArrayList<ProductMobileResultDto>();
//		Date currDate = new Date();
//		final String select = "select * ";
//		final String sqlExceptSelect = " from " + Product.table
//				+ " where app_id=? and is_marketable=1 and active=1 and id in"
//				+ " (select product_id from " + MultiGroupSet.table + " where multi_group_id " + "in (select id from "
//				+ MultiGroup.table + " where app_id=? and start_date<=? and end_date >=? ))";
//		Page<Product> pages = productDao.paginate(mobileParamDto.getPageNo(), mobileParamDto.getPageSize(), select,
//				sqlExceptSelect, mobileParamDto.getAuthUserId(),
//				mobileParamDto.getAuthUserId(), currDate, currDate);
//		for (Product product : pages.getList()) {
//			ProductMobileResultDto resultDto = new ProductMobileResultDto();
//			resultDto.setId(product.getId().toString());
//			resultDto.setMainPic(getImageDomain() + product.getImage());
//			if (StrKit.notBlank(product.getStr("name"))) {
//				resultDto.setName(product.getStr("name").length() > 10
//						? product.getStr("name").substring(0, 10).concat("...") : product.getStr("name"));
//			}
//			resultDto.setPrice(product.getPrice());
//			resultDto.setStore(product.getStock().toString());
//			resultDtos.add(resultDto);
//		}
//
//		return new Page<ProductMobileResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(),
//				pages.getTotalPage(), pages.getTotalRow());
		return null;

	}

	@Override
	public Long getProductCountByCategroyId(Long categroyId) {
		return Db.queryLong("select count(*) from " + Product.table + " where product_category_id=? ", categroyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dbumama.market.service.api.ProductService#getWeappPorudctCount(java.
	 * lang.Long)
	 */
	@Override
	public Long getWeappPorudctCount(Long authUserId) {
		return Db.queryLong("select count(id) from " + Product.table + " where app_id=? and is_marketable=1", authUserId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dbumama.market.service.api.ProductService#importProduct(com.dbumama.
	 * market.model.Product, java.util.ArrayList)
	 */
	@Override
	@Before(Tx.class)
	public void importProduct(Product product, ArrayList<ProductImage> productImages) throws ProductException {
		if (product == null)
			throw new ProductException("product is null");

		try {
			saveOrUpdate(product);
		} catch (Exception e) {
			throw new ProductException("save product error");
		} 

		for (ProductImage image : productImages) {
			image.setProductId(product.getId());
		}

		Db.batchSave(productImages, productImages.size());
	}

}