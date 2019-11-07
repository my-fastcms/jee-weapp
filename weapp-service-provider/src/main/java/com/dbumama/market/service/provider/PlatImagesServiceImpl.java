package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.PlatImages;
import com.dbumama.market.service.api.ImagepathResultDto;
import com.dbumama.market.service.api.PlatImagesService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class PlatImagesServiceImpl extends WxmServiceBase<PlatImages> implements PlatImagesService {

	@Override
	public Page<ImagepathResultDto> paginate(Long platUserId, int pageNo,
			int pageSize) {
		Columns columns = Columns.create("plat_user_id", platUserId).eq("file_type", 1).eq("active", 1);
		
		Page<PlatImages> pages=DAO.paginateByColumns(pageNo, pageSize, columns.getList(), "created desc");
		List<ImagepathResultDto> imagePath=new ArrayList<ImagepathResultDto>();
		for(PlatImages platImage : pages.getList()){
			ImagepathResultDto dto=new ImagepathResultDto();
			dto.setId(platImage.getId());
			dto.setImgPath(getImageDomain() + platImage.getImgPath());
			dto.setImgUrl(platImage.getImgPath());
			dto.setTitle(platImage.getTitle());
			imagePath.add(dto);
		}
		return new Page<ImagepathResultDto>(imagePath, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(),pages.getTotalRow());
	}

}