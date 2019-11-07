package com.dbumama.market.service.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.dbumama.market.service.common.AbstractResultDto;

/**
 * 正在进行中的拼团活动。。。
 * @author wangjun
 *
 */
@SuppressWarnings("serial")
public class GroupingResultDto extends AbstractResultDto{

	private Long groupId;
	private Long productId;
	private String productName;
	private String productImage;
	private String productPrice;
	private Long groupHeaderId;				//团长id
	private String groupHeader;				//团长nick
	private String groupHeaderImg;			//团长头像
	private Integer diffUserCount;			//还需要多少人成团	
	private Integer groupStatus;			//当前团状态
	private Long expiresIn;					//倒计时
	private Boolean isExpires;				//是否过期
	private Boolean isGrouped;				//当前购买用户是否已拼过团
	private Date joinTime;					//当前参团用户的参团时间
	List<GrouponUserResultDto> groupUsers;
	private ProdGroupResultDto multiGroupInfo;	//拼团活动信息
	
	private List<SpecificationResultDto> specifications;	//商品规格
	HashMap<String, ProductSpecPriceResultDto> priceMap;	//商品规格以及价格库存组合
	
	/**
	 * @return the specifications
	 */
	public List<SpecificationResultDto> getSpecifications() {
		return specifications;
	}

	/**
	 * @param specifications the specifications to set
	 */
	public void setSpecifications(List<SpecificationResultDto> specifications) {
		this.specifications = specifications;
	}

	/**
	 * @return the priceMap
	 */
	public HashMap<String, ProductSpecPriceResultDto> getPriceMap() {
		return priceMap;
	}

	/**
	 * @param priceMap the priceMap to set
	 */
	public void setPriceMap(HashMap<String, ProductSpecPriceResultDto> priceMap) {
		this.priceMap = priceMap;
	}

	/**
	 * @return the joinTime
	 */
	public Date getJoinTime() {
		return joinTime;
	}

	/**
	 * @param joinTime the joinTime to set
	 */
	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	/**
	 * @return the multiGroupInfo
	 */
	public ProdGroupResultDto getMultiGroupInfo() {
		return multiGroupInfo;
	}

	/**
	 * @param multiGroupInfo the multiGroupInfo to set
	 */
	public void setMultiGroupInfo(ProdGroupResultDto multiGroupInfo) {
		this.multiGroupInfo = multiGroupInfo;
	}

	public GroupingResultDto (){
		setIsExpires(false);
		setIsGrouped(false);
	}
	
	/**
	 * @return the productId
	 */
	public Long getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productImage
	 */
	public String getProductImage() {
		return productImage;
	}

	/**
	 * @param productImage the productImage to set
	 */
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	
	/**
	 * @return the productPrice
	 */
	public String getProductPrice() {
		return productPrice;
	}

	/**
	 * @param productPrice the productPrice to set
	 */
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	/**
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getGroupHeaderId() {
		return groupHeaderId;
	}
	public void setGroupHeaderId(Long groupHeaderId) {
		this.groupHeaderId = groupHeaderId;
	}
	public String getGroupHeader() {
		return groupHeader;
	}
	public void setGroupHeader(String groupHeader) {
		this.groupHeader = groupHeader;
	}
	public String getGroupHeaderImg() {
		return groupHeaderImg;
	}
	public void setGroupHeaderImg(String groupHeaderImg) {
		this.groupHeaderImg = groupHeaderImg;
	}
	public Integer getDiffUserCount() {
		return diffUserCount;
	}
	public void setDiffUserCount(Integer diffUserCount) {
		this.diffUserCount = diffUserCount;
	}
	public Boolean getIsGrouped() {
		return isGrouped;
	}
	public void setIsGrouped(Boolean isGrouped) {
		this.isGrouped = isGrouped;
	}
	public Long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}
	public List<GrouponUserResultDto> getGroupUsers() {
		return groupUsers;
	}
	public void setGroupUsers(List<GrouponUserResultDto> groupUsers) {
		this.groupUsers = groupUsers;
	}
	public Boolean getIsExpires() {
		return isExpires;
	}
	public void setIsExpires(Boolean isExpires) {
		this.isExpires = isExpires;
	}
	/**
	 * @return the groupStatus
	 */
	public Integer getGroupStatus() {
		return groupStatus;
	}

	/**
	 * @param groupStatus the groupStatus to set
	 */
	public void setGroupStatus(Integer groupStatus) {
		this.groupStatus = groupStatus;
	}
	
}
