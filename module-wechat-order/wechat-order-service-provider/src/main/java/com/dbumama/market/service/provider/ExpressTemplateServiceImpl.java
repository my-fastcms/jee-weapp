package com.dbumama.market.service.provider;

import com.dbumama.market.model.ExpressComp;
import com.dbumama.market.model.ExpressImg;
import com.dbumama.market.model.ExpressTemplate;
import com.dbumama.market.service.api.*;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.util.*;

@Bean
@RPCBean
public class ExpressTemplateServiceImpl extends WxmServiceBase<ExpressTemplate> implements ExpressTemplateService {

	@Inject
	private AuthUserService authUserService; 
	
	private static final ExpressComp expressCompdao = new ExpressComp().dao();
	private static final ExpressTemplate expressTpldao = new ExpressTemplate().dao();
	private static final ExpressImg expImgdao = new ExpressImg().dao();
	
	@Override
	public List<ExpressComp> getUserExpComps(Long sellerId) throws OrderException {
		if(sellerId == null) throw new OrderException("getUserExpComps 接口sellerId 参数必须");
		//系统所有的快递公司
		List<ExpressComp> expressComps =  expressCompdao.find("select * from " + ExpressComp.table);
		//获取用户已添加的快递模板
		List<ExpressTemplate> templates = expressTpldao.find
				("select * from " + ExpressTemplate.table + " where app_id=? and active = 1", sellerId);
		List<ExpressComp> resultComp = new ArrayList<ExpressComp>();
		for(ExpressComp expComp : expressComps){
			boolean has = false;
			for(ExpressTemplate etemp : templates){
				if(expComp.getExpKey().equals(etemp.getExpKey()))
					has = true;
			}
			if(!has) resultComp.add(expComp);
		}
		return resultComp;
	}

	@Override
	public List<ExpressImg> getExpTemplateBackImage(String expKey) throws OrderException {
		if(StrKit.isBlank(expKey)) throw new OrderException("getExpTemplateBackImage 接口参数错误");
		return expImgdao.find("select * from " + ExpressImg.table + " where exp_key=? ", expKey);
	}

	@Override
	public ExpressTemplate getUserExpTemplateByKey(String key, Long sellerId) throws OrderException {
		if(StrKit.isBlank(key) || sellerId == null) throw new OrderException("getUserExpTemplateByKey 接口参数错误");
		
		ExpressTemplate expTpl = expressTpldao.findFirst("select * from " + ExpressTemplate.table + " where "
				+ " app_id=? and exp_key = ? ", sellerId, key);
		
		if(expTpl == null){
			//用户自定义的模板为空的情况下，查询出公共模板
			expTpl = expressTpldao.findFirst("select * from " + ExpressTemplate.table + " where exp_key=? ", key);
		}
		
		if(expTpl == null)
			throw new OrderException("模板不存在");
		
		return expTpl;
	}

	@Override
	public ExpressTemplate saveTemplate(ExpTplSaveParamDto expTemplateDto) throws OrderException {
		if(expTemplateDto == null || expTemplateDto.getAuthUserId() == null 
				|| StrKit.isBlank(expTemplateDto.getExpkey()) || StrKit.isBlank(expTemplateDto.getExpname())
				|| StrKit.isBlank(expTemplateDto.getExpbgimg()))
			throw new OrderException("saveTemplate 接口参数错误");
		Long authUserId = expTemplateDto.getAuthUserId();
		String expname = expTemplateDto.getExpname();
		String expkey = expTemplateDto.getExpkey();
		String expbgimg = expTemplateDto.getExpbgimg();
		String tplcontent = expTemplateDto.getTplcontent();
		if(StrKit.isBlank(tplcontent)) throw new OrderException("模板内容为空");
		String designhtml = expTemplateDto.getDesignhtml();
		if(StrKit.isBlank(designhtml)) throw new OrderException("设计内容为空");
		int pagewidth = expTemplateDto.getPagewidth();
		int pageheight = expTemplateDto.getPageheight();
		int offsetx = expTemplateDto.getOffsetx();
		int offsety = expTemplateDto.getOffsety();
		ExpressTemplate etpl = expressTpldao.findFirst("select * from " + ExpressTemplate.table + " where exp_key=? and app_id=?", expkey, authUserId);
		if(etpl==null){
			etpl = new ExpressTemplate();
			etpl.setExpName(expname.trim());
			etpl.setExpKey(expkey);
			etpl.setAppId(authUserId);
			etpl.setActive(1);
			etpl.setCreated(new Date());
			etpl.setUpdated(new Date());
			etpl.setExpBgimg(expbgimg);
			etpl.setExpTplcontent(tplcontent);
			etpl.setExpDesignhtml(designhtml);
			etpl.setPagewidth(pagewidth);
			etpl.setPageheight(pageheight);
			etpl.setOffsetx(offsetx);
			etpl.setOffsety(offsety);
			etpl.save();
		}else {
			etpl.setActive(1);
			etpl.setExpBgimg(expbgimg);
			etpl.setExpDesignhtml(designhtml);
			etpl.setExpTplcontent(tplcontent);
			etpl.setPagewidth(pagewidth);
			etpl.setPageheight(pageheight);
			etpl.setOffsetx(offsetx);
			etpl.setOffsety(offsety);
			etpl.setUpdated(new Date());
			etpl.update();
		}
		return etpl;
	}
	

