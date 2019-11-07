package com.dbumama.market.service.api;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;

public interface MatterService {

	public List<MatterResultDto> getMatter(String accessToken, String media_id) throws MatterException;

	public Page<MatterResultDto> list(MatterListParamDto matterListParamDto) throws MatterException;

	/**
	 * 保存图文到微信
	 * 
	 * @param accessToken
	 * @return
	 */
	public String save2Weixin(String accessToken, String MatterData, String mediaId) throws MatterException;
}
