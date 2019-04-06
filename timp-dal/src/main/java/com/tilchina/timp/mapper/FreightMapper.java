package com.tilchina.timp.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.Freight;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 运价管理
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface FreightMapper extends BaseMapper<Freight> {

	void deleteList(int[] freightId);

	void updateCheckDate(Long freightId);

	List<Freight> getReferenceList(Map<String, Object> map);

	Freight getFreight(Date orderDate, Long seCityId, Long reCityId, Long brandId, Long carTypeId);

	List<Freight> getList(Map<String, Object> map);

	Freight getOneFreight(Map<String,Object> map);
}
