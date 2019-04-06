package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Brand;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 品牌档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface BrandService extends BaseService<Brand> {

    Map<Long,Brand> queryMap();

    void deleteList(int[] brandIds);
	PageInfo<Brand> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);
	Brand getByName(String brandName);

    List<Brand> queryByNames(Set<String> names);

	PageInfo<Brand> getList(Map<String, Object> data, int pageNum, int pageSize);

	Long queryIdByName(String brandName) throws Exception;

	List<Long> queryByName(String carBrandName);

	void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;
}
