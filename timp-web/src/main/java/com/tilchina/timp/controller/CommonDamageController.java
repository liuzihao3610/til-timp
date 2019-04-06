package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.util.List;
import java.util.Map;

/**
* 通用质损管理主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Slf4j
@RestController
@MultipartConfig
@RequestMapping(value = {"/s1/commondamage"}, produces = {"application/json;charset=utf-8"})
public class CommonDamageController extends BaseControllerImpl<CommonDamage>{

	@Autowired
	private CommonDamageService damageService;

	@Autowired
	private CommonDamageDetailService damageDetailService;

	@Autowired
	private CommonDamagePhotoService damagePhotoService;

	@Override
	protected BaseService<CommonDamage> getService() {
		return damageService;
	}

	// 通用质损管理主表
	@Override
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestBody ApiParam<String> param) {

		try {
			CommonDamage damage = JSON.parseObject(param.getData(), CommonDamage.class);
			damageService.add(damage);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<CommonDamage> getById(@RequestBody ApiParam<Long> param) {
		return super.getById(param);
	}

	@Override
	@Auth(ClientType.WEB)
	public ApiResult<List<CommonDamage>> getList(@RequestBody ApiParam<Map<String, Object>> param) {

		try {
		    PageInfo<CommonDamage> damages = damageService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
			return ApiResult.success(damages);
		} catch (Exception e) {
			return ApiResult.failure("9999", e.getMessage());
		}
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

		try {
			Long damageId = param.getData();
			damageService.deleteById(damageId);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/addWithPhoto")
	@Auth(ClientType.WEB)
	public ApiResult<String> addWithPhoto(
			@RequestParam("damage") String json,
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "photoType", required = false) Integer[] photoType,
			@RequestParam(value = "photoDesc", required = false) String[] photoDesc) {

		try {
			CommonDamage damage = JSON.parseObject(json, CommonDamage.class);
			damageService.addDamageWithPhotos(damage, files, photoType, photoDesc);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	// 主表记录，添加照片
	@PostMapping(value = "/uploadPhotos")
	@Auth(ClientType.WEB)
	public ApiResult<String> uploadPhotos(
			@RequestParam("damageId") Long damageId,
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "photoType", required = false) Integer[] photoType,
			@RequestParam(value = "photoDesc", required = false) String[] photoDesc) {

		try {
			damageService.uploadDamagePhotos(damageId, files, photoType, photoDesc);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/getPhotos")
	@Auth(ClientType.WEB)
	public ApiResult<List<CommonDamagePhoto>> getPhotos(@RequestBody ApiParam<Long> param) {

		try {
			Long damageId = param.getData();
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDamageId(damageId);
			return ApiResult.success(damagePhotos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}


	// 通用质损管理子表
	@PostMapping(value = "/addDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> addDetail(@RequestBody ApiParam<String> param) {

		try {
			CommonDamageDetail damageDetail = JSON.parseObject(param.getData(), CommonDamageDetail.class);
			damageDetailService.add(damageDetail);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/addDetailWithPhotos")
	@Auth(ClientType.WEB)
	public ApiResult<String> addDetailWithPhotos(
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
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/removeDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> deleteDetail(@RequestBody ApiParam<Long> param) {

		try {
			Long damageDetailId = param.getData();
			damageDetailService.deleteById(damageDetailId);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/putPartDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> putPartDetail(@RequestBody ApiParam<String> param) {

		try {
			CommonDamageDetail damageDetail = JSON.parseObject(param.getData(), CommonDamageDetail.class);
			damageDetailService.updateSelective(damageDetail);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/getDetail")
	@Auth(ClientType.WEB)
	public ApiResult<CommonDamageDetail> getDetail(@RequestBody ApiParam<Long> param) {

		try {
			Long damageDetailId = param.getData();
			CommonDamageDetail damageDetail = damageDetailService.queryById(damageDetailId);
			return ApiResult.success(damageDetail);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/getDetails")
	@Auth(ClientType.WEB)
	public ApiResult<List<CommonDamageDetail>> getDetails(@RequestBody ApiParam<Long> param) {

		Long damageId = param.getData();
		try {
			List<CommonDamageDetail> damageDetails = damageDetailService.selectDetailsByDamageId(damageId);
			return ApiResult.success(damageDetails);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/getListDetail")
	@Auth(ClientType.WEB)
	public ApiResult<List<CommonDamageDetail>> getListDetail(@RequestBody ApiParam<Map<String, Object>> param) {

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
	@Auth(ClientType.WEB)
	public ApiResult<String> uploadDetailPhotos(
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
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/getDetailPhotos")
	@Auth(ClientType.WEB)
	public ApiResult<List<CommonDamagePhoto>> getDetailPhotos(@RequestBody ApiParam<Long> param) {

		try {
			Long damageDetailId = param.getData();
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDetailId(damageDetailId);
			return ApiResult.success(damagePhotos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/removePhoto")
	@Auth(ClientType.WEB)
	public ApiResult<String> deletePhoto(@RequestBody ApiParam<Long> param) {

		try {
			Long damagePhotoId = param.getData();
			damagePhotoService.deleteById(damagePhotoId);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/putPartPhoto")
	@Auth(ClientType.WEB)
	public ApiResult<String> putPartPhoto(@RequestBody ApiParam<CommonDamagePhoto> param) {

		try {
			CommonDamagePhoto damagePhoto = param.getData();
			damagePhotoService.updateSelective(damagePhoto);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}

	@PostMapping(value = "/getListPhoto")
	@Auth(ClientType.WEB)
	public ApiResult<List<CommonDamagePhoto>> getPhotoList(@RequestBody ApiParam<Long> param) {

		try {
			Long damageDetailId = param.getData();
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDetailId(damageDetailId);
			return ApiResult.success(damagePhotos);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999",e.getMessage());
		}
	}
}
