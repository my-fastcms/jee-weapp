package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.ActivityStatus;
import com.dbumama.market.service.enmu.OrderType;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.utils.DateTimeUtil;
import com.dbumama.weixin.api.CompCustomApi;
import com.dbumama.weixin.api.CompCustomApi.Articles;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
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
import java.util.*;

@Bean
@RPCBean
public class MultiGroupServiceImpl extends WxmServiceBase<MultiGroup> implements MultiGroupService {

	private static final BuyerUser buyerUserdao = new BuyerUser().dao();
	private static final MultiGroup multiGroupdao = new MultiGroup().dao();
	private static final MultiGroupSet multiGroupSetdao = new MultiGroupSet().dao();
	private static final OrderGroup orderGroupdao = new OrderGroup().dao();
	private static final Product productDao = new Product().dao();
	private static final ProductSpec prodSpecidao = new ProductSpec().dao();
	private static final ProductSpecValue prodSpecValuedao = new ProductSpecValue().dao();
	private static final ProductSpecItem prodSpecItemdao = new ProductSpecItem().dao();
	private static final SpecificationValue specValueDao = new SpecificationValue().dao();
	private static final Specification specDao = new Specification().dao();
	
	private static final Object lockobj = new Object();  
	
	@Inject
	ProductService productService;
	@Inject
	AuthUserService authUserService;
	@Inject
	SpecificationValueService specificationValueService;
	@Inject
	OrderGroupService orderGroupService;
	@Inject
	OrderService orderService;
	@Inject
	OrderItemService orderItemService;
	@Inject
	RefundErrorService refundErrorService;
	
