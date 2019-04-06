package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.City;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 省市区档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface CityMapper extends BaseMapper<City> {

	void deleteList(int[] data);

	List<City> getReferenceList(Map<String, Object> map);

	List<City> getCityList(Long upCityId);

	List<City> getFirstLevel();

	void updateSelective(City record);

	Long queryIdByName(String cityName);

	List<Long> queryByName(Map<String, Object> params);

	List<City> selectByNames(@Param("cityNames")List<String> cityNames);

}
