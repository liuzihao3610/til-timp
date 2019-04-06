package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CorpRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* 公司关系表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CorpRelationMapper extends BaseMapper<CorpRelation> {

	void physicalDelete(@Param("corpId") Long corpId, @Param("adsCorpId") Long adsCorpId);
}
