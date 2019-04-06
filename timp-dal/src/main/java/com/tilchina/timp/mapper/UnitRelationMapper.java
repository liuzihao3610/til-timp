package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.UnitRelation;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* 公司收发货单位档案
*
* @version 1.0.0
* @author XiaHong
*/
@Repository
public interface UnitRelationMapper extends BaseMapper<UnitRelation> {

    List<UnitRelation> selectByUnitId(@Param("unitId") Long unitId);

    List<UnitRelation> selectByCorpId(@Param("adsCorpId") Long adsCorpId);

    List<UnitRelation> selectByCorpIdAndKey(Map<String,Object> data);

    List<UnitRelation> selectByadsCorpIds(@Param("adsCorpIds") List<Long> adsCorpIds);

}
