package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.model.TransporterLicense;
import org.springframework.web.multipart.MultipartFile;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface TransporterLicenseService extends BaseService<TransporterLicense> {
	
	void logicDeleteById(Long id);
	
	void logicDeleteByIdList(int[]  ids);

	PageInfo<TransporterLicense> getRefer(Map<String, Object> data, int pageNum, int pageSize);
	
	/**
	 * 审核
	 * @param data
	 */
	void updateCheck(TransporterLicense data);

	/**
	 * 照片上传
	 * @param file
	 * @param transporterLicenseId
	 */
    void addHeadPhoto(MultipartFile file, Long transporterLicenseId);
}
