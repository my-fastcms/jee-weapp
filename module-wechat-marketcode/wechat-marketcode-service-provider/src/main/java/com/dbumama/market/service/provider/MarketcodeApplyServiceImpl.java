package com.dbumama.market.service.provider;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.dbumama.market.base.ApiResult;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.MarketcodeApply;
import com.dbumama.market.model.MarketcodeCodeactive;
import com.dbumama.market.model.WeappTemplate;
import com.dbumama.market.service.MarketcodeService;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.AuthUserTemplateService;
import com.dbumama.market.service.api.MarketcodeApplyService;
import com.dbumama.market.service.api.MarketcodeCodeactiveService;
import com.dbumama.market.service.api.MarketcodeException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.dbumama.weixin.api.CompMarketcodeApi;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.utils.ClassUtil;

@Bean
@RPCBean
public class MarketcodeApplyServiceImpl extends WxmServiceBase<MarketcodeApply> implements MarketcodeApplyService {

	final static String marketCodeKey = "Wbv39OzxWcojMrj+";
	
	@Inject
	private AuthUserService authUserService;
	@Inject
	private AuthUserTemplateService authUserTemplateService;
	@Inject
	private MarketcodeCodeactiveService marketcodeCodeactiveService;
	
	public static final Object lockobj = new Object();
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#apply(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void apply(Long appId, Long codeNum) throws MarketcodeException {
		if(appId ==null || codeNum == null) throw new MarketcodeException("appId is null or codeNum is null");
		
		if(codeNum % 10000 !=0){
			throw new MarketcodeException("申请码数量必须是10000的整数倍");
		} 
		
		AuthUser authUser = authUserService.findById(appId);
		
		final String accessToken = authUserService.getAccessToken(authUser);
		
		final String isv_application_id = getTradeNo();
		
		ApiResult apiResult = CompMarketcodeApi.applyCode(accessToken, codeNum, isv_application_id);
		if(!apiResult.isSucceed() || apiResult.getInt("application_id") == null){
			throw new MarketcodeException("error code:" + apiResult.getErrorCode() + ",error msg:" + apiResult.getErrorMsg());
		}
		
