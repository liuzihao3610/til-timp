package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.VehiclePhoto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* 轿运车照片档案表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface VehiclePhotoService extends BaseService<VehiclePhoto> {

	void uploadPhoto(Long vehicleId, Long licenseId, Integer licenseType, Integer photoType, String photoDesc, MultipartFile file) throws Exception;

	// 通过照片类型以及轿运车ID或证件ID获取照片
	List<VehiclePhoto> getPhotos(Integer photoType, Long id);

	// 通过轿运车ID获取轿运车照片以及相关联的证件照片
	List<VehiclePhoto> getPhotosByVehicleId(Long vehicleId);
}
