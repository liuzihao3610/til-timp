package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.CheckPoint;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
* 检查站
*
* @version 1.0.0
* @author LiShuqi
*/
public interface CheckPointService extends BaseService<CheckPoint> {

	void deleteList(int[] data);

	PageInfo<CheckPoint> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<CheckPoint> getList(Map<String, Object> data, int pageNum, int pageSize);

	void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;
}
