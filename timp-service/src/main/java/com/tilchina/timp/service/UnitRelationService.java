package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.UnitRelation;

import java.util.List;
import java.util.Map;

/**
* 公司收发货单位档案
*
* @version 1.0.0
* @author XiaHong
*/
public interface UnitRelationService extends BaseService<UnitRelation> {

    List<UnitRelation> queryByUnitId(Long unitId);

    List<UnitRelation> queryByCorpId(Long corpId);

    void adds(Map<String,Object> map);

    List<UnitRelation> queryByCorpId(Map<String,Object> data);

    List<UnitRelation> queryByadsCorpIds(List<Long> adsCorpIds);

}
