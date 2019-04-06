package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSON;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.mapper.AppVersionMapper;
import com.tilchina.timp.model.AppUpdate;
import com.tilchina.timp.model.AppVersion;
import com.tilchina.timp.service.AppVersionService;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.util.RegexUtil;
import com.tilchina.timp.util.SizeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
* APP版本升级表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class AppVersionServiceImpl extends BaseServiceImpl<AppVersion> implements AppVersionService {

	@Autowired
    private AppVersionMapper appversionmapper;
	
	@Override
	protected BaseMapper<AppVersion> getMapper() {
		return appversionmapper;
	}

	@Override
	public void uploadNewVersion(String appVersion, Integer appType, String appUpdateLog, Integer appConstraint, MultipartFile appFile) throws Exception {

		Environment env = Environment.getEnv();

		String appPath;
		String appMd5;
		Long appSize;
		try {
			appSize = appFile.getSize();
			appPath = FileUtil.uploadApk(appFile);
			appMd5 = DigestUtils.md5Hex(new FileInputStream(new File(appPath)));

			AppVersion model = new AppVersion();
			model.setAppVersion(appVersion);
			model.setAppType(appType);
			model.setAppPath(appPath);
			model.setAppUpdateLog(appUpdateLog);
			model.setAppSize(appSize);
			model.setAppMd5(appMd5);
			model.setAppConstraint(appConstraint);
			model.setCreator(env.getUser().getUserId());
			model.setCreateDate(new Date());
			model.setCorpId(env.getUser().getCorpId());
			model.setFlag(0);
			add(model);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	protected StringBuilder checkNewRecord(AppVersion appversion) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "appVersion", "APP版本号", appversion.getAppVersion(), 20));
        s.append(CheckUtils.checkString("NO", "appPath", "APP下载路径", appversion.getAppPath(), 100));
        s.append(CheckUtils.checkString("YES", "appUpdateLog", "APP更新内容", appversion.getAppUpdateLog(), 200));
        s.append(CheckUtils.checkLong("NO", "appSize", "APP更新文件大小", appversion.getAppSize(), 10));
        s.append(CheckUtils.checkString("NO", "appMd5", "APP文件MD5", appversion.getAppMd5(), 50));
        s.append(CheckUtils.checkInteger("YES", "appConstraint", "是否强制更新", appversion.getAppConstraint(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", appversion.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", appversion.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(AppVersion appversion) {
        StringBuilder s = checkNewRecord(appversion);
        s.append(CheckUtils.checkPrimaryKey(appversion.getAppId()));
		return s;
	}

	@Override
	public String getNewVersion(String appVersion, Integer appType) {

		if (!RegexUtil.validate("regex.app.version", appVersion)) {
			throw new RuntimeException("版本号格式有误，请检查后重试。");
		}

		AppUpdate updateVersion = new AppUpdate();
		AppVersion latestVersion = queryLatestVersion(appType);
		if (latestVersion == null) {
			String json = "{\"update\":\"No\"}";
			return json;
		}

		Integer[] source = RegexUtil.extractDigits(appVersion);
		Integer[] target = RegexUtil.extractDigits(latestVersion.getAppVersion());

		int updateConfig = NumberUtils.toInt(PropertiesUtils.getString("app.update.config"));
		if (target[0] - source[0] >= updateConfig) {
			updateVersion.setAppUpdate("Yes");
			updateVersion.setAppVersion(latestVersion.getAppVersion());
			updateVersion.setAppPath(convertAppPath(latestVersion.getAppPath()));
			updateVersion.setAppUpdateLog(latestVersion.getAppUpdateLog());
			updateVersion.setAppSize(SizeConverter.BTrim.convert(latestVersion.getAppSize()));
			updateVersion.setAppMd5(latestVersion.getAppMd5());
			updateVersion.setAppConstraint(true);

			String json = JSON.toJSONString(updateVersion);
			return json;
		}

		if (target[0] >= source[0] && target[1] >= source[1] && target[2] > source[2]) {
			updateVersion.setAppUpdate("Yes");
			updateVersion.setAppVersion(latestVersion.getAppVersion());
			updateVersion.setAppPath(convertAppPath(latestVersion.getAppPath()));
			updateVersion.setAppUpdateLog(latestVersion.getAppUpdateLog());
			updateVersion.setAppSize(SizeConverter.BTrim.convert(latestVersion.getAppSize()));
			updateVersion.setAppMd5(latestVersion.getAppMd5());
			updateVersion.setAppConstraint(latestVersion.getAppConstraint() != 0);

			String json = JSON.toJSONString(updateVersion);
			return json;
		}

		if (target[0] <= source[0] && target[1] <= source[1] && target[2] <= source[2]) {
			String json = "{\"update\":\"No\"}";
			return json;
		}

		if (latestVersion.getAppVersion().equals(appVersion)) {
			String json = "{\"update\":\"No\"}";
			return json;
		}

		String json = "{\"update\":\"No\"}";
		return json;
	}

	@Override
	public AppVersion queryLatestVersion(Integer appType) {
		try {
			AppVersion appVersion = appversionmapper.queryLatestVersion(appType);
			return appVersion;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	private String convertAppPath(String appPath) {
		return String.format("http://10.8.12.123/%s", appPath.substring(6, appPath.length()));
	}
}
