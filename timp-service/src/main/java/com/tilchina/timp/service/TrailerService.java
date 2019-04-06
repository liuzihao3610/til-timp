package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Trailer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 挂车型号档案
*
* @version 1.1.0
* @author XiaHong
*/
public interface TrailerService extends BaseService<Trailer> {

	void deleteList(int[] data);

	void updateBillStatus(Long trailerId, String path);

	PageInfo<Trailer> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	void removeDate(Long trailerId);

	void unaudit(Long data);

	void audit(Long data);
	
    List<Trailer> queryByList(List<Long> trailerIds);

	void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;
}
