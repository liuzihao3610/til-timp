package com.tilchina.timp.app.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.AppApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CommonDamage;
import com.tilchina.timp.model.CommonDamageDetail;
import com.tilchina.timp.model.CommonDamagePhoto;
import com.tilchina.timp.service.CommonDamageDetailService;
import com.tilchina.timp.service.CommonDamagePhotoService;
import com.tilchina.timp.service.CommonDamageService;
import com.tilchina.timp.service.DamagePositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/s1/app/commondamage"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppCommonDamageController extends BaseControllerImpl<CommonDamage> {

	@Autowired
	private CommonDamageService damageService;

	@Autowired
	private CommonDamageDetailService damageDetailService;

	@Autowired
	private CommonDamagePhotoService damagePhotoService;

	@Autowired
	private DamagePositionService damagePositionService;

	@Override
	protected BaseService<CommonDamage> getService() {
		return damageService;
	}

	// 通用质损管理主表
	@PostMapping("/addDamage")
	@Auth(ClientType.APP)
	public ApiResult<String> addDamage(@RequestBody AppApiParam<String> param) {

		try {
			CommonDamage damage = JSON.parseObject(param.getData(), CommonDamage.class);
			damageService.add(damage);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("/removeDamage")
	@Auth(ClientType.APP)
	public ApiResult<String> removeDamage(@RequestBody AppApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			damageService.deleteById(map.get("damageId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	// 为主表记录添加照片
	@PostMapping(value = "/addWithPhoto")
	@Auth(ClientType.APP)
	public ApiResult<String> addWithPhoto(
			@RequestParam("appLogin") String param,
			@RequestParam("damage") String json,
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "photoType", required = false) Integer[] photoType,
			@RequestParam(value = "photoDesc", required = false) String[] photoDesc) {

		try {
			CommonDamage damage = JSON.parseObject(json, CommonDamage.class);
			Long damageId = damageService.appAddDamageWithPhotos(damage, files, photoType, photoDesc);
			Map<String, Long> result = new HashMap(){{
				put("damageId", damageId);
			}};
			return ApiResult.success(damageId.toString());
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDamageList")
	@Auth(ClientType.APP)
	public ApiResult<List<CommonDamage>> getDamageList(@RequestBody AppApiParam<Map<String, Object>> param) {
		return ApiResult.success(damageService.appQueryList(param.getData()));
	}

	// 主表记录，添加照片
	@PostMapping(value = "/uploadPhotos")
	@Auth(ClientType.APP)
	public ApiResult<String> uploadPhotos(
			@RequestParam("appLogin") String param,
			@RequestParam("damageId") Long damageId,
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "photoType", required = false) Integer[] photoType,
			@RequestParam(value = "photoDesc", required = false) String[] photoDesc) {

		try {
			damageService.uploadDamagePhotos(damageId, files, photoType, photoDesc);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getPhotos")
	@Auth(ClientType.APP)
	public ApiResult<List<CommonDamagePhoto>> getPhotos(@RequestBody AppApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(map.get("damageId"));
			return ApiResult.success(damagePhotos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}


	// 通用质损管理子表
	@PostMapping(value = "/addDetail")
	@Auth(ClientType.APP)
	public ApiResult<String> addDetail(@RequestBody AppApiParam<String> param) {

		try {
			CommonDamageDetail damageDetail = JSON.parseObject(param.getData(), CommonDamageDetail.class);
			damageDetailService.add(damageDetail);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/addDetailWithPhotos")
	@Auth(ClientType.APP)
	public ApiResult<String> addDetailWithPhotos(
			@RequestParam("appLogin") String param,
			@RequestParam("damageDetail") String json,
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "photoType", required = false) Integer[] photoType,
			@RequestParam(value = "photoDesc", required = false) String[] photoDesc) {

		try {
			CommonDamageDetail damageDetail = JSON.parseObject(json, CommonDamageDetail.class);
			damageDetailService.addDamageDetailWithPhotos(damageDetail, files, photoType, photoDesc);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure(e);
		}
	}

	@PostMapping(value = "/removeDetail")
	@Auth(ClientType.APP)
	public ApiResult<String> deleteDetail(@RequestBody AppApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			damageDetailService.deleteById(map.get("damageDetailId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/putPartDetail")
	@Auth(ClientType.APP)
	public ApiResult<String> putPartDetail(@RequestBody AppApiParam<String> param) {

		try {
			CommonDamageDetail damageDetail = JSON.parseObject(param.getData(), CommonDamageDetail.class);
			damageDetailService.updateSelective(damageDetail);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDetail")
	@Auth(ClientType.APP)
	public ApiResult<CommonDamageDetail> getDetail(@RequestBody AppApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			CommonDamageDetail damageDetail = damageDetailService.queryById(map.get("damageDetailId"));
			return ApiResult.success(damageDetail);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDetails")
	@Auth(ClientType.APP)
	public ApiResult<List<CommonDamageDetail>> getDetails(@RequestBody AppApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			List<CommonDamageDetail> damageDetails = damageDetailService.selectDetailsByDamageId(map.get("damageId"));
			return ApiResult.success(damageDetails);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getListDetail")
	@Auth(ClientType.APP)
	public ApiResult<List<CommonDamageDetail>> getListDetail(@RequestBody AppApiParam<Map<String, Object>> param) {

		try {
			PageInfo<CommonDamageDetail> pageInfo = damageDetailService.queryList((Map)param.getData(), param.getPageNum(), param.getPageSize());
			log.debug("get result: {}", pageInfo);
			return ApiResult.success(pageInfo);
		} catch (Exception var3) {
			log.error("查询失败，param={}", param, var3);
			return ApiResult.failure("9999", var3.getMessage());
		}
	}

	@PostMapping(value = "/uploadDetailPhotos")
	@Auth(ClientType.APP)
	public ApiResult<String> uploadDetailPhotos(
			@RequestParam("appLogin") String param,
			@RequestParam("damageId") Long damageId,
			@RequestParam("damageDetailId") Long damageDetailId,
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "photoType", required = false) Integer[] photoType,
			@RequestParam(value = "photoDesc", required = false) String[] photoDesc) {
		
		if (damageDetailId == null || files.length < 1)
			return ApiResult.failure("9005", "damageId, files");

		try {
			damageDetailService.uploadDamageDetailPhotos(damageId, damageDetailId, files, photoType, photoDesc);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/removePhoto")
	@Auth(ClientType.APP)
	public ApiResult<String> deletePhoto(@RequestBody AppApiParam<Map<String, Long>> param) {

		try {
			Map<String, Long> map = param.getData();
			damagePhotoService.deleteById(map.get("damagePhotoId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/putPartPhoto")
	@Auth(ClientType.APP)
	public ApiResult<String> putPartPhoto(@RequestBody AppApiParam<CommonDamagePhoto> param) {

		try {
			CommonDamagePhoto damagePhoto = param.getData();
			damagePhotoService.updateSelective(damagePhoto);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getListPhoto")
	@Auth(ClientType.APP)
	public ApiResult<List<CommonDamagePhoto>> getPhotoList(@RequestBody AppApiParam<Long> param) {

		try {
			Long damageDetailId = param.getData();
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDetailId(damageDetailId);
			return ApiResult.success(damagePhotos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getByCarVin")
	@Auth(ClientType.APP)
	public ApiResult<CommonDamage> getByCarVin(@RequestBody AppApiParam<Map<String, String>> param) {

		try {
			Map<String, String> map = param.getData();
			CommonDamage damage = damageService.getByCarVin(map.get("carVin"));
			return ApiResult.success(damage);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure(e);
		}
	}

	@PostMapping(value = "/getReferenceList")
	@Auth(ClientType.APP)
	public ApiResult<List<Map<Long, String>>> getReferenceList(@RequestBody AppApiParam<Map<String, String>> param) {

		try {
			List<Map<Long, String>> result = damagePositionService.getReferenceList(param.getData());
			return ApiResult.success(result);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}