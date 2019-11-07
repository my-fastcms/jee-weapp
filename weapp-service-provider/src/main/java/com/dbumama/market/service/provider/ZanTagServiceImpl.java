package com.dbumama.market.service.provider;

import java.util.Date;
import java.util.List;

import com.dbumama.market.model.ZanTag;
import com.dbumama.market.service.api.ZanTagService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class ZanTagServiceImpl extends WxmServiceBase<ZanTag> implements ZanTagService {

	@Override
	@Before(Tx.class)
	public ZanTag save(Long shopId, String tagName, Long tagId) throws Exception{
		if(shopId == null) throw new Exception("shopId is null");
		if(StrKit.isBlank(tagName)) throw new Exception("tagName is null");
		ZanTag zanTag = new ZanTag();
		if(tagId == null){
			zanTag.setAppId(shopId).setTagName(tagName).setCreated(new Date()).setUpdated(new Date()).setActive(true);
			zanTag.save();
		}
		else{
			zanTag = findById(tagId);
			zanTag.setTagName(tagName).setUpdated(new Date());
			zanTag.update();
		}
		return zanTag;
	}

	@Override
	public ZanTag findByShopId(Long shopId) {
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", shopId));
		columns.add(Column.create("active", 1));
		return DAO.findFirstByColumns(columns);	
	}


	@Override
	public List<ZanTag> findZanTagsByShopId(Long shopId) {
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", shopId));
		columns.add(Column.create("active", 1));
		return DAO.findListByColumns(columns);	
	}

}