package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbumama.market.model.PlatActivitys;
import com.dbumama.market.service.api.PlatActivitysService;
import com.dbumama.market.service.api.WxmallMsgBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class PlatActivitysServiceImpl extends WxmServiceBase<PlatActivitys> implements PlatActivitysService {

	@Override
	public void save(Long platUserId, Long activityId, String name,
			Date startDate, Date endDate, String explain,
			String imgPath) {
		
		if(StrKit.isBlank(name)) throw new WxmallMsgBaseException("name is null");
		if(startDate == null) throw new WxmallMsgBaseException("startDate is null");
		if(endDate == null) throw new WxmallMsgBaseException("endDate is null");
		if(startDate.after(endDate)) throw new WxmallMsgBaseException("活动开始时间必须在活动结束时间之前");
		if(StrKit.isBlank(explain)) throw new WxmallMsgBaseException("explain is null");
		if(StrKit.isBlank(imgPath)) throw new WxmallMsgBaseException("imgPath is null");
		
		//新增的情况，检查其他任务时间
		Record dateRcd = Db.findFirst("select id,end_date from "+PlatActivitys.table
				+ " where active=1 and plat_user_id=? "
				+ " ORDER BY end_date desc LIMIT 1 ", platUserId);
		Date nowDate = new Date();
		
		PlatActivitys platActivitys = findById(activityId);
		if(platActivitys == null){
			if(dateRcd != null){
				Date _endDate = dateRcd.getDate("end_date");
				if(_endDate == null) throw new WxmallMsgBaseException("check time is error _endDate is null");
				if(nowDate.compareTo(_endDate) == 0 || nowDate.before(_endDate))
					throw new WxmallMsgBaseException("已有进行中或未开始的任务！！！");
			}
			platActivitys = new PlatActivitys();
			platActivitys.setPlatUserId(platUserId).setCreated(new Date());
		}else{
			if(dateRcd != null && dateRcd.getInt("id").intValue() != platActivitys.getId().intValue()){
				Date _endDate = dateRcd.getDate("end_date");
				if(_endDate == null) throw new WxmallMsgBaseException("check time is error _endDate is null");
				if(nowDate.compareTo(_endDate) == 0 || nowDate.before(_endDate))
					throw new WxmallMsgBaseException("已有进行中或未开始的任务！！！");
			}
		}
		platActivitys.setName(name).setEndDate(endDate).setStartDate(startDate)
		.setExplain(explain).setImgPath(imgPath).setUpdated(new Date()).setActive(true);
		saveOrUpdate(platActivitys);
		
	}

	@Override
	public Page<PlatActivitys> list(Long platUserId, int pageNo, int pageSize,
			String name) {
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(Column.create("plat_user_id", platUserId));
		if(!StrKit.isBlank(name)){
			columnList.add(Column.create("name", "%"+name+"%", Column.LOGIC_LIKE));			
		}
		Columns columns = Columns.create(columnList);
		
		Page<PlatActivitys> platActivitysPage = DAO.paginateByColumns(pageNo, pageSize, columns, " updated desc ");
		
		return platActivitysPage;
	}

	@Override
	public PlatActivitys getOnline() {
		return DAO.findFirst("select * from " + PlatActivitys.table
				+ " where ?>=start_date and ?<=end_date", new Date(), new Date());
	}

	@Override
	public PlatActivitys findOnlieById(Long activityId) {
		return DAO.findFirst("select * from " + PlatActivitys.table
				+ " where ?>=start_date and ?<=end_date and id = ? ", new Date(), new Date(), activityId);
	}

}