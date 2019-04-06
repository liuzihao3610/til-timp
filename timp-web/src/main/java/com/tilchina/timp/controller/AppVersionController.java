package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.AppVersion;
import com.tilchina.timp.service.AppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
* APP版本升级表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/appversion"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AppVersionController extends BaseControllerImpl<AppVersion>{

	@Autowired
	private AppVersionService appVersionService;
	
	@Override
	protected BaseService<AppVersion> getService() {
		return appVersionService;
	}

	@PostMapping("uploadNewVersion")
	@Auth(ClientType.WEB)
	public ApiResult<String> uploadNewVersion(@RequestParam("appVersion") String appVersion,
	                                          @RequestParam("appType") Integer appType,
	                                          @RequestParam("appUpdateLog") String appUpdateLog,
	                                          @RequestParam("appConstraint") Integer appConstraint,
	                                          @RequestParam("appFile") MultipartFile appFile) {

		try {
			appVersionService.uploadNewVersion(appVersion, appType, appUpdateLog, appConstraint, appFile);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@GetMapping("getNewVersion")
	public ApiResult<String> getNewVersion(@RequestParam("version") String appVersion, @RequestParam("type") Integer appType) {

		try {
			String latestVersion = appVersionService.getNewVersion(appVersion, appType);
			return ApiResult.success(latestVersion);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
