package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CarType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 商品车类别档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface CarTypeMapper extends BaseMapper<CarType> {

	void deleteList(int[] data);

	List<CarType> getCarTypeName(Map<String, Object> map);

	List<CarType> getReferenceList(Map<String, Object> map);

	List<CarType> getList(Map<String, Object> map);

	Long queryIdByName(String typeName);

	List<Long> queryByName(@Param("carTypeName") String carTypeName);
}
