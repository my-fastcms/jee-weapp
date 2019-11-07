package com.dbumama.market.service.provider;

import java.io.IOException;
import java.util.List;

import com.dbumama.market.model.WeiPage;
import com.dbumama.market.model.WeipageCategory;
import com.dbumama.market.service.api.UmpException;
import com.dbumama.market.service.api.WeiPageService;
import com.dbumama.market.service.base.WxmServiceBase;
import com.jfinal.plugin.activerecord.Page;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

@Bean
@RPCBean
public class WeiPageServiceImpl extends WxmServiceBase<WeiPage> implements WeiPageService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#getWeiPageById(java.lang.Long)
	 */
	@Override
	public WeiPage getWeiPageById(Long id) throws UmpException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#getContentById(java.lang.Long)
	 */
	@Override
	public String getContentById(Long id) throws UmpException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#getWeiPageInfoById(java.lang.Long)
	 */
	@Override
	public WeiPage getWeiPageInfoById(Long id) throws UmpException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#list(java.lang.Long, com.dbumama.market.model.WeiPage, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<WeiPage> list(Long sellerId, WeiPage weipage, Integer pageNo, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#getPageHtml(com.dbumama.market.model.WeiPage, boolean)
	 */
	@Override
	public String getPageHtml(WeiPage entity, boolean publish) throws UmpException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#deleteById(java.lang.Long)
	 */
	@Override
	public void deleteById(Long weipageId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#findIndex(java.lang.Long)
	 */
	@Override
	public WeiPage findIndex(Long sellerId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#getSellerWeipage(java.lang.Long)
	 */
	@Override
	public List<WeiPage> getSellerWeipage(Long sellerId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#getSellerWeipageCategory(java.lang.Long)
	 */
	@Override
	public List<WeipageCategory> getSellerWeipageCategory(Long sellerId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#findCategoryById(java.lang.Long)
	 */
	@Override
	public WeipageCategory findCategoryById(Long categoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#deleteCategoryById(java.lang.Long)
	 */
	@Override
	public void deleteCategoryById(Long categoryId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#pageCategory(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page<WeipageCategory> pageCategory(Long sellerId, Integer pageNo, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.WeiPageService#save(com.dbumama.market.model.WeipageCategory)
	 */
	@Override
	public Long save(WeipageCategory category) {
		// TODO Auto-generated method stub
		return null;
	}

}