		MarketcodeApply entry = new MarketcodeApply();
    	entry.setCreated(new Date());
    	entry.setAppId(appId);
    	entry.setActive(true);
        entry.setIsvApplicationId(isv_application_id);
        entry.setCodeCount(codeNum);
        entry.setApplicationId(String.valueOf(apiResult.getInt("application_id")));
        entry.setUpdated(new Date());
        save(entry);
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#applyStatus(java.lang.Long, java.lang.String)
	 */
	@Override
	public ApiResult applyStatus(Long appId, String applicationId, String isvApplicationId) {
		AuthUser authUser = authUserService.findById(appId);
		final String accessToken = authUserService.getAccessToken(authUser);
		return CompMarketcodeApi.applyCodeQuery(accessToken, applicationId, isvApplicationId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#downloadCode(java.lang.Long, java.lang.String, java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<String> downloadCode(Long id) throws MarketcodeException {
		
		MarketcodeApply entry = findById(id);
		
		if(entry == null) throw new MarketcodeException("请先申请生成二维码");
		
		AuthUser authUser = authUserService.findById(entry.getAppId());
		
		if(authUser == null){
			throw new MarketcodeException("公众号信息异常");
		}
		
		final String accessToken = authUserService.getAccessToken(authUser);
		
		ApiResult apiResult = applyStatus(entry.getAppId(), entry.getApplicationId(), entry.getIsvApplicationId());
    	if(!apiResult.isSucceed() && StrKit.isBlank(apiResult.getStr("status"))){
    		throw new MarketcodeException("error code:" + apiResult.getErrorCode() + ",error msg:" + apiResult.getErrorMsg());
    	}
    	
    	if(!"FINISH".equals(apiResult.getStr("status"))){
			throw new MarketcodeException("二维码还未生成，请耐心等待...");
		}
    	
    	List<String> dataList = new ArrayList<String>();
    	
    	logger.info("=====================apiResult:" + apiResult.getJson());
    	
    	JSONArray code_generate_list = apiResult.get("code_generate_list");
		for(int i=0;i<code_generate_list.size();i++){
			JSONObject json = code_generate_list.getJSONObject(i);
			Long codeStart = json.getLong("code_start");
			Long codeEnd = json.getLong("code_end");
			ApiResult codeResult = CompMarketcodeApi.applyCodeDownload(accessToken, entry.getApplicationId(), codeStart, codeEnd);
			if(!codeResult.isSucceed()){
				throw new MarketcodeException("code:" + codeResult.getErrorCode() + ",error:" + codeResult.getErrorMsg());
			}
			
			try {
				final String buffer = codeResult.getStr("buffer");
				//1. base 64 decode
				Base64 base64 = new Base64();
				byte [] codeContent = base64.decode(buffer);
				byte [] ivbyte = marketCodeKey.getBytes("utf-8");
				byte [] sessionKeyByte = marketCodeKey.getBytes("utf-8");
				
				Security.addProvider(new BouncyCastleProvider());
				
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
				Key sKeySpec = new SecretKeySpec(sessionKeyByte, "AES");

				AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
		        params.init(new IvParameterSpec(ivbyte));
	            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);// 初始化
	            byte[] resultByte = cipher.doFinal(codeContent);
	            if (null != resultByte && resultByte.length > 0) {
	            	final String codeStr = new String(resultByte, "UTF-8");//最终解密出来的二维码数据
	            	dataList.add(codeStr);
	            }
			}  catch (Exception e) {
				throw new MarketcodeException(e.getMessage());
			}
		}
    	return dataList;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#findByAppApplicationId(java.lang.Long, java.lang.String)
	 */
	@Override
	public MarketcodeApply findByAppApplicationId(Long appId, String applicationId) {
		return DAO.findFirst("select * from " +MarketcodeApply.table + " where app_id=? and application_id=? ", appId, applicationId);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#ticket2code(java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public ApiResult ticket2code(Long appId, String openid, String codeTicket) throws MarketcodeException {
		final AuthUser weapp = authUserService.findById(appId);//此处传递小程序id
		final MarketcodeCodeactive marketcodeCodeActive = marketcodeCodeactiveService.findByWxaAppId(weapp.getAppId());
		if(marketcodeCodeActive == null) throw new MarketcodeException("该小程序未找到激活信息");
		final AuthUser authUser = authUserService.findById(marketcodeCodeActive.getAppId());
		final String accessToken = authUserService.getAccessToken(authUser);
		return CompMarketcodeApi.ticketToCode(accessToken, openid, codeTicket);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#process(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Before(Tx.class)
	public void process(String isvApplicationId, String code, Integer applicationId, String wxaAppId, String openid) throws MarketcodeException {
		if(StrKit.isBlank(code) || applicationId == null || StrKit.isBlank(wxaAppId) || StrKit.isBlank(openid))
			throw new MarketcodeException("扫码小程序缺少必要参数或许是非法请求");
		
		//获取小程序模板
		AuthUser authUser = authUserService.findByAppId(wxaAppId);
		if(authUser == null) throw new MarketcodeException("系统异常，授权小程序不存在或者非法请求");
		
		WeappTemplate weappTemplate = authUserTemplateService.findWeappTemplate(authUser.getId());
		if(weappTemplate == null) throw new MarketcodeException("系统异常，授权小程序未设置模板,或者模板不存在");
		
		if(weappTemplate.getActive() == null || weappTemplate.getActive() == false)
			throw new MarketcodeException("小程序模板已经下线");
		
		if(StrKit.isBlank(weappTemplate.getProcessClass()))
			throw new MarketcodeException("小程序模板未注册处理类");
		
		try {
			Object temp = ClassUtil.newInstance(Class.forName(weappTemplate.getProcessClass()));
			MarketcodeService marketcodeService = (MarketcodeService) temp;
			marketcodeService.doProcess(wxaAppId, applicationId, isvApplicationId, openid, code);
		} catch (Exception e) {
			throw new MarketcodeException(e.getMessage());
		}
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MarketcodeApplyService#findByIsvAppApplicationId(java.lang.String, java.lang.String)
	 */
	@Override
	public MarketcodeApply findByIsvAppApplicationId(String isvApplicationId, Integer applicationId) {
		return DAO.findFirst("select * from " + MarketcodeApply.table + " where isv_application_id=? and application_id=? ", isvApplicationId, applicationId);
	}

}