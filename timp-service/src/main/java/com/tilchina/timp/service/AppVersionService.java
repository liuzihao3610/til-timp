package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.AppVersion;
import org.springframework.web.multipart.MultipartFile;

/**
* APP版本升级表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface AppVersionService extends BaseService<AppVersion> {

	void uploadNewVersion(String appVersion, Integer appType, String appUpdateLog, Integer appConstraint, MultipartFile appFile) throws Exception;

	String getNewVersion(String appVersion, Integer appType);

	AppVersion queryLatestVersion(Integer appType);
}
