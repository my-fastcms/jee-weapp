package com.dbumama.market.service.provider;

import com.dbumama.market.model.*;
import com.dbumama.market.service.api.InvitecodeRuleService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.dbumama.market.utils.RC4;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.Date;

@Bean
@RPCBean
public class InvitecodeRuleServiceImpl extends WxmServiceBase<InvitecodeRule> implements InvitecodeRuleService {

	private static final InvitecodeRule inviteCodeRuledao = new InvitecodeRule().dao();
	private static final SellerUser sellerUserdao = new SellerUser().dao();
	private static final SellerMission sellerMissiondao = new SellerMission().dao();
	private static final SellerCashRcd sellerCashRcddao = new SellerCashRcd().dao();
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#find()
	 */
	@Override
	public InvitecodeRule findRule() {
		return inviteCodeRuledao.findFirst("select * from " + InvitecodeRule.table);
	}


	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#genInCode()
	 */
	@Override
	public String genInCode(SellerUser sellerUser) throws Exception {
		if(sellerUser == null) throw new Exception("seller is null");
		
		if(StrKit.notBlank(sellerUser.getMyInviteCode()))
			throw new Exception("已经生成过邀请码，不可重复生成");
		
		String incode = RC4.encry_RC4_string(String.format("%07d", sellerUser.getId()), getUUID());
		
		sellerUser.setMyInviteCode(incode);
		sellerUser.setGenCodeTime(new Date());
		sellerUser.update();
		
		return incode;
	}


	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#getCodeCash(java.lang.Long)
	 */
	@Override
	public BigDecimal getCodeCash(Long sellerId) throws WxmallBaseException {
		if(sellerId == null) throw new WxmallBaseException("用户信息异常");
		return Db.queryBigDecimal("select sum(seller_mission) from " + SellerMissionRcd.table + " where seller_id=? ", sellerId);
	}
	

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#getCodeCangetCash(java.lang.Long)
	 */
	@Override
	public BigDecimal getCodeCangetCash(Long sellerId) {
		SellerMission sm = sellerMissiondao.findFirst("select * from " + SellerMission.table + " where seller_id=? ", sellerId);
		return sm == null ? new BigDecimal(0) : sm.getMission();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#applycash(java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public void applycash(Long sellerId, String tradePwd, String wantCash, String cashAccount) throws WxmallBaseException {
		if(sellerId == null) throw new WxmallBaseException("sellerId is null");
		if(StrKit.isBlank(tradePwd)) throw new WxmallBaseException("交易密码不能为空");
		if(StrKit.isBlank(wantCash)) throw new WxmallBaseException("提现金额不能为空");
		if(StrKit.isBlank(cashAccount)) throw new WxmallBaseException("收款账户不能为空");
		SellerUser seller = sellerUserdao.findById(sellerId);
		if(seller == null) throw new WxmallBaseException("账户不存在");
		
		if(seller.getTradePassword() == null || !seller.getTradePassword().equals(DigestUtils.md5Hex(tradePwd)))
			throw new WxmallBaseException("交易密码错误");
		
		SellerMission sellerMission = sellerMissiondao.findFirst("select * from " + SellerMission.table + " where seller_id=? ", sellerId);
		if(sellerMission == null 
				|| sellerMission.getMission() == null
				|| sellerMission.getMission().compareTo(new BigDecimal(0)) !=1 
				|| sellerMission.getMission().compareTo(new BigDecimal(wantCash)) == -1){
			throw new WxmallBaseException("账户余额不足够提现");
		}
		
		//更新用户账户余额
		sellerMission.setMission(sellerMission.getMission().subtract(new BigDecimal(wantCash)).setScale(2, BigDecimal.ROUND_HALF_UP));
		sellerMission.update();
		
		//到此插入一条提现申请记录
		SellerCashRcd scrcd = new SellerCashRcd();
		scrcd.setSellerId(sellerId);
		scrcd.setWantCash(new BigDecimal(wantCash));
		scrcd.setCashAccount(cashAccount);
		scrcd.setCashType(1);
		scrcd.setStatus(1);		//提现审核中
		scrcd.setMemo("");
		scrcd.setActive(true);
		scrcd.setCreated(new Date());
		scrcd.setUpdated(new Date());
		scrcd.save();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#getPlatIncodeUser(java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@Override
	public Page<Record> getPlatIncodeUser(Integer pageNo, Integer pageSize, String phone) {
		String select = " select sus.id,sus.phone,sus.my_invite_code,sus.gen_code_time, sic.share_count, smrcd.total_mission ";
		String sqlExceptSelect = "from t_seller_user sus "
			+" LEFT JOIN "
			+" (select count(*) as share_count, su.share_invite_code from t_seller_user su GROUP BY su.share_invite_code ) sic "
			+" on sus.my_invite_code = sic.share_invite_code "
			+" LEFT JOIN "
			+" (select SUM(smr.seller_mission) as total_mission, smr.seller_id from t_seller_mission_rcd smr GROUP BY smr.seller_id) smrcd "
			+" on sus.id = smrcd.seller_id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhereNotNull("sus.my_invite_code").addWhere("sus.phone", phone)
		.addOrderBy("desc", "sus.gen_code_time").build();
		
		return Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}


	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#getPlatCashRcd(java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer)
	 */
	@Override
	public Page<Record> getPlatCashRcd(Integer pageNo, Integer pageSize, String phone, Integer status) {
		String select = "select s.phone, scr.id as scr_id, scr.want_cash, scr.cash_account, scr.cash_type, scr.status, scr.memo, scr.created, scr.updated ";
		String sqlExceptSelect = " from " + SellerCashRcd.table + " scr "
				+ " left join " + SellerUser.table + " s on scr.seller_id=s.id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("scr.status", status).addWhere("s.phone", phone).addOrderBy("desc", "scr.created").build();
		
		return Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#getUserCashRcd(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<Record> getUserCashRcd(Integer pageNo, Integer pageSize, Long sellerId, Integer status) {
		String select = "select s.phone, scr.id as scr_id, scr.want_cash, scr.cash_account, scr.cash_type, scr.status, scr.memo, scr.created, scr.updated ";
		String sqlExceptSelect = " from " + SellerCashRcd.table + " scr "
				+ " left join " + SellerUser.table + " s on scr.seller_id=s.id ";
		
		QueryHelper helper = new QueryHelper(select, sqlExceptSelect);
		helper.addWhere("scr.status", status).addWhere("scr.seller_id", sellerId).addOrderBy("desc", "scr.created").build();
		return Db.paginate(pageNo, pageSize, helper.getSelect(), helper.getSqlExceptSelect(), helper.getParams());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#confirmCash(java.lang.Long)
	 */
	@Override
	public void confirmCash(Long cashId) throws WxmallBaseException {
		if(cashId == null) throw new WxmallBaseException("cashId is null");
		SellerCashRcd scr = sellerCashRcddao.findById(cashId);
		if(scr == null) throw new WxmallBaseException("提现记录不存在");
		if(scr.getStatus() == null || scr.getStatus() !=1) throw new WxmallBaseException("该状态不可打款");
		
		scr.setStatus(2);
		scr.setUpdated(new Date());
		scr.update();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.incode.InvitecodeService#cancelCash(java.lang.Long, java.lang.String)
	 */
	@Override
	public void cancelCash(Long cashId, String reason) throws WxmallBaseException {
		if(cashId == null) throw new WxmallBaseException("cashId is null");
		if(StrKit.isBlank(reason)) throw new WxmallBaseException("请填写取消打款的原因");
		SellerCashRcd scr = sellerCashRcddao.findById(cashId);
		if(scr == null) throw new WxmallBaseException("提现记录不存在");
		
		if(scr.getStatus() == null || scr.getStatus()!=1) throw new WxmallBaseException("该状态不可取消打款");
		
		//取消打款后，需要把提现金额打回给用户账户
		SellerMission sellerMission = sellerMissiondao.findFirst("select * from " + SellerMission.table + " where seller_id=? ", scr.getSellerId());
		sellerMission.setMission(sellerMission.getMission().add(scr.getWantCash()).setScale(2, BigDecimal.ROUND_HALF_UP));
		sellerMission.update();
		
		scr.setStatus(3);
		scr.setUpdated(new Date());
		scr.setMemo(reason);
		scr.update();
	}
	
}