package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CommonDamage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 通用质损管理主表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CommonDamageService extends BaseService<CommonDamage> {

	CommonDamage getByCarVin(String carVin);

	void appAdd(CommonDamage commonDamage);

	void uploadDamagePhotos(Long damageId, MultipartFile[] files, Integer[] photoType, String[] photoDesc);

	void addDamageWithPhotos(CommonDamage damage, MultipartFile[] files, Integer[] photoType, String[] photoDesc) throws Exception;

	Long appAddDamageWithPhotos(CommonDamage damage, MultipartFile[] files, Integer[] photoType, String[] photoDesc) throws Exception;

	List<CommonDamage> appQueryList(Map<String, Object> map);

	List<CommonDamage> queryByIds(Map<String, Object> map) throws Exception;
}
