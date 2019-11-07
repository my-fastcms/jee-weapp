package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbumama.market.model.ImageSowing;
import com.dbumama.market.service.api.ImageSowingResultDto;
import com.dbumama.market.service.api.ImageSowingService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class ImageSowingServiceImpl extends WxmServiceBase<ImageSowing> implements ImageSowingService {
	public final static String PARAMS_ERROR = "参数错误";
	
	@Override
	public Page<ImageSowingResultDto> list(Long appId, Integer pageNo, Integer pageSize){
		if(appId == null)throw new RuntimeException(PARAMS_ERROR);
		Columns columns = Columns.create();
		columns.add(Column.create("app_id", appId));
		columns.add(Column.create("active", 1));
		Page<ImageSowing> paginate = DAO.paginateByColumns(pageNo, pageSize, columns, " updated desc ");
		List<ImageSowing> imgList = paginate.getList();
		List<ImageSowingResultDto> imgResults = resultDto(imgList);
		return new Page<ImageSowingResultDto>(imgResults,pageNo, pageSize,paginate.getTotalPage(),paginate.getTotalRow());
	}

	@Override
	public void delByIdAndAppid(Long id, Long appId) throws Exception {
		if(id==null || appId==null){
			throw new RuntimeException(PARAMS_ERROR);
		}
		ImageSowing imgSowing = findById(id);
		
		if(imgSowing==null || appId.intValue()!= imgSowing.getAppId().intValue() 
				|| !imgSowing.getActive()){
			throw new RuntimeException(PARAMS_ERROR);
		}
		imgSowing.setActive(false);
		update(imgSowing);
	}

	@Override
	public void mysaveOrUpdate(Long id, Long appId, String sowingImg, String sowingUrl) throws Exception {
		if(appId == null){
			throw new RuntimeException(PARAMS_ERROR);
		}
		
		ImageSowing imageSowing = findById(id);
		if(imageSowing == null){
			Integer totalCount = Db.queryInt("SELECT COUNT(id) as count FROM "+ImageSowing.table+" WHERE active=1 AND app_id=?",appId);
			if(totalCount > 8){
				throw new RuntimeException("轮播图已达上限");
			}
			imageSowing = new ImageSowing();
			imageSowing.setAppId(appId);
			imageSowing.setCreated(new Date());
		}
		imageSowing.setSowingImg(sowingImg);
		imageSowing.setSowingUrl(sowingUrl);
		imageSowing.setUpdated(new Date());
		imageSowing.setActive(true);
		saveOrUpdate(imageSowing);
	}

	
	public List<ImageSowingResultDto> resultDto(List<ImageSowing> imgList){
		List<ImageSowingResultDto> imgResults = new ArrayList<ImageSowingResultDto>();
		for (ImageSowing img: imgList) {
			ImageSowingResultDto imageDto = new ImageSowingResultDto();
			imageDto.setId(img.getId());
			imageDto.setAppId(img.getAppId());
			imageDto.setSowingImg(getImageDomain()+img.getSowingImg());
			imageDto.setSowingUrl(img.getSowingUrl());
			imageDto.setCreated(img.getCreated());
			imageDto.setUpdated(img.getUpdated());
			imgResults.add(imageDto);
		}
		return imgResults;
	}

}