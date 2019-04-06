package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.OilPrice;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
* 油价档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface OilPriceService extends BaseService<OilPrice> {

	void deleteList(int[] data);

	PageInfo<OilPrice> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;

}
