package com.dbumama.market.web.controller;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.model.Area;
import com.dbumama.market.model.BuyerReceiver;
import com.dbumama.market.model.Shop;
import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.service.api.BuyerReceiverService;
import com.dbumama.market.service.api.BuyerReceiverSubmitParamDto;
import com.dbumama.market.service.api.ShopService;
import com.dbumama.market.web.core.controller.BaseApiController;
import com.dbumama.market.web.core.interceptor.ApiSessionInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;
@RequestMapping(value = "receiver")
public class ReceiverController extends BaseApiController{
	
	@RPCInject
	private AreaService areaService;
	@RPCInject
	private BuyerReceiverService receiverService;
	@RPCInject
	private ShopService shopService;
	
	@Before(ApiSessionInterceptor.class)
	public void list(){
		List<BuyerReceiver> receivers = receiverService.getBuyerReceiver(getBuyerId());
		rendSuccessJson(receivers);
	}
	
	@Before(ApiSessionInterceptor.class)
    public void edit() {
        BuyerReceiver receiver = receiverService.findById(getJSONParaToLong("id"));
        rendSuccessJson(receiver);
    }
	
	// 收货地址添加
    @Before({POST.class, ApiSessionInterceptor.class})
	public void save() {
        try {
        	final String address = getJSONPara("address");
        	final String name = getJSONPara("name");
        	final String phone = getJSONPara("phone");
        	final String area_id = getJSONPara("area_id");
        	final String province = getJSONPara("province");
        	final String city = getJSONPara("city");
        	final String district = "请选择区".equals(getJSONPara("district")) || StrKit.isBlank(getJSONPara("district")) ? "" : getJSONPara("district");
        	final String is_default = getJSONPara("is_default");
        	Long receiverId = getJSONParaToLong("receiverId");
        	BuyerReceiverSubmitParamDto submitParam = new BuyerReceiverSubmitParamDto(receiverId, getBuyerId(), address, name, phone, area_id, province, city, district, is_default);
            rendSuccessJson(receiverService.save(submitParam));
        } catch (Exception e) {
            log.error("get receiver error", e);
            rendFailedJson(e.getMessage());
        }
    }
	
   /**
	 * 地区
	 */
	public void area() {
		Long parentId = getJSONParaToLong("parentId");
		List<Area> areas = new ArrayList<Area>();
		Area parent = areaService.findById(parentId);
		if (parent != null) {
			areas =areaService.getChildren(parent.getId());
		} else {
			areas = areaService.findRoots();
		}
		//Map<Long, String> options = new HashMap<Long, String>();
		JSONArray array=new JSONArray();
		for (Area area : areas) {
			//options.put(area.getId(), area.getName());
			JSONObject result = new JSONObject();
			result.put("id", area.getId());
			result.put("name", area.getName());
			array.add(result);
		}
		rendSuccessJson(array);
	}
	
	//获取门店地址
	public void getShop(){
		List<Shop> list = shopService.getShopByAppId(getAuthUserId());
		rendSuccessJson(list);
	}
	
}
