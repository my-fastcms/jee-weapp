package com.dbumama.market.service.base;

import com.dbumama.market.WeappConstants;
import com.jfinal.log.Log;
import io.jboot.db.model.Columns;
import io.jboot.db.model.JbootModel;
import io.jboot.service.JbootServiceBase;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 公共服务类
 * @author wangjun
 *
 */
public abstract class WxmServiceBase<M extends JbootModel<M>> extends JbootServiceBase<M>{
	
	protected static final Log logger = Log.getLog(WxmServiceBase.class);

	protected synchronized String getTradeNo(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	protected synchronized String getUUID(){
		return UUID.randomUUID().toString();
	}

	protected String getImageDomain(){
		return WeappConstants.IMAGE_DOMAIN;
	}
	
	protected String getRandom6Str(){
		return String.valueOf(new Random().nextInt(899999) + 100000);
	}
	
	/**
	 * @param columns
	 * @return
	 */
	public List<M> findByColumns(Columns columns){
		return DAO.findListByColumns(columns);
	}
	
}