	@Override
	public void delTemplate(String expkey, Long sellerId) throws OrderException {
		if(StrKit.isBlank(expkey) || sellerId == null) throw new OrderException("删除模板参数错误");
		
		ExpressTemplate etpl = expressTpldao.findFirst("select * from " + ExpressTemplate.table + " where exp_key=? and app_id=?", expkey, sellerId);
		if(etpl != null){
			etpl.setActive(0);
			etpl.update();
		}
	}

	@Override
	public Map<String, List<PalletElementResultDto>> initPalletElement(Long appId) {
		Map<String, List<PalletElement>> elementCategoryMap = new LinkedHashMap<String, List<PalletElement>>();
		
		List<PalletElement> elements = new ArrayList<PalletElement>();
		PalletElement element = new PalletElement("寄件人姓名", "contact_name");
		elements.add(element);
		element = new PalletElement("寄件人手机", "phone");
		elements.add(element);
		element = new PalletElement("寄件人邮编", "zip_code");
		elements.add(element);
		element = new PalletElement("寄件人所在省", "province");
		elements.add(element);
		element = new PalletElement("寄件人所在市", "city");
		elements.add(element);		
		element = new PalletElement("寄件人所在区/县", "country");
		elements.add(element);
		element = new PalletElement("寄件人详细地址", "addr");
		elements.add(element);
		element = new PalletElement("寄件人公司", "seller_company");
		elements.add(element);
		element = new PalletElement("寄件人备注", "memo");
		elements.add(element);
		elementCategoryMap.put("寄件人信息", elements);

		elements = new ArrayList<PalletElement>();
		element = new PalletElement("收件人姓名", "receiverName");
		elements.add(element);
		element = new PalletElement("收件人手机", "receiverPhone");
		elements.add(element);
		element = new PalletElement("收件人邮编", "zipCode");
		elements.add(element);
		element = new PalletElement("收件人所在省", "receiverProvince");
		elements.add(element);
		element = new PalletElement("收件人所在市", "receiverCity");
		elements.add(element);
		element = new PalletElement("收件人所在区/县", "receiverCountry");
		elements.add(element);
		element = new PalletElement("收件人所在地址", "receiverAddr");
		elements.add(element);
		element = new PalletElement("买家昵称", "buyerNick");
		elements.add(element);
		element = new PalletElement("买家留言", "buyerMemo");
		elements.add(element);
		elementCategoryMap.put("收件人信息", elements);
		
		elements = new ArrayList<PalletElement>();
		element = new PalletElement("订单编号", "orderSn");
		elements.add(element);
		element = new PalletElement("下单时间", "orderCreated");
		elements.add(element);
		/*element = new PalletElement("宝贝名称", "title");
		elements.add(element);
		element = new PalletElement("商家编码", "outerIid");
		elements.add(element);
		element = new PalletElement("宝贝属性", "skuPropertiesName");
		elements.add(element);*/
		element = new PalletElement("运费", "postFee");
		elements.add(element);
		element = new PalletElement("订单总价", "totalPrice");
		elements.add(element);
		/*element = new PalletElement("代收金额", "expressAgencyFee");
		elements.add(element);
		element = new PalletElement("商品总量", "num");
		elements.add(element);*/
		elementCategoryMap.put("订单信息", elements);

		/*elements = new ArrayList<PalletElement>();
		element = new PalletElement("打印时间", "printDate");
		elements.add(element);
		element = new PalletElement("打勾", "duigou");
		elements.add(element);
		elementCategoryMap.put("系统信息", elements);*/
		
		//公众号
		elements = new ArrayList<PalletElement>();
		element = new PalletElement("小程序名称", "nick_name");
		elements.add(element);
		try {
			String qrcodeUrl = authUserService.findById(appId).getQrcodeUrl();
			element = new PalletElement("小程序二维码", "qrcode_url", "img", qrcodeUrl);
			elements.add(element);
		} catch (WxmallBaseException e) {
			e.printStackTrace();
		}
		
		element = new PalletElement("小程序账号主体", "principal_name");
		elements.add(element);
		elementCategoryMap.put("小程序信息", elements);
				
		return convert2ElementDtoMap(elementCategoryMap);
	}
	
	private Map<String, List<PalletElementResultDto>> convert2ElementDtoMap(Map<String, List<PalletElement>> elementCategoryMap){
		Map<String, List<PalletElementResultDto>> elementCategoryDtoMap = new HashMap<String, List<PalletElementResultDto>>();
		for(String elementKey : elementCategoryMap.keySet()){
			List<PalletElement> elementsInMap = elementCategoryMap.get(elementKey);
			List<PalletElementResultDto> elementDtos = new ArrayList<PalletElementResultDto>();
			for(PalletElement pe : elementsInMap){
				PalletElementResultDto peDto = new PalletElementResultDto();
				peDto.setImgSrc(pe.getImgSrc());
				peDto.setKey(pe.getKey());
				peDto.setLabel(pe.getLabel());
				peDto.setText(pe.getText());
				peDto.setType(pe.getType());
				elementDtos.add(peDto);
			}
			elementCategoryDtoMap.put(elementKey, elementDtos);
		}
		return elementCategoryDtoMap;
	}

	@Override
	public List<ExpressTemplate> getUserExpTemplate(Long appId) {
		return expressTpldao.find("select * from " + ExpressTemplate.table + " where app_id=? and active = 1", appId);
	}
	
}