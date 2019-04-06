package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CityArea;
import com.tilchina.timp.model.CityAreaDetail;

import java.util.List;
import java.util.Map;

/**
* 虚拟城市区域
*
* @version 1.0.0
* @author WangShengguang
*/
public interface CityAreaService {

    CityArea queryById(Long id);

    PageInfo<CityArea> queryList(Map<String, Object> map, int pageNum, int pageSize);

    List<CityAreaDetail> queryDetails(Long cityAreaId);

    List<CityArea> queryList(Map<String, Object> map);

    void add(CityArea record);

    void add(List<CityArea> records);

    void updateSelective(CityArea record);

    void deleteById(Long id);
}
