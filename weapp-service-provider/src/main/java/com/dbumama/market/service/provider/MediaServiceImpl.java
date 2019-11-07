package com.dbumama.market.service.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbumama.market.base.ApiResult;
import com.dbumama.market.model.AuthUser;
import com.dbumama.market.model.Media;
import com.dbumama.market.service.api.AuthUserService;
import com.dbumama.market.service.api.MediaItemNewsResDto;
import com.dbumama.market.service.api.MediaItemResDto;
import com.dbumama.market.service.api.MediaService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.dbumama.weixin.api.CompMediaApi;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;

@Bean
@RPCBean
public class MediaServiceImpl extends WxmServiceBase<Media> implements MediaService {

	@Inject
	private AuthUserService authUserService;
	
	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MediaService#list(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<MediaItemNewsResDto> listMediasNews(Long appId, Integer pageNo, Integer pageSize) {
		
		AuthUser authUser = authUserService.findById(appId);
		if(authUser == null) return null;
		
		pageNo = pageNo -1; //微信素材接口是从0开始

		int totalPage = 0;
		int totalRow = 0;
		List<MediaItemNewsResDto> resDtos = new ArrayList<MediaItemNewsResDto>();
		
		try {
			
			ApiResult res=CompMediaApi.batchGetMaterial(CompMediaApi.MediaType.NEWS, authUserService.getAccessToken(authUser), pageNo,pageSize);
			
			if(!res.isSucceed()) return null;
			
			totalRow = res.getInt("total_count");
			
			totalPage = totalRow % pageSize == 0 ? totalRow/pageSize : totalRow/pageSize + 1;
			
			List items = res.getList("item");
			
			for(int i=0;i<items.size();i++){
				Map<String, Object> item = (Map<String, Object>) items.get(i);
				String mediaId = (String) item.get("media_id");
				JSONObject newsItems = (JSONObject) item.get("content");
				JSONArray newsArray = newsItems.getJSONArray("news_item");
				if(newsArray != null && newsArray.size()>0){
					Map<String, Object> newsItem = (Map<String, Object>) newsArray.get(0);
					MediaItemNewsResDto resDto = new MediaItemNewsResDto();
					resDto.setMediaId(mediaId);
					resDto.setCoverPic((String)newsItem.get("thumb_url"));
					resDto.setTitle((String)newsItem.get("title"));
					String digest = (String)newsItem.get("digest");
					digest = digest.length() > 20 ? digest.substring(0, 20).concat("...") : digest;
					resDto.setDigest(digest);
					resDto.setUrl((String)newsItem.get("url"));
					resDtos.add(resDto);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new Page<MediaItemNewsResDto>(resDtos, pageNo, pageSize, totalPage, totalRow);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MediaService#listMedias(java.lang.Long, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Page<MediaItemResDto> listMedias(Long appId, CompMediaApi.MediaType type, Integer pageNo, Integer pageSize) {
		
		AuthUser authUser = authUserService.findById(appId);
		if(authUser == null) return null;
		
		pageNo = pageNo -1; //微信素材接口是从0开始

		int totalPage = 0;
		int totalRow = 0;
		List<MediaItemResDto> resDtos = new ArrayList<MediaItemResDto>();
		
		try {
			ApiResult res=CompMediaApi.batchGetMaterial(type, authUserService.getAccessToken(authUser), pageNo,pageSize);
			
			if(!res.isSucceed()) return null;
			
			totalRow = res.getInt("total_count");
			
			totalPage = totalRow % pageSize == 0 ? totalRow/pageSize : totalRow/pageSize + 1;
			
			List items = res.getList("item");
			
			for(int i=0;i<items.size();i++){
				Map<String, Object> item = (Map<String, Object>) items.get(i);
				String mediaId = (String) item.get("media_id");
				String name = (String) item.get("name");
				Integer updateTime = (Integer) item.get("update_time");
				String url = (String) item.get("url");
				
				MediaItemResDto resDto = new MediaItemResDto(mediaId, name, updateTime, url, type.get());
//				resDto.setMediaId(mediaId);
//				resDto.setName(name);
//				resDto.setUpdateTime(updateTime);
//				resDto.setUrl(url);
				
				resDtos.add(resDto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new Page<MediaItemResDto>(resDtos, pageNo, pageSize, totalPage, totalRow);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MediaService#listMediasImage(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<MediaItemResDto> listMediasImage(Long appId, Integer pageNo, Integer pageSize) {
		return listMedias(appId, CompMediaApi.MediaType.IMAGE, pageNo, pageSize);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MediaService#listMediasVoice(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<MediaItemResDto> listMediasVoice(Long appId, Integer pageNo, Integer pageSize) {
		return listMedias(appId, CompMediaApi.MediaType.VOICE, pageNo, pageSize);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.zanm.service.api.MediaService#listMediasVideo(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<MediaItemResDto> listMediasVideo(Long appId, Integer pageNo, Integer pageSize) {
		return listMedias(appId, CompMediaApi.MediaType.VIDEO, pageNo, pageSize);
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.MediaService#list(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<Media> list(Long appId, Integer pageNo, Integer pageSize) {
		Columns columns = Columns.create(Column.create("app_id", appId));
		return DAO.paginateByColumns(pageNo, pageSize, columns, " updated desc ");
	}
	
}