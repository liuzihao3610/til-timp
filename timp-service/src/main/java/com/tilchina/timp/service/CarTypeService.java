package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CarType;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 商品车类别档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface CarTypeService extends BaseService<CarType> {

    Map<String,CarType> queryMap();

    void deleteList(int[] data);

	PageInfo<CarType> getCarTypeName(Map<String, Object> map, int pageNum, int pageSize);

	PageInfo<CarType> getReferenceList(Map<String, Object> data, int pageNum, int pageSize);

	Long queryIdByName(String typeName);

	List<Long> queryByName(String carTypeName);

	void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;
}
