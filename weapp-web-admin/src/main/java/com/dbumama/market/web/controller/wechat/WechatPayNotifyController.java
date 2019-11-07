/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.web.controller.wechat;

import com.dbumama.market.model.AuthCert;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.BuyerUser;
import com.dbumama.market.service.api.*;
import com.dbumama.market.utils.SignKit;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import io.jboot.components.limiter.annotation.EnableLimit;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 微信支付回调
 * @author wangjun
 * 2019年1月23日
 */
@RequestMapping(value = "pay/notify")
public class WechatPayNotifyController extends Controller{

	//返回成功的xml给微信
	static final String TO_RES_WEIXIN = "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
			+ "<return_msg><![CDATA[OK]]></return_msg></xml>";
	
	//正在处理的用户订单
	public static Set<String> userTradeSet = new HashSet<String>();
	
	@RPCInject
	private PayService payService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private BuyerUserService buyerUserService;
	@RPCInject
	private AuthCertService authCertService;
	
	public Logger log = Logger.getLogger(getClass());
	
	public void renderDefault(){
		renderText(TO_RES_WEIXIN, "text/xml");
	}
	
	@Before(POST.class)
	@Clear
	@EnableLimit(rate=1, fallback="renderDefault")
	public void index(){
		log.debug("===================支付成功，成功回调");
		String resultXml = getRawData();
		if(StrKit.isBlank(resultXml)){
			renderNull();
			return;
		}
		SAXReader saxReader = new SAXReader();
		try {
			saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		} catch (SAXException e) {
			e.printStackTrace();
			renderNull();
			return;
		}
		
		InputSource source = new InputSource(new StringReader(resultXml));
		source.setEncoding(JFinal.me().getConstants().getEncoding());

		Document doc = null;
		try {
			doc = saxReader.read(source);
		} catch (DocumentException e) {
			renderNull();
			return;
		}
		Element root = doc.getRootElement();
		//校验签名
		TreeMap<String, Object> params = new TreeMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<Element> elements = root.elements();
		String authAppId = ""; 
		for(Element e : elements){
			params.put(e.getName(), e.getTextTrim());
			if("appid".equals(e.getName())){
				authAppId = e.getTextTrim();
			}
		}
		
		AuthUser authUser = authUserService.getAuthUserByAppId(authAppId);
		if(authUser == null){
			log.error("[" + authAppId + "]授权公众号不存在");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		AuthCert use = authCertService.findUse(authUser.getAppId());
		if(use == null){
			use = authCertService.findDefault();
		}
		if(use.getPayMchId() == null || use.getPaySecretKey() ==null || use.getCertFile() == null){
			log.error("[" + authAppId + "]公众号支付配置设置不全");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		String sign = SignKit.sign(params, use.getPaySecretKey());
		String tenpaySign = root.elementText("sign").toUpperCase();
		if(StrKit.isBlank(sign) 
				|| StrKit.isBlank(tenpaySign) 
				|| !sign.equals(tenpaySign)){
			log.error("微信支付异步通知签名错误");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		log.debug("成功回调，签名正确");
		
		//根据out_trade_no 判断订单支付来源
		String tradeNo = (String) params.get("out_trade_no");
		log.debug("tradeNo:" + tradeNo);
		String openid = (String) params.get("openid");
		BuyerUser user = buyerUserService.findByOpenId(openid);
		if(user==null){
			log.error("["+ openid + "] user is null");
			renderText(TO_RES_WEIXIN, "text/xml");
			return;
		}
		
		synchronized (userTradeSet) {
			if(userTradeSet.contains(tradeNo+user.getId())){
				//正在处理的订单数据，本次不处理
				renderText(TO_RES_WEIXIN, "text/xml");
				return;
			}
			userTradeSet.add(tradeNo+user.getId());
		}
		try {
			//==========================业务处理
			if(StrKit.notBlank(tradeNo) && tradeNo.startsWith("l-")){
				//说明是从抽奖充值支付过来的
				payService.resultLotteryCallback(user, params);
			}else if(StrKit.notBlank(tradeNo) && tradeNo.startsWith("c-")){
				payService.resultMemberCardCallback(user, params);				
			}else{
				//回调更新订单状态
				payService.resultOrderCallback(user, params);
			}
		} catch (PayException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			//处理成功后，移除
			userTradeSet.remove(tradeNo+user.getId());
		}
		
		renderText(TO_RES_WEIXIN, "text/xml");
	}
	
}
