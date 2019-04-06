package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DriverLicense;

/**
* 驾驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface DriverLicenseService extends BaseService<DriverLicense> {

	void deleteList(int[] data);

	void updateBillStatus(Long driverLicenseId, String path);

	void addInfoAndPhoto(DriverLicense driverLicense, MultipartFile file);

	void addPhoto(MultipartFile file, String licenseName, int licenseType, Long driverId);

	void deleteByDriverIdList(int[] data);

	void deleteByDriverId(Long id);
	
	DriverLicense selectByDriverId(Long driverId);

	void audit(Long data);

	void unaudit(Long data);

	PageInfo<DriverLicense> getList(Map<String, Object> data, int pageNum, int pageSize);
	
	


}
