package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;

import com.dbumama.market.model.SellerImages;
import com.dbumama.market.service.api.ImagepathResultDto;
import com.dbumama.market.service.api.SellerImagesService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class SellerImagesServiceImpl extends WxmServiceBase<SellerImages> implements SellerImagesService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerImagesService#paginate(java.lang.Integer, java.lang.Integer, java.lang.Long)
	 */
	@Override
	public Page<ImagepathResultDto> paginate(Long sellerId, Long groupId, Integer pageNo, Integer pageSize) {
		Columns columns = Columns.create("seller_id", sellerId).eq("img_group_id", groupId).eq("file_type", 1).eq("active", 1);
		
		if(groupId != null && groupId.intValue() == -1000){
			columns = Columns.create("seller_id", sellerId).eq("file_type", 1).eq("active", 1).is_not_null("media_id");
		}
		
		Page<SellerImages> pages=DAO.paginateByColumns(pageNo, pageSize, columns.getList(), "created desc");
		List<ImagepathResultDto> imagePath=new ArrayList<ImagepathResultDto>();
		for(SellerImages sellerImage : pages.getList()){
			ImagepathResultDto dto=new ImagepathResultDto();
			dto.setId(sellerImage.getId());
			dto.setImgPath(getImageDomain() + sellerImage.getImgPath());
			dto.setImgUrl(sellerImage.getImgPath());
			dto.setTitle(sellerImage.getTitle());
			dto.setMediaId(sellerImage.getMediaId() == null ? "" : sellerImage.getMediaId());
			imagePath.add(dto);
		}
		return new Page<ImagepathResultDto>(imagePath, pages.getPageNumber(), pages.getPageSize(), pages.getTotalPage(),pages.getTotalRow());
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.SellerImagesService#paginate(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<SellerImages> paginate(Long sellerId, Integer pageNo, Integer pageSize) {
		Columns columns = Columns.create("seller_id", sellerId).eq("file_type", 1).eq("active", 1);
		return DAO.paginateByColumns(pageNo, pageSize, columns.getList(), "created desc");
	}

}