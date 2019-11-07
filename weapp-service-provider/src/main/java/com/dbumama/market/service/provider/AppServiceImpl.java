package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbumama.market.model.App;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.AuthUserApp;
import com.dbumama.market.service.api.AppService;
import com.dbumama.market.service.api.WxmallMsgBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class AppServiceImpl extends WxmServiceBase<App> implements AppService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AppService#findApps()
	 */
	@Override
	public List<App> findApps(AuthUser authUser) {
		if(authUser == null) return null;
		
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(Column.create("active", 1));
		if(authUser.getServiceType() == 0){
			//小程序应用
			columnList.add(Column.create("app_type", 2));
		}else if(authUser.getServiceType() == 2){
			//服务号应用
			columnList.add(Column.create("app_type", 1));
		}else if(authUser.getServiceType() == 1){
			//订阅号应用
			columnList.add(Column.create("app_type", 1));
		}else{
			return null;
		}
		
		Columns columns = Columns.create(columnList);
		List<App> apps = DAO.findListByColumns(columns, " created asc ");
		
		apps.addAll(DAO.find("select * from " + App.table + " where app_type=0 and active=1"));
		
		return apps;
	}

	@Override
	public List<App> findApps() {
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(Column.create("active", 1));

		//服务号应用
		columnList.add(Column.create("app_type", 1));
		
		Columns columns = Columns.create(columnList);
		List<App> apps = DAO.findListByColumns(columns, " created asc ");
		
		apps.addAll(DAO.find("select * from " + App.table + " where app_type=0 and active=1"));
		
		return apps;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AppService#hotApps()
	 */
	@Override
	public List<App> hotApps() {
		List<Record> records = Db.find("select count(aua.app_id) as order_num, aua.app_id from "+AuthUserApp.table+" aua GROUP BY aua.app_id ORDER BY order_num DESC limit 10 ");
		
		List<App> apps = new ArrayList<App>();
		for(Record rcd : records){
			App app = findById(rcd.getLong("app_id"));
			apps.add(app);
		}
		
		return apps;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AppService#newApps()
	 */
	@Override
	public List<App> newApps() {
		return DAO.find("select * from " + App.table + " order by created desc limit 10");
	}

	@Override
	public void save(Long id, Integer app_category,Integer app_type, String app_image, String app_name, String imgList,
			String app_desc,String app_content) {
		
		if(StrKit.isBlank(app_name)) throw new WxmallMsgBaseException("app_name is null");
		if(app_category == null) throw new WxmallMsgBaseException("app_category is null");
		if(StrKit.isBlank(app_image)) throw new WxmallMsgBaseException("app_image is null");
		if(StrKit.isBlank(imgList)) throw new WxmallMsgBaseException("imgList is null");		
		if(app_type == null) throw new WxmallMsgBaseException("app_type is null");		
		
		App app = null;
		if(id != null){
			app = findById(id);
			if(app == null) throw new WxmallMsgBaseException("nApp is null");
		}else{
			app = new App();
			app.setCreated(new Date());
		}
		app.setAppName(app_name).setAppImage(app_image).setAppCategory(app_category)
		.setAppContent(app_content).setUpdated(new Date()).setActive(true).setAppShowImages(imgList)
		.setAppType(app_type).setAppDesc(app_desc);
		
		try {
			saveOrUpdate(app);
		} catch (Exception e) {
			throw new WxmallMsgBaseException(e.getMessage());
		}
	}

}