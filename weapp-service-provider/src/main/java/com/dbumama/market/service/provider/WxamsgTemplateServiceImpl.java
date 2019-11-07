package com.dbumama.market.service.provider;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.WxamsgTemplate;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.WxamsgTemplateService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.enmu.MsgTplType;
import com.jfinal.aop.Inject;
import com.dbumama.weixin.api.CompWxaTemplateApi;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WxamsgTemplateServiceImpl extends WxmServiceBase<WxamsgTemplate> implements WxamsgTemplateService {

	@Inject
	private AuthUserService authUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WxamsgTemplateService#findByAppId(java.lang.Long)
	 */
	@Override
	public List<WxamsgTemplate> findByAppId(Long appId) {
		return DAO.find("select * from " + WxamsgTemplate.table + " where app_id=?", appId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WxamsgTemplateService#getTemplate(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public String getTemplateId(Long appId, Integer msgType) {
		WxamsgTemplate template = getTemplate(appId, msgType);
		return template == null ? "" : template.getTemplateId();
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WxamsgTemplateService#getTemplate(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public WxamsgTemplate getTemplate(Long appId, Integer msgType) {
		return DAO.findFirst("select * from " + WxamsgTemplate.table + " where app_id=? and template_type=? ", appId, msgType);
	}

	@Override
	public void synOnline(Long appId) {
		if(appId == null) throw new  WxmallBaseException("appId is null");
		AuthUser authUser = authUserService.findById(appId);
		if(authUser == null) throw new WxmallBaseException("authUser is null");
		
		List<WxamsgTemplate> wxamsgTpls = findByAppId(authUser.getId());
		
		if(wxamsgTpls != null && wxamsgTpls.size() > 0){
			for (WxamsgTemplate wxamsgTemplate : wxamsgTpls) {
				delMessageTemplate(authUser,wxamsgTemplate.getTemplateId(),wxamsgTemplate.getId());
			}
		}
		
		//自动添加模板消息配置
		//拼团待成团提醒
		setMessageTemplate(authUser, "AT0911", MsgTplType.grouping.ordinal(), new Integer []{1,10,3,14,4,6,8,7});
		//拼团成功消息配置
		setMessageTemplate(authUser, "AT0051", MsgTplType.groupsuccess.ordinal(), new Integer []{6,2,12,3,15,8,9,13,21,19});
		//拼团结果通知消息设置(失败通知)
		setMessageTemplate(authUser, "AT1814", MsgTplType.groupfail.ordinal(), new Integer []{1,2,3,4});
		//支付成功消息配置
		setMessageTemplate(authUser, "AT0009", MsgTplType.paied.ordinal(), new Integer []{10,33,3,6,9,7,8,34,32});
		//支付失败消息配置
		setMessageTemplate(authUser, "AT0010", MsgTplType.payfail.ordinal(), new Integer []{1,4,18,19,21,23});
		//订单待支付消息配置（开启催付，就必须关闭订单关闭消息提醒）
		setMessageTemplate(authUser, "AT0525", MsgTplType.unpay.ordinal(), new Integer []{1,13,4,9,20});
		//订单创建成功消息配置（取消）
		setMessageTemplate(authUser, "AT0210", MsgTplType.created.ordinal(), new Integer []{1,2,40,49,10,5});
		//订单超时关闭消息提醒配置（开启订单关闭消息提醒，就必须关闭订单催付消息提醒）
		setMessageTemplate(authUser, "AT1410", MsgTplType.closed.ordinal(), new Integer []{3,4,6,10,11,8});
		//订单发货消息提醒配置
		setMessageTemplate(authUser, "AT0007", MsgTplType.shiped.ordinal(), new Integer []{7,77,3,2,23,26});
		//预约课程成功消息配置
		setMessageTemplate(authUser, "AT0060", MsgTplType.appointcoursesuccess.ordinal(), new Integer []{1,11});
		//预约课程失败消息配置
		setMessageTemplate(authUser, "AT2372", MsgTplType.appointcoursefail.ordinal(), new Integer []{1,2,4});
		//预约考试成功消息配置
		setMessageTemplate(authUser, "AT2095", MsgTplType.appointexamsuccess.ordinal(), new Integer []{1,3});
		//预约考试失败消息配置
		setMessageTemplate(authUser, "AT0737", MsgTplType.appointexamfail.ordinal(), new Integer []{1,3});
		//销售员推广成功通知
		setMessageTemplate(authUser, "AT0563", MsgTplType.agentgeneralizesuccess.ordinal(), new Integer []{3,2,1});
		//获得未结算佣金通知
		setMessageTemplate(authUser, "AT0005", MsgTplType.getcommission.ordinal(), new Integer []{1,3,29,25});
		//砍价进度通知
		setMessageTemplate(authUser, "AT1179", MsgTplType.bargainschedule.ordinal(), new Integer []{1,2});
		
	}
	
	private void setMessageTemplate(AuthUser authUser, String id, Integer type, Integer [] indexs) throws WxmallBaseException{
		JSONObject groupingJson = new JSONObject();
		groupingJson.put("id", id);
		JSONArray groupingArray = new JSONArray();
		for(int i=0; i<indexs.length; i++){
			groupingArray.add(indexs[i]);
		}
		groupingJson.put("keyword_id_list", groupingArray);
		ApiResult groupingRes = CompWxaTemplateApi.addTemplate(authUserService.getAccessToken(authUser), groupingJson.toString());
		if(groupingRes.isSucceed()){ 
			WxamsgTemplate wxamsgTpl = new WxamsgTemplate();
			wxamsgTpl.setAppId(authUser.getId()).setTemplateKuId(id).setTemplateId(groupingRes.get("template_id")).setTemplateType(type).setActive(true);
			save(wxamsgTpl);
		}
	}
	
	private void delMessageTemplate(AuthUser authUser, String template_id, Long id) throws WxmallBaseException{
		JSONObject groupingJson = new JSONObject();
		groupingJson.put("template_id", template_id);

		ApiResult groupingRes = CompWxaTemplateApi.delTemplate(authUserService.getAccessToken(authUser), groupingJson.toString());
		if(groupingRes.isSucceed()){ 
			delete(findById(id));
		}
	}

}