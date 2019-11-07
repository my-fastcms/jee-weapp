package com.dbumama.market.web.controller;

import java.util.List;

import com.dbumama.market.model.BuyerReceiver;
import com.dbumama.market.service.api.AreaService;
import com.dbumama.market.service.api.BuyerReceiverService;
import com.dbumama.market.service.api.BuyerReceiverSubmitParamDto;
import com.dbumama.market.web.core.controller.BaseMobileController;

import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "receiver")
public class ReceiverController extends BaseMobileController{
	@RPCInject
	private AreaService areaService;
	@RPCInject
	private BuyerReceiverService receiverService;
	
	public void index(){
		List<BuyerReceiver> receivers = receiverService.getBuyerReceiver(getBuyerId());
		setAttr("receiverList", receivers);
		render("/receiver/index.html");
	}
	
	public void view(){
		List<BuyerReceiver> receivers = receiverService.getBuyerReceiver(getBuyerId());
		setAttr("receiverList", receivers);
		render("/receiver/view.html");
	}
	
	public void add() {
        render("/receiver/receiver-input.html");
    }

    public void edit() {
        BuyerReceiver receiver = receiverService.findById(getParaToLong("id"));
        setAttr("receiver", receiver);
        render("/receiver/receiver-input.html");
    }

    //地址管理-添加
	public void addView() {
        render("/receiver/add_view.html");
    }
	
	//地址管理-修改
    public void editView() {
        BuyerReceiver receiver = receiverService.findById(getParaToLong("id"));
        setAttr("receiver", receiver);
        render("/receiver/add_view.html");
    }
    
    // 收货地址添加
    public void save() {
        try {
        	final String address = getPara("address");
        	final String name = getPara("name");
        	final String phone = getPara("phone");
        	final String area_id = getPara("area_id");
        	final String province = getPara("province");
        	final String city = getPara("city");
        	final String district = getPara("district");
        	final String is_default = getPara("is_default");
        	final Long receiverId = getParaToLong("id");
            
        	BuyerReceiverSubmitParamDto submitParam = new BuyerReceiverSubmitParamDto(receiverId, getBuyerId(), address, name, phone, area_id, province, city, district, is_default);
            rendSuccessJson(receiverService.save(submitParam));
        } catch (Exception e) {
            log.error("get receiver error", e);
            rendFailedJson("系统500错误");
        }
    }
    
	public void delete() {
        Long id = getParaToLong("id");
        if(id == null){
        	rendFailedJson("请选择要删除的项");
        	return;
        }
        BuyerReceiver receiver = receiverService.findById(getParaToLong("id"));
        if(receiver != null){
        	receiverService.delete(receiver);
        }
        rendSuccessJson();
    }
    
    /**
	 * 地区
	 */
	public void area() {
		renderJson(areaService.list(getParaToLong("parentId")));
	}
}
