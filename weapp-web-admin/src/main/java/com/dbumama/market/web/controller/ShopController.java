package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.base.ApiResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.dbumama.market.model.Area;
import com.dbumama.market.model.ImageSowing;
import com.dbumama.market.model.SellerAddr;
import com.dbumama.market.model.Shop;
import com.dbumama.market.model.ShopEmployee;
import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.ImageSowingService;
import com.dbumama.market.service.api.OrderException;
import com.dbumama.market.service.api.SellerAddrService;
import com.dbumama.market.service.api.ShopEmployeeService;
import com.dbumama.market.service.api.ShopService;
import com.dbumama.market.web.core.controller.BaseAppAdminController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.dbumama.weixin.api.CompWxaCodeApi;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;

@RequestMapping(value="shop")
@RequiresPermissions(value="/shop")
public class ShopController extends BaseAppAdminController {

	@RPCInject
	private SellerAddrService sellerAddrService;
	@RPCInject
	private AreaService areaService;
	@RPCInject
	private AuthUserService authUserService;
	@RPCInject
	private ShopService shopService;
	@RPCInject
	private ShopEmployeeService shopEmployeeService;
	@RPCInject
	private ImageSowingService imageSowingService;
	
	public void index(){
		render("/shop/shop_index.html");
	}
	
	public void employee(){
		render("/shop/employee_index.html");
	}
	
	public void employeeList(){
		rendSuccessJson(shopEmployeeService.list(getAuthUserId(), getPageNo(), getPageSize(), getPara("sName"),getPara("eName")));
	}
	
	public void shopList(){
		rendSuccessJson(shopService.getShopByAppId(getAuthUserId()));
	}
	
	public void list(){
		rendSuccessJson(shopService.list(getAuthUserId(), getPageNo(), getPageSize(), getParaToInt("active")));
	}
	
	public void edit(){
		Shop shop = shopService.findById(getParaToLong("shopId"));
		if(shop != null){
			Area area = areaService.findById(shop.getAreaId());
			if(area != null)
				setAttr("areaPath", area.getTreePath());
		}
		setAttr("shop", shop);
		render("/shop/shop_edit.html");
	}
	
	public void employeeEdit(){
		ShopEmployee employee = shopEmployeeService.findById(getParaToLong("employeeId"));
		setAttr("employee", employee);
		render("/shop/employee_edit.html");
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "empl_name", message = "请输入员工名称"),
        @Form(name = "phone", message = "请输入手机电话"),
        @Form(name = "shop_id", message ="请选择所属店铺")
	})
	public void saveEmployee(final Long id,  final Long shop_id,final String empl_name,final String phone){
		ShopEmployee employee = new ShopEmployee();
		employee.setId(id).setShopId(shop_id).setEmplName(empl_name).setPhone(phone)
		.setAppId(getAuthUserId());
		
		try {
			shopEmployeeService.saveEmployee(employee);
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson("保存失败");
		}
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "shop_name", message = "请输入店铺名称"),
        @Form(name = "shop_logo", message = "请输入店铺LOGO"),
        @Form(name = "shop_contact", message = "请输入店长名字"),
        @Form(name = "shop_contact_phone", message = "请输入店长电话"),
        @Form(name = "shop_sign", message = "请输入店招"),
        @Form(name = "areaId", message = "请选择门店区域"),
        @Form(name = "shop_address", message ="请填写门店详细地址")
	})
	public void saveShop(final Long id, final Long areaId, final String shop_name, final String shop_logo, final String shop_sign,
			final String shop_contact, final String shop_contact_phone, final String shop_address, final String lat, final String lng, final String photos, final Integer active){
		Shop shop = shopService.findById(id);
		if(shop == null){
			shop = new Shop ();
			shop.setAppId(getAuthUserId());
		}
		shop.setShopName(shop_name).setShopLogo(shop_logo).setShopSign(shop_sign).setShopContact(shop_contact).setShopContactPhone(shop_contact_phone)
		.setShopAddress(shop_address).setAreaId(areaId).setLat(lat).setLng(lng).setPhotos(photos).setActive(active == null || active==1 ? true : false);
		
		try {
			shopService.saveOrUpdate(shop);
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson("保存失败");
		}
		
	}
	
	public void sendaddr(){
		SellerAddr sellerAddr = sellerAddrService.getSendAddr(getSellerId());
		if(sellerAddr != null){
			Area area = areaService.findById(sellerAddr.getAreaId());
			if(area != null)
				setAttr("areaPath", area.getTreePath());
		}
		setAttr("sendAddr", sellerAddr);
		render("/shop/send_addr.html");
	}
	
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "contactName", message = "请输入联系人姓名"),
        @Form(name = "areaId", message = "请选择所在地区"),
        @Form(name = "addr", message = "请输入详细联系地址"),
        @Form(name = "phone", message = "请输入联系电话")
	})
	public void saveaddr(Long addrId, Long areaId, String contactName, String city, String country, String province, String addr, String memo, String phone, String sellerCompany, String zipCode){
		
		SellerAddr sellerAddr = sellerAddrService.findById(addrId);
		if(sellerAddr == null){
			sellerAddr = new SellerAddr();
			sellerAddr.setSellerId(getSellerId());
		}
		
		sellerAddr.setAreaId(areaId).setContactName(contactName).setCity(city).setCountry(country).setProvince(province)
		.setAddr(addr).setMemo(memo).setPhone(phone).setSellerCompany(sellerCompany).setZipCode(zipCode);
		
		try {
			sellerAddrService.saveOrUpdate(sellerAddr);	
			rendSuccessJson();
		} catch (OrderException e) {
			rendFailedJson(e.getMessage());
		}
		
	}
	
	/**轮播图*/
	public void sowingimage(){
		render("/shop/sowingimage_index.html");
	}
	/**异步获取轮播图列表信息*/
	public void sowinglist(){
		try {
			Long authUserId = getAuthUserId();
			rendSuccessJson(imageSowingService.list(authUserId, getPageNo(), getPageSize()));
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
	}
	/**跳转修改页面*/
	@SuppressWarnings("unchecked")
	public void sowingedit(){
		//===
		ApiResult getPageRes = CompWxaCodeApi.getPage(authUserService.getAccessToken(getAuthUser()));
		List<String> pageList = getPageRes.getList("page_list");
		setAttr("pageList", pageList);
		//===
		ImageSowing imageSowing = imageSowingService.findById(getParaToLong("sowingId"));
		
		Long appId = getAuthUserId();
		//区别公众号和小程序
		setAttr("appid",appId.intValue());
		setAttr("sowing",imageSowing);
		render("/shop/sowingimg_edit.html");
	}
	/**保存和修改*/
	@Before(POST.class)
	@EmptyValidate({
        @Form(name = "sowing_img", message = "请输入店铺轮播图")
	})
	public void saveSowing(Long id, String sowing_img,String sowing_url){
		Long appId = getAuthUserId();
		try{
			imageSowingService.mysaveOrUpdate(id,appId,sowing_img,sowing_url);
			rendSuccessJson();
		}catch (Exception e){
			rendFailedJson(e.getMessage());
		}
	}
	/**删除对应的轮播图*/
	public void delSowing(Long id){
		try {
			imageSowingService.delByIdAndAppid(id,getAuthUserId());
			rendSuccessJson();
		} catch (Exception e) {
			rendFailedJson(e.getMessage());
		}
		
	}

}
