package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CommonDamageDetail;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


/**
* 通用质损管理子表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CommonDamageDetailService extends BaseService<CommonDamageDetail> {

	List<Long> selectDamageDetailIdByDamageId(Long damageId);
	List<CommonDamageDetail> selectDetailsByDamageId(Long damageId);

	void addDamageDetailWithPhotos(CommonDamageDetail damageDetail, MultipartFile[] files, Integer[] photoType, String[] photoDesc);
	void uploadDamageDetailPhotos(Long damageId, Long damageDetailId, MultipartFile[] files, Integer[] photoType, String[] photoDesc);
}
