package com.dbumama.market.service.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.FullCut;
import com.dbumama.market.model.FullCutProduct;
import com.dbumama.market.model.FullCutSet;
import com.dbumama.market.model.Product;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.ActivityStatus;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.utils.DateTimeUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
public class FullCutServiceImpl extends WxmServiceBase<FullCut> implements FullCutService {

	private static final FullCut fullCutDao = new FullCut().dao();
	private static final FullCutSet fullCutSetDao = new FullCutSet().dao();
	private static final Product productDao = new Product().dao();
	
	@Override
	public FullCut save(FullCutParamDto paramDto) throws UmpException {
		if(paramDto == null) throw new UmpException("保存出错,请检查参数");
		FullCut fullCut=new FullCut();
		fullCut.setId(paramDto.getId());
		fullCut.setFullCutName(paramDto.getFullCutName());
		fullCut.setStartDate(paramDto.getStartDate());
		fullCut.setEndDate(paramDto.getEndDate());
		fullCut.setAppId(paramDto.getAuthUserId());
		fullCut.setProductIds(paramDto.getProductIds());
		
		if(fullCut.getStartDate().before(new Date()) && fullCut.getEndDate().after(new Date())){
			fullCut.setStatus(ActivityStatus.activing.ordinal());//进行中
		}else if(fullCut.getStartDate().after(new Date())){
			fullCut.setStatus(ActivityStatus.unactive.ordinal());
		}else if(fullCut.getEndDate().before(new Date())){
			fullCut.setStatus(ActivityStatus.activeend.ordinal());
		}
		
		if(fullCut.getId()==null){
			fullCut.setActive(true);
			fullCut.save();
			String productids=paramDto.getProductIds();
			String productIds[]=(productids.substring(0,productids.length()-1)).split("-");
			for (int i = 0; i < productIds.length; i++) {
				FullCutProduct p=new FullCutProduct();
				p.setProductId(new Long(productIds[i]));
				p.setFullCutId(fullCut.getId());
				try {
					p.save();
				} catch (Exception e) {
					throw new UmpException(e.getMessage());
				}
			}
		}else{
			fullCut.update();
			Db.deleteById(FullCutProduct.table,"full_cut_id",fullCut.getId());
			String productids=paramDto.getProductIds();
			String productIds[]=(productids.substring(0,productids.length()-1)).split("-");
			for (int i = 0; i < productIds.length; i++) {
				FullCutProduct p=new FullCutProduct();
				p.setProductId(new Long(productIds[i]));
				p.setFullCutId(fullCut.getId());
				try {
					p.save();
				} catch (Exception e) {
					throw new UmpException(e.getMessage());
				}
			}
		}
		JSONArray jarr = null; 
		try {
			jarr = JSONArray.parseArray(paramDto.getSetItem());					
		} catch (Exception e) {
			throw new UmpException(e.getMessage());
		}
		
		if(jarr == null || jarr.size()<=0) throw new UmpException("没有设置满减配置项");
		
		JSONArray newArr = new JSONArray();
		for(Iterator<?> iterator = jarr.iterator(); iterator.hasNext();){
			JSONObject job = (JSONObject) iterator.next(); 
			 if(!"del".equals(job.getString("opt"))){
				newArr.add(job);
			 }
		}
		
		if(newArr.size()<=0) throw new UmpException("没有设置满减配置项");
		
		 for (Iterator<?> iterator = jarr.iterator(); iterator.hasNext();) { 
	          JSONObject job = (JSONObject) iterator.next(); 
	          FullCutSet fullCutset=new FullCutSet();
	          fullCutset.setId(job.getLong("id"));
	          fullCutset.setFullCutId(fullCut.getId());
	          fullCutset.setMeet(job.getBigDecimal("meet"));
	          if(job.getInteger("cash_required")==null){
	        	  fullCutset.setCashRequired(0);
	          }else{
	        	  fullCutset.setCashRequired(job.getInteger("cash_required"));
	          }
	          if(job.getInteger("postage")==null){
	        	  fullCutset.setPostage(0); 
	          }else{
	        	  fullCutset.setPostage(job.getInteger("postage"));
	          }
	          fullCutset.setCash(job.getBigDecimal("cash"));
	          if(fullCutset.getId()==null){
	        	  fullCutset.setActive(true);
	        	  try {
	        		  fullCutset.save();
	        	  } catch (Exception e) {
	        		  throw new UmpException(e.getMessage());
	        	  }
	          }else{
	        	  if("del".equals(job.getString("opt"))){
	        		  try {
	        			  fullCutset.delete();
					  } catch (Exception e) {
						  throw new UmpException(e.getMessage());
					  }
	        	  }else{
	        		  try {
		        		  fullCutset.update();						
					  } catch (Exception e) {
						  throw new UmpException(e.getMessage());
					  }
	        	  }
	          }
		 }
		return fullCut; 
	}

