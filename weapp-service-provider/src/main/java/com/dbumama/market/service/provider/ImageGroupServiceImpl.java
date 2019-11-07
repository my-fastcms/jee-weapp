package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.ImageGroup;
import com.dbumama.market.model.SellerImages;
import com.dbumama.market.service.api.ImageGroupResultDto;
import com.dbumama.market.service.api.ImageGroupService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.service.sqlhelper.QueryHelper;
import com.jfinal.plugin.activerecord.Db;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class ImageGroupServiceImpl extends WxmServiceBase<ImageGroup> implements ImageGroupService {

	@Override
	public List<ImageGroupResultDto> getGroup(Long sellerId) {
		QueryHelper helper = new QueryHelper();
		helper.addWhere("seller_id", sellerId).build();
        List<ImageGroup> imageGroups = DAO.find("select * from "+ImageGroup.table + helper.getWhere(), helper.getParams());
        List<ImageGroupResultDto> resultDtos=new ArrayList<ImageGroupResultDto>();
        
        ImageGroupResultDto dto=new ImageGroupResultDto();
    	dto.setImageNum(Db.queryLong("select count(id) as imageNum from "+SellerImages.table +" where active=1 and file_type =1 and media_id is not null"));
    	dto.setId(-1000L);//
    	dto.setGroupName("微信图片");
    	resultDtos.add(dto);
        
        for (ImageGroup imageGroup : imageGroups) {
        	dto=new ImageGroupResultDto();
        	dto.setImageNum(Db.queryLong("select count(id) as imageNum from "+SellerImages.table +" where active=1 and file_type =1 AND img_group_id=?", imageGroup.getId()));
        	dto.setId(imageGroup.getId());
        	dto.setGroupName(imageGroup.getGroupName());
        	resultDtos.add(dto);
		}
		return resultDtos;
	}

}