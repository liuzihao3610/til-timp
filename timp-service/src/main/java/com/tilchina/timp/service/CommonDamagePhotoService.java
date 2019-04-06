package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CommonDamagePhoto;

import java.util.List;

/**
* 质损照片
*
* @version 1.0.0
* @author XueYuSong
*/
public interface CommonDamagePhotoService extends BaseService<CommonDamagePhoto> {

	List<Long> selectPhotoIdByDamageId(Long damageId);
	List<CommonDamagePhoto> selectPhotosByDamageId(Long damageId);
	List<CommonDamagePhoto> selectPhotosByDetailId(Long damageDetailId);
	List<Long> selectPhotoIdByDamageDetailId(Long damageDetailId);
}
