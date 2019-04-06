package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.OilDepot;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 油库管理
*
* @version 1.0.0
* @author LiushuQi
*/
@Repository
public interface OilDepotMapper extends BaseMapper<OilDepot> {

	List<OilDepot> getList(Map<String, Object> map);

}
