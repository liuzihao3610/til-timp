package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.CityType;
import com.tilchina.timp.model.City;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
* 省市区档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface CityService extends BaseService<City> {

	void deleteList(int[] data);

	PageInfo<City> getReferenceList(Map<String, Object> map,int pageNum, int pageSize);

	List<City> getFirstLevel();
	List<City> getCityList(Long data);

	Long queryIdByName(String cityName);

	List<Long> queryByName(String cityName, CityType cityType);

	List<City>  queryIdByNames(List<String> cityNames);

	Workbook exportExcel() throws Exception;
}
