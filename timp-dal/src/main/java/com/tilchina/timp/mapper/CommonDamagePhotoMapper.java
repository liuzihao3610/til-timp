package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CommonDamagePhoto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 质损照片
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CommonDamagePhotoMapper extends BaseMapper<CommonDamagePhoto> {

	List<Long> selectDamagePhotoIdByDamageId(Long damageId);
	List<CommonDamagePhoto> selectDamagePhotoByDamageId(Long damageId);
	List<CommonDamagePhoto> selectDamagePhotoByDamageDetailId(Long damageDetailId);
	List<Long> selectDamagePhotoIdByDamageDetailId(Long damageDetailId);
}
