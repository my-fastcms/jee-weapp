package com.dbumama.market.web.controller;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.MemberRank;
import com.dbumama.market.service.api.CustomerException;
import com.dbumama.market.service.api.MemberRankService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="memberRank", viewPath="customer")
@RequiresPermissions(value="/memberRank")
public class MemberRankController extends BaseAppAdminController {

	@RPCInject
	private MemberRankService memberRankService;
	
	public void index(){
		render("member_rank_index.html");
	}
	
	public void list(){
		try {
			rendSuccessJson(memberRankService.list(getAuthUserId()));
		} catch (CustomerException e) {
			rendFailedJson(e.getMessage());
		}
	}
	
	public void add(){
		MemberRank rank = memberRankService.findById(getParaToLong("id"));
		setAttr("rank", rank);
		render("member_rank_add.html");
	}
	
	@Before(POST.class)
	public void save(Long id, String rank_name, BigDecimal first_charge, BigDecimal rank_discount, BigDecimal rank_cash_full, BigDecimal rank_cash_rward){
		MemberRank memberRank = memberRankService.findById(id);
		if(memberRank == null){
			memberRank = new MemberRank();
			memberRank.setAppId(getAuthUserId()).setCreated(new Date()).setActive(true);
		}
		
		memberRank.setRankName(rank_name).setRankDiscount(rank_discount).setRankCashFull(rank_cash_full).setRankCashRward(rank_cash_rward).setUpdated(new Date());
		
		memberRankService.saveOrUpdate(memberRank);
		
		rendSuccessJson();
	}

}
