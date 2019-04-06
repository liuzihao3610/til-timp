package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.mapper.VehiclePhotoMapper;
import com.tilchina.timp.model.VehiclePhoto;
import com.tilchina.timp.service.VehicleLicenseService;
import com.tilchina.timp.service.VehiclePhotoService;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
* 轿运车照片档案表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class VehiclePhotoServiceImpl extends BaseServiceImpl<VehiclePhoto> implements VehiclePhotoService {

	@Autowired
    private VehiclePhotoMapper vehiclephotomapper;

	@Autowired
	private VehicleLicenseService vehicleLicenseService;
	
	@Override
	protected BaseMapper<VehiclePhoto> getMapper() {
		return vehiclephotomapper;
	}

	@Override
	protected StringBuilder checkNewRecord(VehiclePhoto vehiclephoto) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("YES", "licenseType", "证件类型", vehiclephoto.getLicenseType(), 10));
        s.append(CheckUtils.checkInteger("NO", "photoType", "照片类型", vehiclephoto.getPhotoType(), 10));
        s.append(CheckUtils.checkString("YES", "photoDesc", "照片描述", vehiclephoto.getPhotoDesc(), 100));
        s.append(CheckUtils.checkString("NO", "photoPath", "照片路径", vehiclephoto.getPhotoPath(), 100));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(VehiclePhoto vehiclephoto) {
        StringBuilder s = checkNewRecord(vehiclephoto);
        s.append(CheckUtils.checkPrimaryKey(vehiclephoto.getPhotoId()));
		return s;
	}

	@Override
	public void uploadPhoto(Long vehicleId, Long licenseId, Integer licenseType, Integer photoType, String photoDesc, MultipartFile file) throws Exception {
		try {
			Environment environment = Environment.getEnv();
			String imageType = "timp.image.path";
			if (photoType.equals(0)) {
				imageType = "image.vehicle";
			}
			if (photoType.equals(1)) {
				imageType = "image.license";
			}
			String photoPath = FileUtil.uploadImage(file,imageType);

			if (Objects.nonNull(licenseId)) {
				vehicleId = vehicleLicenseService.getVehicleIdByLicenseId(licenseId);
			}

			VehiclePhoto photo = new VehiclePhoto();
			photo.setVehicleId(vehicleId);
			photo.setLicenseId(licenseId);
			photo.setLicenseType(licenseType);
			photo.setPhotoType(photoType);
			photo.setPhotoDesc(photoDesc);
			photo.setPhotoPath(photoPath);
			photo.setCreator(environment.getUser().getUserId());
			photo.setCorpId(environment.getUser().getCorpId());
			add(photo);

		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<VehiclePhoto> getPhotos(Integer photoType, Long id) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("photoType", photoType);
//			if (photoType.equals(0)) {
//				params.put("vehicleId", id);
//			}
//			if (photoType.equals(1)) {
//				params.put("licenseId", id);
//			}

			List<VehiclePhoto> photos = vehiclephotomapper.getPhotos(params);
			return photos;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public List<VehiclePhoto> getPhotosByVehicleId(Long vehicleId) {
		try {
			List<VehiclePhoto> photos = vehiclephotomapper.getPhotosByVehicleId(vehicleId);
			return photos;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
