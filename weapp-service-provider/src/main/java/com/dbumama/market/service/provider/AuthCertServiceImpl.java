package com.dbumama.market.service.provider;

import com.dbumama.market.model.AuthCert;
import com.dbumama.market.service.api.AuthCertService;
import com.dbumama.market.service.api.WxmallBaseException;
import com.dbumama.market.service.base.WxmServiceBase;
import com.dbumama.market.utils.FileUtils;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCBean;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Bean
@RPCBean
public class AuthCertServiceImpl extends WxmServiceBase<AuthCert> implements AuthCertService {

	/* (non-Javadoc)
	 * @see com.dbumama.market.service.api.AuthCertService#findByAppId(java.lang.String)
	 */
	@Override
	public List<AuthCert> findByAppId(String appId) {
		return DAO.find("select * from " + AuthCert.table + " where app_id=? and is_default != 1 order by updated desc", appId);
	}

	@Override
	public AuthCert findDefault() {
		return DAO.findFirst("select * from " + AuthCert.table + " where is_default=1");
	}

	@Override
	public AuthCert findUse(String appId) {
		return DAO.findFirst("select * from " + AuthCert.table + " where app_id=? and is_use = 1",appId);
	}

	@Override
	public void saveOrUpdate(Long id, String appId,String pay_mch_id, String pay_secret_key,File uFile) {
		if(appId == null) throw new WxmallBaseException("appId is null");
		if(pay_mch_id == null) throw new WxmallBaseException("支付账号不能为空");
		if(pay_secret_key == null) throw new WxmallBaseException("支付账号不能为空");
		
		AuthCert cert = findById(id);
		
		if(cert == null){
			cert = new AuthCert();
			cert.setCreated(new Date()).setAppId(appId).setIsDefault(false);
		}
		
		if(uFile != null){		
			try {
				cert.setCertFile(FileUtils.toByteArray(uFile));
			} catch (IOException e) {
				throw new WxmallBaseException(e.getMessage());
			}finally {
				//删除文件
				if(uFile != null)
					uFile.delete();
			}
		}
		
		cert.setActive(1).setUpdated(new Date()).setPayMchId(pay_mch_id).setPaySecretKey(pay_secret_key);
		
		try {
			saveOrUpdate(cert);				
		} catch (Exception e) {
			throw new WxmallBaseException(e.getMessage());
		}
	}

	@Override
	public void startUse(Long id,String appId) {
		
		List<AuthCert> list = findByAppId(appId);
		if(list != null && list.size() > 0){
			for (AuthCert authCert : list) {
				authCert.setIsUse(false);
				try {
					update(authCert);				
				} catch (Exception e) {
					throw new WxmallBaseException(e.getMessage());
				}
			}
		}
		if(id == null) return;
		
		AuthCert cert = findById(id);
		if(cert == null) throw new WxmallBaseException("启用失败");
		cert.setIsUse(true);
		try {
			update(cert);				
		} catch (Exception e) {
			throw new WxmallBaseException(e.getMessage());
		}
	}

	@Override
	public void UpdateDefault(Long id, String pay_mch_id, String pay_secret_key, File file) {
		
		if(pay_mch_id == null) throw new WxmallBaseException("支付账号不能为空");
		if(pay_secret_key == null) throw new WxmallBaseException("支付账号不能为空");
		
		AuthCert cert = findById(id);
		
		if(cert == null) throw new WxmallBaseException("更新出错");
		
		if(file != null){		
			try {
				cert.setCertFile(FileUtils.toByteArray(file));
			} catch (IOException e) {
				throw new WxmallBaseException(e.getMessage());
			}finally {
				//删除文件
				if(file != null)
					file.delete();
			}
		}
		
		cert.setActive(1).setUpdated(new Date()).setIsDefault(true)
		.setPayMchId(pay_mch_id).setPaySecretKey(pay_secret_key).setIsUse(false);
		try {
			saveOrUpdate(cert);				
		} catch (Exception e) {
			throw new WxmallBaseException(e.getMessage());
		}
	}

}