	@Override
	@Before(Tx.class)
	public void save(MultiGroup multiGroup, String multiGroupSetItems) throws UmpException {
		if(multiGroup == null || StrKit.isBlank(multiGroupSetItems) || multiGroup.getAppId() == null)
			throw new UmpException("保存多人拼团活动缺少必要参数");
		if(multiGroup.getStartDate() == null || multiGroup.getEndDate() == null 
				|| (multiGroup.getStartDate().after(multiGroup.getEndDate()))){
			throw new UmpException("请确认活动开始时间跟有效时间");
		}
		
		if(multiGroup.getStartDate().before(new Date()) && multiGroup.getEndDate().after(new Date())){
			multiGroup.setStatus(ActivityStatus.activing.ordinal());//进行中
		}else if(multiGroup.getStartDate().after(new Date())){
			multiGroup.setStatus(ActivityStatus.unactive.ordinal());
		}else if(multiGroup.getEndDate().before(new Date())){
			multiGroup.setStatus(ActivityStatus.activeend.ordinal());
		}
		
		if(multiGroup.getGroupCondition() == null) throw new UmpException("请选择参团条件");
		if(multiGroup.getNeedFollows() == null) throw new UmpException("请选择是否需要关注");
		if(multiGroup.getOfferNum() == null || multiGroup.getOfferNum().intValue() < 2 ) throw new UmpException("拼团人数不能小于2");
		if(multiGroup.getNeedFollows()){
			if(StrKit.isBlank(multiGroup.getFollowsTitle())) throw new UmpException("请输入关注回复标题");
			if(StrKit.isBlank(multiGroup.getFollowsImage())) throw new UmpException("请上传关注回复封面");
			if(StrKit.isBlank(multiGroup.getFollowsIntro())) throw new UmpException("请输入关注回复简介");
		}
		if(multiGroup.getMultiType() == null) throw new UmpException("请选择活动类型");
		if(StrKit.isBlank(multiGroup.getActivityImage())) throw new UmpException("请上传活动展示图");
		if(StrKit.isBlank(multiGroup.getShareImage())) throw new UmpException("请上传分享展示图");
		if(StrKit.isBlank(multiGroup.getShareIntro())) throw new UmpException("请输入分享简介");
		if(StrKit.isBlank(multiGroup.getShareTitle())) throw new UmpException("请输入分享标题");
		if(StrKit.isBlank(multiGroup.getDetailIntro())) throw new UmpException("请输入商品详情页分享简介");
		if(StrKit.isBlank(multiGroup.getDetailTitle())) throw new UmpException("请输入商品详情页分享标题");
		
		if(multiGroup.getId() != null){
			MultiGroup mGroup = multiGroupdao.findById(multiGroup.getId());
			if(mGroup == null) throw new UmpException("拼团活动参数出错");
			multiGroup.setAppId(mGroup.getAppId()).setGroupCondition(mGroup.getGroupCondition()).setMultiType(mGroup.getMultiType());
		}
		
		//设置需要强制关注的信息
//		if(multiGroup.getNeedFollows()){
//			String scenceId = QrcodeScenceIdKit.QRCODE_MULTIGROUP_PREFIX;
//			synchronized (lockobj) {
//				String scenceIdStr = String.valueOf(System.currentTimeMillis());
//				scenceId = scenceId + scenceIdStr.substring(0, 10);
//			}
//			//获取系统时间
//		    String dateString = DateTimeUtil.toDateString(new Date());
//		    Date startTime = null;
//		    Date endTime = null;
//			try {
//				startTime = DateTimeUtil.FORMAT_YYYY_MM_DD.parse(dateString);
//				endTime = DateTimeUtil.FORMAT_YYYY_MM_DD.parse(DateTimeUtil.toDateString(multiGroup.getEndDate()));
//			} catch (ParseException e1) {
//				throw new WxmallBaseException(e1.getMessage());
//			}
//		    //计算出毫秒
//		    long diff  = endTime.getTime() - startTime.getTime();
//		    //转换为秒
//			int expireSeconds = (int)(diff/1000);
//
//			AuthUser authUser = authUserService.findById(multiGroup.getAppId());
//			if(authUser == null) throw new UmpException("authUser is null");
//			//调用临时二维码的接口
//			ApiResult qrcodeRes = CompQrcodeApi.createTemporaryStr(expireSeconds, scenceId,authUserService.getAccessToken(authUser));
//			if(!qrcodeRes.isSucceed()){
//				throw new WxmallBaseException("获取临时二维码失败");
//			}
//			//获取返回数据
//			String ticket = qrcodeRes.getStr("ticket");
//			String url = CompQrcodeApi.getShowQrcodeUrl(ticket);
//			try{
//				multiGroup.setFollowsUrl(url).setFollowsTicket(ticket);
//			}catch (Exception e){
//				throw new UmpException("保存二维码失败");
//			}
//		}else{
//			multiGroup.setFollowsImage(null).setFollowsIntro(null).setFollowsTicket(null).setFollowsTitle(null).setFollowsUrl(null);
//		}
		
		try {
			saveOrUpdate(multiGroup);			
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
		
		JSONArray jarr = null;
		try {
			jarr = JSONArray.parseArray(multiGroupSetItems);
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
		if(jarr == null || jarr.size() <=0) throw new UmpException("未设置活动拼团信息");
		
		JSONArray newArr = new JSONArray();
		for(Iterator<?> iterator = jarr.iterator(); iterator.hasNext();){
			JSONObject job = (JSONObject) iterator.next(); 
			 if(!"del".equals(job.getString("opt"))){
				newArr.add(job);
			 }
		}
		if(newArr.size()<=0) throw new UmpException("没有设置拼团商品");
		
		List<MultiGroupSet> multiGroupAddSet=new ArrayList<MultiGroupSet>();
		List<MultiGroupSet> multiGroupUpdateSet=new ArrayList<MultiGroupSet>();
		List<MultiGroupSet> multiGroupDelSet=new ArrayList<MultiGroupSet>();
		for(int i=0;i<jarr.size();i++){
			JSONObject jsonObj = jarr.getJSONObject(i);
			Long multiGroupSetId = jsonObj.getLong("msetId");
			MultiGroupSet multiGroupSet = null;
			if(multiGroupSetId != null){//修改拼团设置项
				multiGroupSet=multiGroupSetdao.findById(multiGroupSetId);
				if(multiGroupSet == null) continue;
				if("del".equals(jsonObj.getString("opt"))){
					multiGroupDelSet.add(multiGroupSet);
				}else{
					try {
						if(jsonObj == null 
								|| jsonObj.getLong("productId") == null 
								|| jsonObj.getBigDecimal("prime_cost") == null 
								|| jsonObj.getBigDecimal("collage_price") == null)
							throw new UmpException("保存拼团活动，拼团价设置项值有误，请检查拼团价格");				
					} catch (Exception e) {
						throw new UmpException("保存拼团活动失败，拼团价设置项值有误，未知异常");
					}
					if(jsonObj.getBigDecimal("collage_price").compareTo(jsonObj.getBigDecimal("prime_cost"))==1){
						throw new UmpException("拼团价格大于原价请检查拼团价格");
					}
					multiGroupSet.setPrimeCost(jsonObj.getBigDecimal("prime_cost"));
					multiGroupSet.setCollagePrice(jsonObj.getBigDecimal("collage_price"));
					multiGroupSet.setUpdated(new Date());
					multiGroupUpdateSet.add(multiGroupSet);
				}
			}else{
				if("del".equals(jsonObj.getString("opt"))) continue;//排除删除项
				try {
					if(jsonObj == null 
							|| jsonObj.getLong("productId") == null 
							|| jsonObj.getBigDecimal("prime_cost") == null 
							|| jsonObj.getBigDecimal("collage_price") == null)
						throw new UmpException("保存拼团活动，拼团价设置项值有误，请检查拼团价格");				
				} catch (Exception e) {
					throw new UmpException("保存拼团活动失败，拼团价设置项值有误，未知异常");
				}
				if(jsonObj.getBigDecimal("collage_price").compareTo(jsonObj.getBigDecimal("prime_cost"))==1){
					throw new UmpException("拼团价格大于原价请检查拼团价格");
				}
				//新增拼团设置项
				multiGroupSet=new MultiGroupSet();
				multiGroupSet.setProductId(jsonObj.getLong("productId"));
				multiGroupSet.setMultiGroupId(multiGroup.getId());
				multiGroupSet.setSpecificationValue(jsonObj.getString("specificationValue"));
				multiGroupSet.setPrimeCost(jsonObj.getBigDecimal("prime_cost"));
				multiGroupSet.setCollagePrice(jsonObj.getBigDecimal("collage_price"));
				multiGroupSet.setCreated(new Date());
				multiGroupSet.setActive(1);
				multiGroupSet.setUpdated(new Date());
				multiGroupAddSet.add(multiGroupSet);
			}
		}
		if(multiGroupAddSet.size()<=0 && multiGroupDelSet.size() <=0 && multiGroupUpdateSet.size()<=0)
			throw new UmpException("没有设置拼团项数据");
		try {
			if(multiGroupAddSet.size() > 0){
				Db.batchSave(multiGroupAddSet, multiGroupAddSet.size());	
			}
			if(multiGroupUpdateSet.size() > 0){
				Db.batchUpdate(multiGroupUpdateSet, multiGroupUpdateSet.size());	
			}
			if(multiGroupDelSet.size()>0){
				for(MultiGroupSet mg : multiGroupDelSet){
					if(mg != null) mg.delete();
				}
			}
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
	}
	
	@Override
	public GrouponResultDto getGroupInfo(Long grouponId) throws UmpException {
		if(grouponId == null) return null;
		MultiGroup multiGroup=multiGroupdao.findById(grouponId);
		if(multiGroup == null) throw new UmpException("拼团数据不存在");
		
		List<MultiGroupSet> mgsets = multiGroupSetdao.find("select * from " + MultiGroupSet.table + " where multi_group_id=? ", grouponId); 
		Set<Long> productIds = new HashSet<Long>();
		for(MultiGroupSet mgs : mgsets){
			productIds.add(mgs.getProductId());
		}
		
		List<ProductResultDto> productDtos=new ArrayList<ProductResultDto>();
		for (Long productid : productIds) {
			ProductResultDto dto=getProductResultDto(multiGroup, productid);
			productDtos.add(dto);
		}
		GrouponResultDto resultDto=new GrouponResultDto();
		resultDto.setMultiGroup(multiGroup);
		resultDto.setProductDto(productDtos);
		return resultDto;
	}
	
	@Override
	public Page<ProductResultDto> getProducts4GrouponPage(ProductParamDto productParamDto) throws ProductException {
		List<ProductResultDto> resultDtos = new ArrayList<ProductResultDto>();
		Date currDate = new Date();
		final String select = "select * ";
		final String sqlExceptSelect = " from "+Product.table+" where app_id=? and is_marketable=1 and active=1 and id not in"
				                        + " (select product_id from " + MultiGroupSet.table + " where multi_group_id "
				                        + "in (select id from "+MultiGroup.table+" where app_id=? and active=1 and start_date<=? and end_date >=? ))";
		Page<Product> pages = productDao.paginate(productParamDto.getPageNo(), productParamDto.getPageSize(),
				select, sqlExceptSelect, productParamDto.getAuthUserId(), productParamDto.getAuthUserId(), currDate, currDate);
		for(Product product : pages.getList()){
			ProductResultDto resultDto = new ProductResultDto();
			resultDto.setId(product.getId());
			resultDto.setImg(getImageDomain() + product.getImage());
			resultDto.setName(product.getName().length()>10?product.getName().substring(0, 10).concat("..."):product.getName());
			resultDto.setPrice(product.getPrice());
			resultDto.setStock(product.getStock());
			
			 if(product.getIsUnifiedSpec() !=null && !product.getIsUnifiedSpec()){//是否多规格
				     List<String> arraySpec=new ArrayList<>();
				    String spec="";
					List<ProductSpecItem> specItems = prodSpecItemdao.find("select * FROM "+ProductSpecItem.table+" WHERE product_id = ? ", product.getId());
                       for (ProductSpecItem productSpecItem : specItems) {
                    	   spec=spec+productSpecItem.getPrice()+";"+productSpecItem.getStock()+",";
					}
                     arraySpec.add(spec);
				    List<String> arrayTitle=new ArrayList<>();
				    List<String> arrayInfor=new ArrayList<>();
					List<String> arrayInforId=new ArrayList<>();
				 List<ProductSpec> prodSpeces = prodSpecidao.find("select * from " + ProductSpec.table + " where product_id = ?", product.getId());
		            for(ProductSpec ps : prodSpeces){
		            	Specification speci = specDao.findById(ps.getSpecificationId());
		            	arrayTitle.add(speci.getName());
		            	String names="";
		            	String ids="";
		            	List<ProductSpecValue> prodSpecValues = prodSpecValuedao.find(
		            			"select * from " + ProductSpecValue.table + " where product_id=? and specification_id = ? ", product.getId(), ps.getSpecificationId());
		            	for(ProductSpecValue productSpecValue : prodSpecValues){
		            		SpecificationValue specificationValue = specValueDao.findById(productSpecValue.getSpecificationValueId());
		            		names=names+specificationValue.getName()+";";
		            		ids=ids+specificationValue.getId().toString()+";";
		            	}
		            	arrayInfor.add(names);
		            	arrayInforId.add(ids);
		            }
		            
		            resultDto.setArrayTitle(arrayTitle);
		            resultDto.setArrayInfor(arrayInfor);
		            resultDto.setArrayInforId(arrayInforId);
		            resultDto.setArraySpec(arraySpec);
			 }
			resultDtos.add(resultDto);
		}
	
		return new Page<ProductResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(), pages.getTotalRow());

	}
	
	@Override
	public Page<MultiGroup> list(GrouponParamDto grouponParamDto) throws UmpException {
		if(grouponParamDto == null)
			throw new UmpException("获取拼团活动列表数据参数错误");
		
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", grouponParamDto.getAuthUserId()));
		columns.add(Column.create("active", grouponParamDto.getActive()));
		columns.add(Column.create("status", grouponParamDto.getStatus()));
		return multiGroupdao.paginateByColumns(grouponParamDto.getPageNo(), grouponParamDto.getPageSize(), columns, " updated desc ");
	}
	
	@Override
	public ProductResultDto getProductResultDto(MultiGroup multiGroup, Long productId) throws UmpException {
		Product product = productDao.findById(productId);
		ProductResultDto resultDto = new ProductResultDto();
		resultDto.setId(product.getId());
		resultDto.setImg(getImageDomain() + product.getImage());
		resultDto.setName(product.getName().length()>20?product.getName().substring(0, 20).concat("..."):product.getName());
		resultDto.setPrice(product.getPrice());
		resultDto.setStock(product.getStock());
		
		if(product.getIsUnifiedSpec() !=null && !product.getIsUnifiedSpec()){//是否多规格
		    List<String> arraySpec=new ArrayList<>();
		    String spec="";
			List<ProductSpecItem> specItems = prodSpecItemdao.find("select * FROM "+ProductSpecItem.table+" WHERE product_id = ? ", product.getId());
               for (ProductSpecItem productSpecItem : specItems) {
            	   spec=spec+productSpecItem.getPrice()+";"+productSpecItem.getStock()+",";
			}
            arraySpec.add(spec);
		    List<String> arrayTitle=new ArrayList<>();
		    List<String> arrayInfor=new ArrayList<>();
			List<String> arrayInforId=new ArrayList<>();
			List<ProductSpec> prodSpeces = prodSpecidao.find("select * from " + ProductSpec.table + " where product_id = ?", product.getId());
            for(ProductSpec ps : prodSpeces){
            	Specification speci = specDao.findById(ps.getSpecificationId());
            	arrayTitle.add(speci.getName());
            	String names="";
            	String ids="";
            	List<ProductSpecValue> prodSpecValues = prodSpecValuedao.find(
            			"select * from " + ProductSpecValue.table + " where product_id=? and specification_id = ? ", product.getId(), ps.getSpecificationId());
            	for(ProductSpecValue productSpecValue : prodSpecValues){
            		SpecificationValue specificationValue = specValueDao.findById(productSpecValue.getSpecificationValueId());
            		names=names+specificationValue.getName()+";";
            		ids=ids+specificationValue.getId().toString()+";";
            	}
            	arrayInfor.add(names);
            	arrayInforId.add(ids);
            }
	            
        	resultDto.setArrayTitle(arrayTitle);
        	resultDto.setArrayInfor(arrayInfor);
        	resultDto.setArrayInforId(arrayInforId);
        	resultDto.setArraySpec(arraySpec);
		}
		
		List<String> arrayGroupSet=new ArrayList<>();
		String groupSet="";
		List<MultiGroupSet> items = multiGroupSetdao.find("select * FROM "+MultiGroupSet.table+" WHERE product_id = ? and multi_group_id=?", product.getId(),multiGroup.getId());
		for (MultiGroupSet multiGroupSet : items) {
			 groupSet=groupSet+multiGroupSet.getId()+";"+multiGroupSet.getCollagePrice()+",";
		}
		arrayGroupSet.add(groupSet);
		resultDto.setArrayGroupSet(arrayGroupSet);
		 
		return resultDto;
	}
	
	@Override
	public ProdGroupResultDto getProductGroup(Product product) throws UmpException {
		if(product == null) throw new ProductException("没有商品");
		
		MultiGroup multiGroup = getProductMultiGroup(product);
		if(multiGroup == null) return null;
		
		ProdGroupResultDto dto = new ProdGroupResultDto();
		if(multiGroup.getStartDate().after(new Date())){
			dto.setGroupTime("还差" + DateTimeUtil.compareDay(multiGroup.getStartDate(), new Date()) + "天开始");
		}else {
			dto.setGroupTime("剩余" + DateTimeUtil.compareDay(multiGroup.getEndDate(), new Date()) + "天结束");
		}
		dto.setQuota(multiGroup.getQuota());//当此拼团没人限购件数
		dto.setMultiGroupId(multiGroup.getId());
		dto.setGroupNum(multiGroup.getOfferNum()+"人成团");
		dto.setCollagePrice(getCollageSectionPrice(product, multiGroup));
		return dto;
	}

	/**
	 * 获取商品拼团活动区间价
	 * @return
	 */
	private String getCollageSectionPrice(Product product, MultiGroup multiGroup){
		List<MultiGroupSet> set = multiGroupSetdao.find(
				"select * from "+MultiGroupSet.table+" where multi_group_id=? and product_id=?", 
				multiGroup.getId(),product.getId());
		
		if(set == null || set.size()<=0) return null;
		
		BigDecimal min,max;
		min=max=set.get(0).getCollagePrice();
		
		for (MultiGroupSet multiGroupSet : set) {
			BigDecimal bprice = multiGroupSet.getCollagePrice();
			if(bprice.compareTo(max)==1){
				max= bprice;
			}
			if(bprice.compareTo(min)==-1){
				min= bprice;
			}
		}
		if(max.compareTo(min)==0){
			return max.toString();
		}else{  
		    return min+" ~ "+max;
		}
		
	}
	
	@Override
	public BigDecimal getCollagePrice(Product product, String specvalue) throws ProductException {
		MultiGroup group = getProductMultiGroup(product);
		if(group == null) return null;
		QueryHelper queryHelper = new QueryHelper();
		queryHelper.addWhere("multi_group_id", group.getId()).addWhere("product_id", product.getId()).addWhere("specification_value", specvalue).build();
		MultiGroupSet multiGroupSet=multiGroupSetdao.findFirst("select * from " + MultiGroupSet.table + queryHelper.getWhere(), queryHelper.getParams());
		return multiGroupSet == null ? null : multiGroupSet.getCollagePrice();
	}

	@Override
	public MultiGroup getProductMultiGroup(Product product) throws UmpException {
		List<MultiGroup> groups = multiGroupdao.find("select * from " + MultiGroup.table + 
				" where app_id=? and active=1 and start_date<=? and end_date>=?", product.getAppId(), new Date(), new Date());
		
		for(MultiGroup group : groups){
			List<MultiGroupSet> groupSets = multiGroupSetdao.find(" select * from " + MultiGroupSet.table + " where multi_group_id=? ", group.getId());
			for(MultiGroupSet groupSet : groupSets){
				if(groupSet.getProductId() == product.getId())
					return group;
			}
		}
		
		return null;
	}

	@Override
	public GroupingResultDto getGroupAndJoinUserInfos(Long groupId, Long buyerId) throws UmpException {
		if(groupId == null || buyerId == null) throw new UmpException("获取拼团成员信息缺少必要参数");
		
		//获取当前拼团订单
		//检查发起者正在组团的订单
		OrderGroup orderGroup = orderGroupdao.findById(groupId);
		if(orderGroup == null) return null;
		
		BuyerUser groupLeader = buyerUserdao.findById(orderGroup.getBuyerId());
		if(groupLeader == null) throw new UmpException("获取拼团成员信息失败，团长不存在");
		
		Product product = productDao.findById(orderGroup.getProductId());
		if(product == null) return null;
		
		//当前商品进行中的拼团活动
		MultiGroup multiGroup = getProductMultiGroup(product);
		if(multiGroup == null) throw new UmpException("拼团活动已过期");
		
		GroupingResultDto groupingDto = new GroupingResultDto();
		groupingDto.setProductId(orderGroup.getProductId());
		groupingDto.setGroupId(orderGroup.getId());
		groupingDto.setGroupHeader(groupLeader.getNickname().length()>4?groupLeader.getNickname().substring(0, 4).concat("..."):groupLeader.getNickname());
		groupingDto.setGroupHeaderId(orderGroup.getBuyerId());
		groupingDto.setProductName(product.getName());
		groupingDto.setProductImage(getImageDomain() + product.getImage());
		groupingDto.setProductPrice(product.getPrice());
		groupingDto.setGroupStatus(orderGroup.getGroupStatus());
		//检查拼团活动的时效
		if(multiGroup.getValidTime() != null){
			Integer expiresIn = multiGroup.getValidTime() * 3600; //转换成秒
			Long expiredTime = orderGroup.getCreated().getTime() + ((expiresIn -5) * 1000);
			if(expiredTime < System.currentTimeMillis())
				groupingDto.setIsExpires(true);
			Calendar ca=Calendar.getInstance();
			ca.setTime(orderGroup.getCreated());
			ca.add(Calendar.HOUR_OF_DAY, multiGroup.getValidTime());
			groupingDto.setExpiresIn((ca.getTime().getTime() - new Date().getTime())/1000);
		}
		
		groupingDto.setMultiGroupInfo(getProductGroup(product));
		
		List<Record> orderGusers = Db.find("select bu.id as group_user_id, bu.nickname, bu.headimgurl, og.*, og.created as join_time from " 
				+ OrderGuser.table + " og "
				+ " left join " + BuyerUser.table + " bu on og.buyer_id = bu.id "
				+ " where og.group_id= ? AND og.active=1 order by og.created asc ", orderGroup.getId());
		
		List<GrouponUserResultDto> groupUserDtos = new ArrayList<GrouponUserResultDto>();
		for(Record record : orderGusers){
			GrouponUserResultDto groupUser = new GrouponUserResultDto();
			groupUser.setId(record.getLong("group_user_id"));
			groupUser.setNickName(record.getStr("nickname").length()>4?record.getStr("nickname").substring(0, 4).concat("..."):record.getStr("nickname"));
			groupUser.setHeadimg(record.getStr("headimgurl"));
			groupUserDtos.add(groupUser);
			
			if(groupUser.getId().longValue() == buyerId.longValue()){
				groupingDto.setIsGrouped(true);
				groupingDto.setJoinTime(record.getDate("join_time"));
			}
		}
		groupingDto.setGroupUsers(groupUserDtos);
		groupingDto.setDiffUserCount(multiGroup.getOfferNum() - groupUserDtos.size());
		
		if(product.getIsUnifiedSpec() !=null && !product.getIsUnifiedSpec()){//是否多规格
        	List<SpecificationResultDto> specifications = new ArrayList<SpecificationResultDto>();
        	//查询商品多规格
            List<ProductSpec> prodSpeces = prodSpecidao.find("select * from " + ProductSpec.table + " where product_id = ?", product.getId());
            for(ProductSpec ps : prodSpeces){
            	SpecificationResultDto specificationResultDto = new SpecificationResultDto();
            	Specification speci = specDao.findById(ps.getSpecificationId());
            	specificationResultDto.setSpecification(SpecificationUtil.getSpecDto(speci));
            	List<ProductSpecValue> prodSpecValues = prodSpecValuedao.find(
            			"select * from " + ProductSpecValue.table + " where product_id=? and specification_id = ? ", product.getId(), ps.getSpecificationId());
            	List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue> ();
            	for(ProductSpecValue productSpecValue : prodSpecValues){
            		SpecificationValue specificationValue = specValueDao.findById(productSpecValue.getSpecificationValueId());
            		specificationValues.add(specificationValue);
            	}
            	specificationResultDto.setSpecificationValues(SpecificationUtil.getSpecValueDtoList(specificationValues));
            	specifications.add(specificationResultDto);
            }
            groupingDto.setSpecifications(specifications);
        }
		
		//sku组合 sku作为key 价格，库存作为值
		groupingDto.setPriceMap(productService.getProductSpecPrice(product.getId()));
		
		return groupingDto;
	}
	
	@Override
	public List<GroupingResultDto> getGroupsByProduct(Product product) throws UmpException {
		if(product == null) throw new UmpException("获取商品拼团列表缺少必要参数");
		//当前商品进行中的拼团活动
		MultiGroup multiGroup = getProductMultiGroup(product);
		if(multiGroup == null) return null;

		List<Record> groups = Db.find("select ogh.*, bu.nickname, bu.headimgurl, ogh.id as ogh_id, ogh.created as ogh_created from "
				+ OrderGroup.table + " ogh " 
				+ " left join " + BuyerUser.table + " bu on ogh.buyer_id=bu.id " 
				+ " where ogh.multi_group_id=? and ogh.product_id=? and ogh.active=1 and ogh.group_status=0", multiGroup.getId(), product.getId());
		if(groups == null) return null;

		List<GroupingResultDto> groupings = new ArrayList<GroupingResultDto>();
		
		int count = 0;
		for(Record rcd : groups){
			GroupingResultDto groupingDto = new GroupingResultDto();
			groupingDto.setGroupId(rcd.getLong("ogh_id"));
			groupingDto.setGroupHeader(rcd.getStr("nickname").length()>4?rcd.getStr("nickname").substring(0, 4).concat("..."):rcd.getStr("nickname"));
			groupingDto.setGroupHeaderImg(rcd.getStr("headimgurl"));
			groupingDto.setGroupHeaderId(rcd.getLong("buyer_id"));
			//检查拼团活动的时效
			if(multiGroup.getValidTime() != null){
				Integer expiresIn = multiGroup.getValidTime() * 3600; //转换成秒
				Long expiredTime = rcd.getDate("ogh_created").getTime() + ((expiresIn -5) * 1000);
				if(expiredTime < System.currentTimeMillis())
					groupingDto.setIsExpires(true);
				
				if(groupingDto.getIsExpires())//去掉超时未成团的数据
					continue;
				
				Calendar ca=Calendar.getInstance();
				ca.setTime(rcd.getDate("ogh_created"));
				ca.add(Calendar.HOUR_OF_DAY, multiGroup.getValidTime());
				groupingDto.setExpiresIn((ca.getTime().getTime() - new Date().getTime())/1000);
			}
			Long groupUserCont = Db.queryLong("select count(id) from " + OrderGuser.table + " where group_id=? AND active=1", rcd.getLong("ogh_id"));
			groupingDto.setDiffUserCount(multiGroup.getOfferNum() - groupUserCont.intValue());
			groupings.add(groupingDto);
			count++;
			if(count == 5) break;
		}
		
		return groupings;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MultiGroupService#getUnFinishMultiGroup()
	 */
	@Override
	public List<MultiGroup> getUnFinishMultiGroup() {
		return multiGroupdao.find("select * from " + MultiGroup.table + " where status != 2 and active =1");
	}

	@Override
	public List<Record> getMultiGroupMini(Long appId,Long id,int pageNo, int pageSize) {
		if (appId == null)
			throw new ProductException("微信端获取商品列表缺少必要参数");
		pageSize = 6;
		String select = "SELECT mgs.specification_value, mgs.prime_cost,mgs.collage_price,p.id, p.stock, p.name, p.image,mg.offer_num,mg.start_date,mg.end_date";
		String sqlExceptSelect = "FROM " + MultiGroupSet.table + " mgs "
				+ "left join " + Product.table + " p on p.id=mgs.product_id "
				+ "LEFT JOIN "+MultiGroup.table+" mg on mg.id=mgs.multi_group_id";

		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		
		helper.addWhere("p.app_id", appId)
				.addWhereTHEN_LE("mg.start_date", new Date())
				.addWhereTHEN_GE("mg.end_date", new Date())
				.addWhere("mgs.multi_group_id", id).build();

		Page<Record> pager = Db.paginate(pageNo, pageSize,
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		
		for (Record product : pager.getList()) {

			product.set("image", (getImageDomain() + product.getStr("image")));
			StringBuilder specifications = new StringBuilder();
			if(StrKit.notBlank(product.getStr("specification_value"))){
				String[] spfs = product.getStr("specification_value").split(",");
				for (String string : spfs) {
					SpecificationValue specification = specificationValueService.findById(Long.valueOf(string));
					specifications.append(specification.getName()+" ");
				}
			}
			product.set("specification_value", specifications.toString());
		}
		
		return pager.getList();
	}

	@Override
	public List<Record> getOrderProduct(Long multigroupId, Long buyerId, int pageNo, int pageSize) {
		if(multigroupId == null) throw new WxmallMsgBaseException("multigroupId is null");
		if(buyerId == null) throw new WxmallMsgBaseException("buyerId is null");
		
		String select = "SELECT o.group_status,p.`name`,p.id,p.image,oi.specification_value,mg.offer_num,mg.valid_time,og.created as og_created,gu.group_id as gu_gid ";
		String sqlExceptSelect = "FROM "+OrderGuser.table+" gu "
				+"LEFT JOIN "+OrderGroup.table+" og ON gu.group_id = og.id "
				+"LEFT JOIN "+Order.table+" o ON o.group_id = gu.group_id "
				+"LEFT JOIN "+OrderItem.table+" oi ON o.id = oi.order_id "
				+"LEFT JOIN "+MultiGroup.table+" mg ON og.multi_group_id = mg.id "
				+"LEFT JOIN "+Product.table+" p ON oi.product_id = p.id ";

		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("gu.buyer_id", buyerId).addWhere("mg.id", multigroupId)
		.addWhere("o.buyer_id", buyerId).addWhereNotNull("o.group_status").build();

		Page<Record> pager = Db.paginate(pageNo, pageSize,
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		for (Record product : pager.getList()) {
			product.set("image", (getImageDomain() + product.getStr("image")));
			StringBuilder specifications = new StringBuilder();
			if(StrKit.notBlank(product.getStr("specification_value"))){
				String[] spfs = product.getStr("specification_value").split(",");
				for (String string : spfs) {
					SpecificationValue specification = specificationValueService.findById(Long.valueOf(string));
					specifications.append(specification.getName()+" ");
				}
			}
			product.set("specification_value", specifications.toString());
			
			//检查拼团活动的时效
			if(product.getInt("valid_time") == null) continue;
			Integer expiresIn = product.getInt("valid_time") * 3600; //转换成秒
			Long expiredTime = product.getDate("og_created").getTime() + ((expiresIn -5) * 1000);
			if(expiredTime < System.currentTimeMillis()){
				product.set("expiresIn", "已超时");
				continue;
			}
			
			Calendar ca=Calendar.getInstance();
			ca.setTime(product.getDate("og_created"));
			ca.add(Calendar.HOUR_OF_DAY, product.getInt("valid_time"));
			product.set("expiresIn", (ca.getTime().getTime() - new Date().getTime())/1000);
			
			Long groupUserCont = Db.queryLong("select count(id) from " + OrderGuser.table + " where group_id=? ", product.getLong("gu_gid"));
			product.set("count",product.getInt("offer_num").intValue() - groupUserCont.intValue());
		}
		
		return pager.getList();
	
	}

	@Override
	public HashMap<String, String> getPutIn(Long id,AuthUser authUser) {
		String url = "http://"+authUser.getAppId()+".dbumama.com/multigroup?id="+id;
		String shortUrl = authUserService.getShortUrl(authUser,url);
		HashMap<String, String> put = new HashMap<String,String>();
		put.put("shortUrl", shortUrl);
		put.put("url", url);
		
		return put;
	}

	@Override
	public MultiGroup findByTicket(String ticket,Long appId) {
		return DAO.findFirst("select * from " + MultiGroup.table + " where follows_ticket=? and app_id = ? and active=1 ", ticket,appId);
	}

	@Override
	public void reply(AuthUser authUser, String openid, MultiGroup multiGroup) {
		
		if(multiGroup.getEndDate().before(new Date())) return;
		//图文消息
		List<Articles> newsList = new ArrayList<Articles>();
		Articles articlesTest = new Articles();
		articlesTest.setTitle("拼团活动");
		articlesTest.setDescription(multiGroup.getFollowsIntro());
		if(multiGroup.getFollowsImage()!=null){
			articlesTest.setPicurl(multiGroup.getFollowsImage());
		}
		String url = "http://"+authUser.getAppId()+".dbumama.com/multigroup?id="+multiGroup.getId();
		articlesTest.setUrl(authUserService.getShortUrl(authUser, url));
		
		newsList.add(articlesTest);
		CompCustomApi.sendNews(authUserService.getAccessToken(authUser), openid, newsList, authUser.getAppId());
		
	}

	@Override
	public void sendJoinMassages(Order order) {
		
		if(order.getOrderType() != OrderType.pintuan.ordinal()) return;
		
		//团
		OrderGroup orderGroup = orderGroupService.findById(order.getGroupId());
		
		//参团者
		BuyerUser buyerUser = buyerUserdao.findById(order.getBuyerId());
		
		//团配置
		MultiGroup multiGroup = findById(orderGroup.getMultiGroupId());
		
		//公众号
		AuthUser authUser = authUserService.findById(multiGroup.getAppId());
		
		//团商品
		Product product = productService.findById(orderGroup.getProductId());
		
		//团订单
		List<Order> orders = orderService.getOrdersByGroup(orderGroup.getId());
		
		//差多少人成团
		Integer diffGroupers = multiGroup.getOfferNum() - orders.size();
		diffGroupers = diffGroupers <=0 ? 0 : diffGroupers;
		
		//该团拼团结束时间
		String groupEndTime = DateTimeUtil.getNextDateTimeStringByHour(orderGroup.getCreated(), multiGroup.getValidTime());
		
		//通知该团成员，有人加入拼团了
		for(Order _order : orders){
			//订单项
			List<OrderItem> orderItems = orderItemService.getOrderItems(_order.getId());
			
			if(orderItems == null || orderItems.size()<=0) continue;
			
			OrderItem orderItem = orderItems.get(0);
			
			String text = "拼团人数+1\n"
					+ "通知类型:好友@"+buyerUser.getNickname()+"成功加入了您的拼团\n"
					+ "商品名称:"+product.getName()+"\n"
					+ "商品价格:"+orderItem.getPrice().toString()+"\n"
					+ "拼团状态:目前还差"+diffGroupers+"人即可成团\n"
					+ "拼团结束时间:"+groupEndTime+"\n\n";
			
			//使用图文消息回复
			//给团员发送拼团进度消息
			List<Articles> newsList = new ArrayList<Articles>();
			Articles articlesTest = new Articles();
			articlesTest.setTitle("拼团加入通知");
			articlesTest.setDescription(text);
			
			String url = "http://"+authUser.getAppId()+".dbumama.com/multigroup?id="+multiGroup.getId();
			String shortUrl = authUserService.getShortUrl(authUser,url);
			articlesTest.setUrl(shortUrl);
			newsList.add(articlesTest);
			ApiResult apiResult = CompCustomApi.sendNews(authUserService.getAccessToken(authUser), buyerUserdao.findById(_order.getBuyerId()).getOpenId(), newsList, authUser.getAppId());
			if(!apiResult.isSucceed()){
				logger.error("openId:" + buyerUserdao.findById(_order.getBuyerId()).getOpenId() + ", error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
			}
		}
		
	}
	
	@Override
	public void sendFailMassages(Order order) {
		
		if(order.getOrderType() != OrderType.pintuan.ordinal()) return;
		
		//团
		OrderGroup orderGroup = orderGroupService.findById(order.getGroupId());
		
		//团配置
		MultiGroup multiGroup = findById(orderGroup.getMultiGroupId());
		
		//公众号
		AuthUser authUser = authUserService.findById(multiGroup.getAppId());
		
		//团商品
		Product product = productService.findById(orderGroup.getProductId());
		
		//团订单
		List<Order> orders = orderService.getOrdersByGroup(orderGroup.getId());
		
		String text = "拼团失败\n"
				+ "商品名称:"+product.getName()+"\n"
				+ "您的拼团已结束，本次拼团失败\n"
				+ "没拼成，别灰心！首页有更多免费商品等你领,已支付订单金额会原路返回到您的微信账户\n\n";
		
		//使用图文消息回复
		//给团员发送拼团进度消息
		List<Articles> newsList = new ArrayList<Articles>();
		Articles articlesTest = new Articles();
		articlesTest.setTitle("拼团失败通知");
		articlesTest.setDescription(text);
		
		String url = "http://"+authUser.getAppId()+".dbumama.com/multigroup?id="+multiGroup.getId();
		String shortUrl = authUserService.getShortUrl(authUser,url);
		articlesTest.setUrl(shortUrl);
		newsList.add(articlesTest);
		
		//通知该团成员，有人加入拼团了
		for(Order _order : orders){
			//订单项
			List<OrderItem> orderItems = orderItemService.getOrderItems(_order.getId());
			
			if(orderItems == null || orderItems.size()<=0) continue;
			
			ApiResult apiResult = CompCustomApi.sendNews(authUserService.getAccessToken(authUser), buyerUserdao.findById(_order.getBuyerId()).getOpenId(), newsList, authUser.getAppId());
			if(!apiResult.isSucceed()){
				logger.error("openId:" + buyerUserdao.findById(_order.getBuyerId()).getOpenId() + ", error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
			}
		}
		
	}

	@Override
	public void sendSuccessMassages(Order order) {
		if(order.getOrderType() != OrderType.pintuan.ordinal()) return;
		
		//团
		OrderGroup orderGroup = orderGroupService.findById(order.getGroupId());
		
		//团长
		BuyerUser groupLeader = buyerUserdao.findById(orderGroup.getBuyerId());
		
		//团配置
		MultiGroup multiGroup =  findById(orderGroup.getMultiGroupId());
		
		//公众号
		AuthUser authUser = authUserService.findById(multiGroup.getAppId());
		
		//团商品
		Product product = productService.findById(orderGroup.getProductId());
		
		//团订单
		List<Order> orders = orderService.getOrdersByGroup(orderGroup.getId());
		
		StringBuffer groupUsers = new StringBuffer();
		for(Order _order : orders){
			groupUsers.append(buyerUserdao.findById(_order.getBuyerId()).getNickname() + ",");
		}
		if(groupUsers.length()>0) groupUsers.deleteCharAt(groupUsers.length() - 1);
		
		Integer multiType = multiGroup.getMultiType();
		if(multiType != null && multiType.intValue() == 1){
			_singleGroup(orders,groupLeader,product,groupUsers,authUser,multiGroup);
		}else{
			_defaultGroup(orders,groupLeader,product,groupUsers,authUser,multiGroup);
		}
	}

	
	private void _defaultGroup(List<Order> orders, BuyerUser groupLeader, Product product, StringBuffer groupUsers,
			AuthUser authUser,MultiGroup multiGroup) {
		//通知该团成员，拼团成功
		for(Order _order : orders){
			//订单项
			List<OrderItem> orderItems = orderItemService.getOrderItems(_order.getId());
			
			if(orderItems == null || orderItems.size()<=0) continue;
			
			OrderItem orderItem = orderItems.get(0);
			
			String text = "恭喜拼团成功\n"
					+ "商品名称:"+product.getName()+"\n"
					+ "商品价格:"+orderItem.getPrice().toString()+"\n"
					+ "团长:"+groupLeader.getNickname()+"\n"
					+ "团成员:"+groupUsers.toString()+"\n"
					+ "恭喜本次拼团成功，请耐心等待商家发货\n\n";
			
			//使用图文消息回复
			//给团员发送拼团进度消息
			List<Articles> newsList = new ArrayList<Articles>();
			Articles articlesTest = new Articles();
			articlesTest.setTitle("拼团通知");
			articlesTest.setDescription(text);
			
			String url = "http://"+authUser.getAppId()+".dbumama.com/multigroup?id="+multiGroup.getId();
			String shortUrl = authUserService.getShortUrl(authUser,url);
			articlesTest.setUrl(shortUrl);
			newsList.add(articlesTest);
			ApiResult apiResult = CompCustomApi.sendNews(authUserService.getAccessToken(authUser),buyerUserdao.findById(_order.getBuyerId()).getOpenId(), newsList, authUser.getAppId());
			if(!apiResult.isSucceed()){
				logger.error("openId:" +buyerUserdao.findById(_order.getBuyerId()).getOpenId() + ", error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
			}
			
		}
		
	}

	private void _singleGroup(List<Order> orders, BuyerUser groupLeader, Product product, StringBuffer groupUsers,
			AuthUser authUser,MultiGroup multiGroup) {
		
		//获取中奖的订单
		Order winOrder = getwinOrder(orders);
		List<OrderItem> items = orderItemService.getOrderItems(winOrder.getId());
		OrderItem winItem = items.get(0);

		//中奖者
		BuyerUser user = buyerUserdao.findById(winOrder.getBuyerId());
		String text = "恭喜被抽中\n"
				+ "商品名称:"+product.getName()+"\n"
				+ "商品价格:"+winItem.getPrice().toString()+"\n"
				+ "团长:"+groupLeader.getNickname()+"\n"
				+ "团成员:"+groupUsers.toString()+"\n"
				+ "中奖成员:"+user.getNickname()+"\n\n"
				+ "恭喜你在本次拼团被抽中了，请耐心等待商家发货\n\n";
		
		//使用图文消息回复
		//给团员发送拼团进度消息
		List<Articles> newsList = new ArrayList<Articles>();
		Articles articlesTest = new Articles();
		articlesTest.setTitle("拼团通知");
		articlesTest.setDescription(text);
		
		String url = "http://"+authUser.getAppId()+".dbumama.com/multigroup?id="+multiGroup.getId();
		String shortUrl = authUserService.getShortUrl(authUser,url);
		articlesTest.setUrl(shortUrl);
		newsList.add(articlesTest);
		ApiResult apiResult = CompCustomApi.sendNews(authUserService.getAccessToken(authUser),  user.getOpenId(), newsList, authUser.getAppId());
		if(!apiResult.isSucceed()){
			logger.error("openId:" + user.getOpenId() + ", error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
		}
		
		//通知该团其他成员，中奖者是谁
		for(Order _order : orders){
			
			if(_order.getId().longValue() == winOrder.getAppId().longValue()) continue;
			//订单项
			List<OrderItem> orderItems = orderItemService.getOrderItems(_order.getId());
			
			if(orderItems == null || orderItems.size()<=0) continue;
			
			try {
				orderService.refund(_order);	
			} catch (WxmallBaseException e) {
				//如果中途有退款接口调用失败，记录失败记录到数据库
				//由于此处把拼团超时状态更改后，不管退款成功与否，
				//定时任务是再也扫描不到这个拼团任务了，所以必须记录退款失败的情况
				RefundError rerror = refundErrorService.findByOrderId(_order.getId());
				if(rerror == null){
					rerror = new RefundError();
					rerror.setOrderId(_order.getId());
					rerror.setActive(true);
				}
				rerror.setRefundError(e.getMessage());
				refundErrorService.saveOrUpdate(rerror);
			}
			
			text = "本次拼团未中奖\n"
					+ "商品名称:"+product.getName()+"\n"
					+ "商品价格:"+winItem.getPrice().toString()+"\n"
					+ "团长:"+groupLeader.getNickname()+"\n"
					+ "团成员:"+groupUsers.toString()+"\n"
					+ "中奖成员:"+user.getNickname()+"\n\n"
					+ "很遗憾本次拼团您未中奖，别灰心！点击有更多免费商品等你领,已支付订单金额会原路返回到您的微信账户\n\n";
			
			//使用图文消息回复
			//给团员发送拼团进度消息
			newsList = new ArrayList<Articles>();
			articlesTest = new Articles();
			articlesTest.setTitle("拼团加入通知");
			articlesTest.setDescription(text);
			
			articlesTest.setUrl(shortUrl);
			newsList.add(articlesTest);
			apiResult = CompCustomApi.sendNews(authUserService.getAccessToken(authUser), buyerUserdao.findById(_order.getBuyerId()).getOpenId(), newsList, authUser.getAppId());
			if(!apiResult.isSucceed()){
				logger.error("openId:" + buyerUserdao.findById(_order.getBuyerId()).getOpenId() + ", error_code:" + apiResult.getErrorCode() + ",error_msg" + apiResult.getErrorMsg());
			}
		}
	}
	
	private Order getwinOrder(List<Order> orders){
		int num = new Random().nextInt(orders.size());
		Order winOrder = orders.get(num);
		List<OrderItem> items = orderItemService.getOrderItems(winOrder.getId());

		if(items == null || items.size()<=0){
			getwinOrder(orders);
		}
		return winOrder;
	}

}