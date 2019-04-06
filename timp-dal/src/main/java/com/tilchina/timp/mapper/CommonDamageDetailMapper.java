package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CommonDamageDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 通用质损管理子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CommonDamageDetailMapper extends BaseMapper<CommonDamageDetail> {

	CommonDamageDetail getByPrimaryKey(Long damageDetailId);
	List<Long> selectDamageDetailIdByDamageId(Long damageId);
	List<CommonDamageDetail> selectDetailsByDamageId(Long damageId);
}