	@Override
	public Page<ProductResultDto> getProducts4FullCutPage(ProductParamDto productParamDto) throws ProductException {
		if(productParamDto == null || productParamDto.getAuthUserId() == null) throw new ProductException("获取未打折商品列表出错");
		List<ProductResultDto> resultDtos = new ArrayList<ProductResultDto>();
		
		//查询出商品有效满减送数据
		Date currDate = new Date();
		final String select = "select * ";
		final String sqlExceptSelect = " from "+Product.table+" where app_id=? and id not in"
				+ " (select product_id from " + FullCutProduct.table + " where full_cut_id "
				+ "in (select id from "+FullCut.table+" where app_id=? and start_date<=? and end_date >=? ))";
		
		Page<Product> pages = productDao.paginate(productParamDto.getPageNo(), productParamDto.getPageSize(),
				select, sqlExceptSelect, productParamDto.getAuthUserId(), productParamDto.getAuthUserId(), currDate, currDate);
		for(Product product : pages.getList()){
			ProductResultDto resultDto = new ProductResultDto();
			resultDto.setId(product.getId());
			resultDto.setImg(getImageDomain() + product.getImage());
			resultDto.setName(product.getName());
			resultDto.setPrice(product.getPrice());
			resultDto.setStock(product.getStock());
			resultDtos.add(resultDto);
		}
	
		return new Page<ProductResultDto>(resultDtos, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public Page<FullCut> list(FullcutPageParamDto promotionParam) throws UmpException {
		if(promotionParam == null)
			throw new UmpException("获取满减送列表数据参数错误");
		
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", promotionParam.getAuthUserId()));
		columns.add(Column.create("active", promotionParam.getActive()));
		columns.add(Column.create("status", promotionParam.getStatus()));
		
		Page<FullCut> pages = fullCutDao.paginateByColumns(promotionParam.getPageNo(), promotionParam.getPageSize(), columns, " updated desc ");
		List<FullCut> lists=new ArrayList<FullCut>();
		for (FullCut fullCut : pages.getList()) {
			lists.add(fullCut);
		}
		return new Page<FullCut> (lists, promotionParam.getPageNo(), promotionParam.getPageSize(), pages.getTotalPage(), pages.getTotalRow());
	}

	@Override
	public List<ProdFullCutResultDto> getProductFullCut(Product product) throws UmpException {
		List<ProdFullCutResultDto> resultDtos=new ArrayList<ProdFullCutResultDto>();
		String sql="select f.start_date,f.end_date,s.meet,s.cash,s.cash_required,s.postage from t_full_cut f LEFT JOIN t_full_cut_set s on "
				   +" f.id=s.full_cut_id where f.id in "
				   +"(select p.full_cut_id from t_full_cut_product p where p.product_id=? ) and f.app_id=? and f.start_date<=? and f.end_date>=?";
		List<Record> rocords=Db.find(sql,product.getId(),product.getAppId(), new Date(), new Date());
			for (Record record : rocords) {
				ProdFullCutResultDto profcrd=new ProdFullCutResultDto();
				Date startDate=record.getDate("start_date");
				Date endDate=record.getDate("end_date");
				BigDecimal meet=record.getBigDecimal("meet");
				BigDecimal cash=record.getBigDecimal("cash");
				int cashRequired=record.getInt("cash_required");
				int postage=record.getInt("postage");
				profcrd.setMeet(meet);
				profcrd.setCash(cash);
				profcrd.setPostage(postage);
				if(cashRequired==1&&postage==1){
					profcrd.setFullCutInfo("满"+meet+"元送"+cash+"元+包邮");
				}else if(cashRequired==1&&postage!=1){
					profcrd.setFullCutInfo("满"+meet+"元送"+cash+"元");
				}else if(cashRequired!=1&&postage==1){
					profcrd.setFullCutInfo("满"+meet+"元包邮"); 
				}
				 
				if(startDate.after(new Date())){
					profcrd.setFullCutTime("还差" + DateTimeUtil.compareDay(startDate, new Date()) + "天开始");
				}else{
					profcrd.setFullCutTime("剩余" + DateTimeUtil.compareDay(endDate, new Date()) + "天结束");
				}
				 
				resultDtos.add(profcrd);
			}
		return resultDtos;
	}

	@Override
	public List<FullCutSet> getFullCutSetsByFullCut(Long fullCutId) {
	    return fullCutSetDao.find("Select * from "+FullCutSet.table +" WHERE full_cut_id=?", fullCutId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.FullCutService#getFullCutInfo(java.lang.Long)
	 */
	@Override
	public FullCutResultDto getFullCutInfo(Long id) {
		if(id == null) return null;
		
		FullCut fullCut=findById(id);
		if(fullCut == null) return null;
		
		FullCutResultDto dto = new FullCutResultDto();

		dto.setId(fullCut.getId());
		dto.setName(fullCut.getFullCutName());
		dto.setStartDate(fullCut.getStartDate());
		dto.setEndDate(fullCut.getEndDate());
		dto.setProductIds(fullCut.getProductIds());
	    dto.setSets(getFullCutSetsByFullCut(fullCut.getId()));
	    
		return dto;
	}

	@Override
	public List<Record> getFullCutMini(Long appId,
			int pageNo, int pageSize) {
		if (appId == null)
			throw new ProductException("微信端获取商品列表缺少必要参数");
		pageSize = 4;
		String select = "SELECT p.id, p.stock, p.name, p.image,p.price";
		String sqlExceptSelect = "FROM " + FullCut.table + " fc "
				+ " left join " + FullCutProduct.table + " fcp on fcp.full_cut_id=fc.id " 
				+ " left join " + Product.table + " p on p.id=fcp.product_id " ;

		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("fc.app_id", appId).addWhere("fc.active", 1).addWhere("fc.status", 0).build();

		Page<Record> pager = Db.paginate(pageNo, pageSize,
				helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		for (Record product : pager.getList()) {
			product.set("image", (getImageDomain() + product.getStr("image")));
		}
		return pager.getList();
	}
	
}