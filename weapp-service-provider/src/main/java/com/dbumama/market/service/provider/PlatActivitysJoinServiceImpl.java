package com.dbumama.market.service.provider;

import com.dbumama.market.WeappConstants;
import com.dbumama.market.model.*;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.weixin.api.CompTagApi;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
@RPCBean
public class PlatActivitysJoinServiceImpl extends WxmServiceBase<PlatActivitysJoin> implements PlatActivitysJoinService {

	@Inject
	private PhoneCodeService phoneCodeService;
	@Inject
	private SellerUserService sellerUserService;
	@Inject
	private AuthUserService authUserService;
	@Inject
	private PlatActivitysService platActivitysService;
	
	@Override
	public void joinUser(AuthUser authUser, SellerUser sellerUser, Long activityId) {
		if(authUser == null) throw new WxmallMsgBaseException("authUser is null");
		if(sellerUser == null) throw new WxmallMsgBaseException("sellerUser is null");
		if(activityId == null) throw new WxmallMsgBaseException("activityId is null");

		PlatActivitysJoin platActivitysJoin = findIsJoin(authUser.getId(), activityId);
		if(platActivitysJoin != null){
			throw new WxmallMsgBaseException("您已报名过该活动，无需重复报名！");
		}
		try{
			platActivitysJoin = new PlatActivitysJoin();
			platActivitysJoin.setAppId(authUser.getId()).setFansCount(authUser.getFansCount()).setActivityId(activityId).setActive(true)
			.setCreated(new Date()).setUpdated(new Date());
			save(platActivitysJoin);
		}catch(WxmallMsgBaseException e){
			throw new WxmallMsgBaseException("参加活动出现异常，请稍后重试！");
		}
		
		//插入join数据之后
		if(StrKit.notBlank(sellerUser.getOpenId())){
			AuthUser authUserdbu = authUserService.getAuthUserByAppId(WeappConstants.WECHAT_LOGIN_APPID);
			
			List<String> openIdList = new ArrayList<String>();
			openIdList.add(sellerUser.getOpenId());
			CompTagApi.batchAddTag(111, openIdList, authUserService.getAccessToken(authUserdbu));	
		}
	}

	@Override
	public void addPhone(SellerUser sellerUser, String phone, String phoneCode) {
		if(sellerUser == null) throw new WxmallMsgBaseException("sellerUser is null");
		if(StrKit.isBlank(phone)) throw new WxmallMsgBaseException("phone is null");
		if(StrKit.isBlank(phoneCode)) throw new WxmallMsgBaseException("phoneCode is null");
		
		//check 手机验证码
		UserCode userCode = phoneCodeService.getVerifyUserCode(phone, phoneCode);
		if(userCode == null){
			throw new WxmallMsgBaseException("手机验证码错误");
		}
		//检查验证码是否过期 30分钟后过期
		Integer expires_in = 1800;
		Long expiredTime = userCode.getUpdated().getTime() + ((expires_in -5) * 1000);
		if(expiredTime == null || expiredTime < System.currentTimeMillis()){
			throw new WxmallMsgBaseException("验证码已过期");
		}
		SellerUser sellerUserUpdate = sellerUserService.findById(sellerUser.getId());
		sellerUserUpdate.setPhone(phone).setUpdated(new Date());
		sellerUserService.update(sellerUserUpdate);
	}

	@Override
	public PlatActivitysJoin findIsJoin(Long  appId, Long activityId) {
		return DAO.findFirst("select * from " + PlatActivitysJoin.table + " where app_id=? and activity_id = ?", appId,activityId);
	}

	@Override
	public Page<Record> lookList(Long activityId, String nickName, int pageNo, int pageSize) {
		PlatActivitys platActivitys = platActivitysService.findOnlieById(activityId);
		if(platActivitys != null){//活动进行中，粉丝数每次更新
			String select = "SELECT paj.fans_count as oFansCount, au.nick_name, au.fans_count as nFansCount, (au.fans_count-paj.fans_count) as fansCount";
			String sqlExceptSelect = "FROM " + PlatActivitysJoin.table + " paj "
					+ " left join " + AuthUser.table + " au on paj.app_id=au.id " ;
	
			QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
			helper.addWhere("paj.activity_id", activityId).addWhere("paj.active", 1).addWhereLike("au.nick_name", nickName).addOrderBy("desc","fansCount").build();
	
			Page<Record> pager = Db.paginate(pageNo, pageSize,
					helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
			//排名 名次
			for (int i=0;i<pager.getList().size();i++) {
				pager.getList().get(i).set("num",i+1);
			}
			return pager;	
		}else{//活动结束，粉丝数不更新
			Page<Record> platActivitysJoin = findByActId(activityId, pageNo, pageSize);
			return platActivitysJoin;
		}
	}
	@Override
	public void stopAct(Long activityId) {
		//判断活动粉丝是否已经停用增长粉丝
		PlatActivitys platActivitys = platActivitysService.findById(activityId);
		if(!platActivitys.getActive()) throw new WxmallMsgBaseException("已停用，无需重复操作");
		
		String select = "SELECT paj.fans_count as oFansCount, au.nick_name, au.fans_count as nFansCount, (au.fans_count-paj.fans_count) as fansCount";
		String sqlExceptSelect = "FROM " + PlatActivitysJoin.table + " paj "
				+ " left join " + AuthUser.table + " au on paj.app_id=au.id " ;

		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("paj.activity_id", activityId).addWhere("paj.active", 1).addOrderBy("desc","fansCount").build();

		List<Record> pager = Db.find(helper.getSelect()+" "+helper.getSqlExceptSelect(), helper.getParams());
		//批量更新粉丝数， 组装sql语句
		String sql = "UPDATE " + PlatActivitysJoin.table + " SET add_fans_count = CASE id";
		String ids = "";
		for (int i=0;i<pager.size();i++) {
			sql += " WHEN " + pager.get(i).getLong("id") + " THEN " +pager.get(i).getInt("fansCount");
			ids += pager.get(i).getLong("id") + ",";
		}
		sql += " END WHERE id IN (" + ids.substring(0, ids.length() - 1) + ") ";
		System.out.println(sql);
		try{
			Db.update(sql);
		}catch(WxmallMsgBaseException e){
			throw new WxmallMsgBaseException("停用错误");
		}
		//更改状态
		platActivitys.setActive(false).setUpdated(new Date());
		platActivitysService.update(platActivitys);
	}

	private Page<Record> findByActId(Long activityId, int pageNo, int pageSize) {
		String select = "SELECT paj.add_fans_count as fansCount, au.nick_name";
		String sqlExceptSelect = "FROM " + PlatActivitysJoin.table + " paj "
				+ " left join " + AuthUser.table + " au on paj.app_id=au.id " ;
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("activity_id", activityId).addWhere("paj.active", 1).addOrderBy("desc","fansCount").build();
		Page<Record> pager = Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
		//排名 名次
		for (int i=0;i<pager.getList().size();i++) {
			pager.getList().get(i).set("num",i+1);
		}
		return pager;
	}
}