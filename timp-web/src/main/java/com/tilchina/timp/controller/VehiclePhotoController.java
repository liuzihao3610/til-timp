package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.VehiclePhoto;
import com.tilchina.timp.service.VehiclePhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.util.List;
import java.util.Map;

/**
* 轿运车照片档案表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@MultipartConfig
@RequestMapping(value = {"/s1/vehiclephoto"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class VehiclePhotoController extends BaseControllerImpl<VehiclePhoto>{

	@Autowired
	private VehiclePhotoService vehiclephotoservice;
	
	@Override
	protected BaseService<VehiclePhoto> getService() {
		return vehiclephotoservice;
	}

	@PostMapping("uploadVehiclePhoto")
	@Auth(ClientType.WEB)
	public ApiResult<String> uploadVehiclePhoto(@RequestParam("vehicleId") Long vehicleId,
			                                    @RequestParam("photoDesc") String photoDesc,
			                                    @RequestParam("file")MultipartFile file) {
		try {
			vehiclephotoservice.uploadPhoto(vehicleId, null,null, 0, photoDesc, file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping("uploadLicensePhoto")
	@Auth(ClientType.WEB)
	public ApiResult<String> uploadLicensePhoto(@RequestParam("licenseId") Long licenseId,
	                                            @RequestParam("licenseType") Integer licenseType,
	                                            @RequestParam("photoDesc") String photoDesc,
	                                            @RequestParam("file")MultipartFile file) {
		try {
			vehiclephotoservice.uploadPhoto(null, licenseId, licenseType, 1, photoDesc, file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping("uploadPhoto")
	@Auth(ClientType.WEB)
	public ApiResult<String> uploadPhoto(@RequestParam("vehicleId") Long vehicleId,
	                                     @RequestParam("licenseId") Long licenseId,
	                                     @RequestParam("licenseType") Integer licenseType,
	                                     @RequestParam("photoType") Integer photoType,
	                                     @RequestParam("photoDesc") String photoDesc,
	                                     @RequestParam("file") MultipartFile file) {
		try {
			vehiclephotoservice.uploadPhoto(vehicleId, licenseId, licenseType, photoType, photoDesc, file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping("getLicensePhoto")
	@Auth(ClientType.WEB)
	public ApiResult<List<VehiclePhoto>> getLicensePhoto(@RequestBody ApiParam<Map<String, Long>> params) {
		try {
			List<VehiclePhoto> photos = vehiclephotoservice.getPhotos(1, params.getData().get("licenseId"));
			return ApiResult.success(photos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping("getVehiclePhoto")
	@Auth(ClientType.WEB)
	public ApiResult<List<VehiclePhoto>> getVehiclePhoto(@RequestBody ApiParam<Map<String, Long>> params) {
		try {
			List<VehiclePhoto> photos = vehiclephotoservice.getPhotosByVehicleId(params.getData().get("vehicleId"));
			return ApiResult.success(photos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<VehiclePhoto> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<List<VehiclePhoto>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
		return super.getList(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {
		return super.post(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
		return super.putPart(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> put(@RequestBody ApiParam<String> param) {
		return super.put(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
		return super.delete(param);
	}
}
