package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.WeappStyle;
import com.dbumama.market.model.WeappStyleTabbarItem;
import com.dbumama.market.service.api.AuthUserStyleService;
import com.dbumama.market.service.api.WeappStyleResultDto;
import com.dbumama.market.service.api.WeappStyleService;
import com.dbumama.market.service.api.WeappStyleTabbarItemResultDto;
import com.dbumama.market.service.api.WeappStyleTabbarItemService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeappStyleServiceImpl extends WxmServiceBase<WeappStyle> implements WeappStyleService {

	@Inject
	private WeappStyleTabbarItemService weappStyleTabbarItemService;
	@Inject
	private AuthUserStyleService authUserStyleService;
	
//	private static final AuthUserStyle authUserStyleDao = new AuthUserStyle().dao();
	
	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeappStyleService#getAppStyle(java.lang.Long)
	 */
	@Override
	public WeappStyleResultDto getAppStyle(Long authUserId) {
		WeappStyle style = authUserStyleService.getAuthUserStyle(authUserId);
		if(style == null) return null;
		
//		AuthUserStyle userStyle = authUserStyleDao.findFirst("select * from " + AuthUserStyle.table + " where app_id=? ", authUserId);
		
		WeappStyleResultDto styleResDto = new WeappStyleResultDto();
		styleResDto.setNavbarBgcolor(style.getNavbarBgcolor());
		styleResDto.setOtherBgcolor(style.getOtherBgcolor());
		styleResDto.setTabbarBgColor(style.getTabbarBgColor());
		styleResDto.setTabbarColor(style.getTabbarColor());
		styleResDto.setTabbarSelectedColor(style.getTabbarSelectedColor());
		
		List<WeappStyleTabbarItem> styleTabItems = weappStyleTabbarItemService.getStyleItems(style.getId());
		List<WeappStyleTabbarItemResultDto> tabbarItems = new ArrayList<WeappStyleTabbarItemResultDto>();
		for(WeappStyleTabbarItem tabbarItem : styleTabItems){
			WeappStyleTabbarItemResultDto tabbarItemDto = new WeappStyleTabbarItemResultDto();
			tabbarItemDto.setTabbarIndex(tabbarItem.getTabbarIndex());
			tabbarItemDto.setTabbarIconPath(tabbarItem.getTabbarIconPath());
			tabbarItemDto.setTabbarSelectedIconpath(tabbarItem.getTabbarSelectedIconpath());
			tabbarItemDto.setTabbarTitle(tabbarItem.getTabbarTitle());
			tabbarItems.add(tabbarItemDto);
		}
		styleResDto.setTabbarItems(tabbarItems);
		
		return styleResDto;
	}

}