package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.CityAreaDetail;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;

/**
* 虚拟城市区域明细
*
* @version 1.0.0
* @author WangShengguang
*/
@Repository
public interface CityAreaDetailMapper extends BaseMapper<CityAreaDetail> {

    void deleteByPrimaryKey(List<CityAreaDetail> details);

    int deleteDetailsByCityAreaId(Long cityAreaId);

    void updateByPrimaryKeySelective(List<CityAreaDetail> details);

    List<CityAreaDetail> selectByCityAreaId(Long cityAreaId);
}
