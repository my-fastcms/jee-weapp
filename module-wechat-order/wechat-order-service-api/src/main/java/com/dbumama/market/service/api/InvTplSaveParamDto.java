package com.dbumama.market.service.api;

import com.dbumama.market.service.common.AbstractParamDto;

@SuppressWarnings("serial")
public class InvTplSaveParamDto extends AbstractParamDto {

	String tplcontent;
	String designhtml;
	String tablehtml;
	String imghtml;
	Integer pagewidth;
	Integer pageheight;
	Integer offsetx;
	Integer offsety;
	
	public InvTplSaveParamDto(String tplcontent, String designhtml, String tablehtml, Long authUserId) {
		super();
		this.tplcontent = tplcontent;
		this.designhtml = designhtml;
		this.tablehtml = tablehtml;
		this.authUserId = authUserId;
	}
	
	public String getTplcontent() {
		return tplcontent;
	}
	public void setTplcontent(String tplcontent) {
		this.tplcontent = tplcontent;
	}
	public String getDesignhtml() {
		return designhtml;
	}
	public void setDesignhtml(String designhtml) {
		this.designhtml = designhtml;
	}
	public String getTablehtml() {
		return tablehtml;
	}
	public void setTablehtml(String tablehtml) {
		this.tablehtml = tablehtml;
	}
	public String getImghtml() {
		return imghtml;
	}
	public void setImghtml(String imghtml) {
		this.imghtml = imghtml;
	}
	public Integer getPagewidth() {
		return pagewidth;
	}
	public void setPagewidth(Integer pagewidth) {
		this.pagewidth = pagewidth;
	}
	public Integer getPageheight() {
		return pageheight;
	}
	public void setPageheight(Integer pageheight) {
		this.pageheight = pageheight;
	}
	public Integer getOffsetx() {
		return offsetx;
	}
	public void setOffsetx(Integer offsetx) {
		this.offsetx = offsetx;
	}
	public Integer getOffsety() {
		return offsety;
	}
	public void setOffsety(Integer offsety) {
		this.offsety = offsety;
	}
	
}
