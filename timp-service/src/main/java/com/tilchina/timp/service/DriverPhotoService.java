package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.DriverLicense;
import com.tilchina.timp.model.DriverPhoto;

/**
* 驾驶员照片档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface DriverPhotoService extends BaseService<DriverPhoto> {

	void deleteList(int[] data);

	void updateBillStatus(Long photoId, String path);

	void addInfoAndPhoto(DriverPhoto driverPhoto, MultipartFile file);

	void addPhoto(MultipartFile file, String photoDes, int photoType, Long driverId);

	void removeDate(Long photoId);

	void audit(Long data);

	void unaudit(Long data);

	void deleteByDriverIdList(int[] data);

	void deleteByDriverId(Long id);
	
	List<DriverPhoto> selectByDriverId(Long driverId);

	void add(String photoDes, int photoType, MultipartFile file);

	PageInfo<DriverPhoto> getList(Map<String, Object> data, int pageNum, int pageSize);

}
