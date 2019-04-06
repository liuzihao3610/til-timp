package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CommonDamage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 通用质损管理主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CommonDamageMapper extends BaseMapper<CommonDamage> {

	CommonDamage getByCarVin(String carVin);

	List<CommonDamage> queryByIds(Map<String, Object> map);
}
